package com.filerhythm.app.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import com.filerhythm.app.data.model.FileCategory
import com.filerhythm.app.data.model.FileItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * File repository — wraps both MediaStore (for media) and DocumentFile/SAF (for full tree).
 *
 * Strategy:
 * - On Android 10+: use MediaStore for media (images, video, audio) — scoped, fast.
 * - For non-media files (Documents, APKs, Archives): use java.io.File on external storage
 *   when MANAGE_EXTERNAL_STORAGE is granted (Android 11+) or for app-specific dirs.
 * - For SAF tree URIs granted by user (Android 13+ photo picker / SAF): use DocumentFile.
 *
 * This dual approach matches how modern Android file managers work.
 */
class FileRepository(private val context: Context) {

    /**
     * Get all media files of a given category via MediaStore.
     * Returns a Flow-like list — caller handles empty/error states.
     */
    suspend fun getMediaFiles(category: FileCategory): List<FileItem> = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val collection: Uri
        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.RELATIVE_PATH,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.MediaColumns.MIME_TYPE
        )

        when (category) {
            FileCategory.IMAGES -> {
                collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                else
                    @Suppress("DEPRECATION") MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            FileCategory.VIDEOS -> {
                collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                else
                    @Suppress("DEPRECATION") MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }
            FileCategory.AUDIO -> {
                collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                else
                    @Suppress("DEPRECATION") MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            FileCategory.DOWNLOADS -> {
                collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI
                else
                    @Suppress("DEPRECATION") MediaStore.Files.getContentUri("external")
            }
            else -> {
                // For documents, APKs, archives — use Files collection
                collection = MediaStore.Files.getContentUri("external")
            }
        }

        val selection = when (category) {
            FileCategory.APKS -> "${MediaStore.Files.FileColumns.MIME_TYPE} = 'application/vnd.android.package-archive'"
            FileCategory.ARCHIVES -> buildString {
                ARCHIVE_EXTS.forEachIndexed { i, ext ->
                    if (i > 0) append(" OR ")
                    append(MediaStore.MediaColumns.DISPLAY_NAME).append(" LIKE '%.").append(ext).append("'")
                }
            }
            FileCategory.DOCUMENTS -> buildString {
                DOC_EXTS.forEachIndexed { i, ext ->
                    if (i > 0) append(" OR ")
                    append(MediaStore.MediaColumns.DISPLAY_NAME).append(" LIKE '%.").append(ext).append("'")
                }
            }
            else -> null
        }

        val result = mutableListOf<FileItem>()
        try {
            resolver.query(
                collection, projection, selection, null,
                "${MediaStore.MediaColumns.DATE_MODIFIED} DESC"
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                val pathCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.RELATIVE_PATH)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
                val modCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED)
                val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val name = cursor.getString(nameCol) ?: continue
                    val path = cursor.getString(pathCol) ?: ""
                    val size = cursor.getLong(sizeCol)
                    val mod = cursor.getLong(modCol) * 1000
                    val mime = cursor.getString(mimeCol) ?: "*/*"

                    val itemUri = ContentUris.withAppendedId(collection, id)
                    result.add(
                        FileItem(
                            uri = itemUri,
                            name = name,
                            path = path,
                            isDirectory = false,
                            size = size,
                            lastModified = mod,
                            mimeType = mime,
                            fileCategory = category
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        result
    }

    /**
     * List files in a directory given a java.io.File path.
     * Used for app-specific external storage dirs and (when MANAGE_EXTERNAL_STORAGE is granted)
     * for the full external storage tree.
     */
    suspend fun listFilesInDirectory(dir: File, showHidden: Boolean = false): List<FileItem> = withContext(Dispatchers.IO) {
        if (!dir.exists() || !dir.isDirectory) return@withContext emptyList()
        dir.listFiles()
            ?.filter { showHidden || !it.name.startsWith(".") }
            ?.sortedWith(compareByDescending<File> { it.isDirectory }.thenBy { it.name.lowercase() })
            ?.map { f ->
                FileItem(
                    uri = Uri.fromFile(f),
                    name = f.name,
                    path = f.absolutePath,
                    isDirectory = f.isDirectory,
                    size = if (f.isFile) f.length() else 0L,
                    lastModified = f.lastModified(),
                    mimeType = guessMime(f.name),
                    fileCategory = FileCategory.fromNameAndMime(f.name, guessMime(f.name), f.isDirectory),
                    childCount = if (f.isDirectory) (f.list()?.size ?: 0) else 0
                )
            } ?: emptyList()
    }

    /**
     * List files via SAF DocumentFile (for SAF tree URIs granted via ACTION_OPEN_DOCUMENT_TREE).
     */
    suspend fun listDocumentFiles(treeUri: Uri, showHidden: Boolean = false): List<FileItem> = withContext(Dispatchers.IO) {
        val root = DocumentFile.fromTreeUri(context, treeUri) ?: return@withContext emptyList()
        val result = mutableListOf<FileItem>()
        root.listFiles().forEach { doc ->
            val name = doc.name ?: return@forEach
            if (!showHidden && name.startsWith(".")) return@forEach
            result.add(
                FileItem(
                    uri = doc.uri,
                    name = name,
                    path = doc.uri.toString(),
                    isDirectory = doc.isDirectory,
                    size = doc.length(),
                    lastModified = doc.lastModified(),
                    mimeType = doc.type ?: guessMime(name),
                    fileCategory = FileCategory.fromNameAndMime(name, doc.type ?: "", doc.isDirectory),
                    childCount = if (doc.isDirectory) 0 else 0
                )
            )
        }
        result
    }

    /**
     * Returns storage stats: total / used / free.
     */
    suspend fun getStorageStats(): StorageStats = withContext(Dispatchers.IO) {
        val root = Environment.getExternalStorageDirectory()
        val stat = android.os.StatFs(root.absolutePath)
        val total = stat.totalBytes
        val free = stat.availableBytes
        StorageStats(total = total, used = total - free, free = free)
    }

    private fun guessMime(name: String): String {
        val ext = name.substringAfterLast('.', "").lowercase()
        return when (ext) {
            "jpg","jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            "bmp" -> "image/bmp"
            "mp4" -> "video/mp4"
            "mkv" -> "video/x-matroska"
            "avi" -> "video/x-msvideo"
            "mov" -> "video/quicktime"
            "mp3" -> "audio/mpeg"
            "flac" -> "audio/flac"
            "wav" -> "audio/wav"
            "ogg" -> "audio/ogg"
            "pdf" -> "application/pdf"
            "txt" -> "text/plain"
            "md" -> "text/markdown"
            "json" -> "application/json"
            "xml" -> "application/xml"
            "html","htm" -> "text/html"
            "apk" -> "application/vnd.android.package-archive"
            "zip" -> "application/zip"
            "rar" -> "application/x-rar-compressed"
            "7z" -> "application/x-7z-compressed"
            "tar" -> "application/x-tar"
            "gz" -> "application/gzip"
            "doc","docx" -> "application/msword"
            "xls","xlsx" -> "application/vnd.ms-excel"
            "ppt","pptx" -> "application/vnd.ms-powerpoint"
            else -> "*/*"
        }
    }

    companion object {
        private val ARCHIVE_EXTS = setOf("zip","rar","7z","tar","gz","bz2","xz","tgz")
        private val DOC_EXTS = setOf("pdf","doc","docx","xls","xlsx","ppt","pptx","txt","md","csv","json","xml","html")
    }
}

data class StorageStats(val total: Long, val used: Long, val free: Long)

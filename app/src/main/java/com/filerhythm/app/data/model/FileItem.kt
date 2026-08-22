package com.filerhythm.app.data.model

import android.net.Uri
import androidx.compose.ui.graphics.Color

/**
 * A single file or folder entry shown in the file browser.
 * Backed by either a MediaStore entry (for media types) or a DocumentFile (via SAF).
 */
data class FileItem(
    val uri: Uri,
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long,
    val lastModified: Long,
    val mimeType: String,
    val fileCategory: FileCategory = FileCategory.fromNameAndMime(name, mimeType, isDirectory),
    val childCount: Int = 0
) {
    val extension: String
        get() = if (isDirectory) "" else name.substringAfterLast('.', "").lowercase()

    val displaySize: String
        get() = formatSize(size)
}

enum class FileCategory(val label: String) {
    IMAGES("Images"),
    VIDEOS("Videos"),
    AUDIO("Audio"),
    DOCUMENTS("Documents"),
    APKS("APKs"),
    ARCHIVES("Archives"),
    DOWNLOADS("Downloads"),
    OTHERS("Others"),
    FOLDER("Folder");

    companion object {
        fun fromNameAndMime(name: String, mime: String, isDir: Boolean): FileCategory {
            if (isDir) return FOLDER
            val ext = name.substringAfterLast('.', "").lowercase()
            return when {
                ext in IMAGE_EXTS || mime.startsWith("image/") -> IMAGES
                ext in VIDEO_EXTS || mime.startsWith("video/") -> VIDEOS
                ext in AUDIO_EXTS || mime.startsWith("audio/") -> AUDIO
                ext in DOC_EXTS || mime.startsWith("application/pdf") ||
                    mime.startsWith("application/msword") ||
                    mime.startsWith("application/vnd.openxmlformats") ||
                    mime.startsWith("application/vnd.oasis.opendocument") ||
                    mime.startsWith("text/") -> DOCUMENTS

                ext == "apk" -> APKS
                ext in ARCHIVE_EXTS -> ARCHIVES
                else -> OTHERS
            }
        }

        private val IMAGE_EXTS = setOf("jpg","jpeg","png","gif","webp","bmp","heic","heif","tiff","svg")
        private val VIDEO_EXTS = setOf("mp4","mkv","avi","mov","webm","flv","wmv","3gp","mpeg","mpg")
        private val AUDIO_EXTS = setOf("mp3","flac","aac","ogg","oga","wav","m4a","opus","amr","mid","midi")
        private val DOC_EXTS = setOf("pdf","doc","docx","xls","xlsx","ppt","pptx","odt","ods","odp","txt","md","rtf","csv","json","xml","html","htm")
        private val ARCHIVE_EXTS = setOf("zip","rar","7z","tar","gz","bz2","xz","tgz")
    }
}

/**
 * Storage entry — represents a quick-access location (Downloads, DCIM, etc.)
 */
data class StorageLocation(
    val name: String,
    val path: String,
    val icon: FileIcon,
    val category: FileCategory
)

enum class FileIcon {
    FOLDER, IMAGE, VIDEO, AUDIO, DOCUMENT, APK, ARCHIVE, DOWNLOAD, OTHER, INTERNAL_STORAGE
}

fun formatSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val size = bytes.toDouble()
    var digitGroups = (Math.log10(size) / Math.log10(1024.0)).toInt()
    if (digitGroups >= units.size) digitGroups = units.size - 1
    return String.format("%.1f %s", size / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}

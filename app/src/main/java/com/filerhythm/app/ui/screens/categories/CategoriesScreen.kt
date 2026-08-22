package com.filerhythm.app.ui.screens.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.FolderZip
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.filerhythm.app.R
import com.filerhythm.app.data.model.FileCategory
import com.filerhythm.app.data.repository.FileRepository
import com.filerhythm.app.ui.theme.AppSettings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    settings: AppSettings,
    onCategoryClick: (FileCategory) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { FileRepository(context) }

    val cats = listOf(
        CategoryEntry(FileCategory.IMAGES, Icons.Rounded.Photo, "Photos & Pictures"),
        CategoryEntry(FileCategory.VIDEOS, Icons.Rounded.Movie, "Movies & Video clips"),
        CategoryEntry(FileCategory.AUDIO, Icons.Rounded.MusicNote, "Music & Audio"),
        CategoryEntry(FileCategory.DOCUMENTS, Icons.Rounded.Description, "PDF, Docs, Sheets"),
        CategoryEntry(FileCategory.APKS, Icons.Rounded.Android, "App packages"),
        CategoryEntry(FileCategory.ARCHIVES, Icons.Rounded.FolderZip, "Zip, Rar, 7z"),
        CategoryEntry(FileCategory.DOWNLOADS, Icons.Rounded.Download, "Downloaded files")
    )

    val counts = remember { mutableStateOf<Map<FileCategory, Int>>(emptyMap()) }
    LaunchedEffect(Unit) {
        scope.launch {
            val m = mutableMapOf<FileCategory, Int>()
            cats.forEach { c ->
                runCatching { repo.getMediaFiles(c.category).size }.onSuccess { m[c.category] = it }
            }
            counts.value = m
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(
            text = stringResource(R.string.nav_categories),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cats, key = { it.category.name }) { c ->
                CategoryCardBig(
                    entry = c,
                    count = counts.value[c.category] ?: 0,
                    onClick = { onCategoryClick(c.category) }
                )
            }
        }
    }
}

@Composable
private fun CategoryCardBig(
    entry: CategoryEntry,
    count: Int,
    onClick: () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(150.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    entry.icon,
                    contentDescription = entry.category.label,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column {
                Text(
                    text = entry.category.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${count} files",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private data class CategoryEntry(
    val category: FileCategory,
    val icon: ImageVector,
    val subtitle: String
)

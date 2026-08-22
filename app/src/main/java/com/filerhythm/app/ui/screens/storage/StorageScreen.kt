package com.filerhythm.app.ui.screens.storage

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.filerhythm.app.R
import com.filerhythm.app.data.model.FileCategory
import com.filerhythm.app.data.model.formatSize
import com.filerhythm.app.data.repository.FileRepository
import com.filerhythm.app.data.repository.StorageStats
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.FolderZip
import androidx.compose.material.icons.rounded.Download
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { FileRepository(context) }
    var stats by remember { mutableStateOf<StorageStats?>(null) }
    var categorySizes by remember { mutableStateOf<Map<FileCategory, Long>>(emptyMap()) }

    LaunchedEffect(Unit) {
        scope.launch {
            stats = repo.getStorageStats()
            val m = mutableMapOf<FileCategory, Long>()
            listOf(
                FileCategory.IMAGES, FileCategory.VIDEOS, FileCategory.AUDIO,
                FileCategory.DOCUMENTS, FileCategory.APKS, FileCategory.ARCHIVES,
                FileCategory.DOWNLOADS
            ).forEach { c ->
                runCatching {
                    val files = repo.getMediaFiles(c)
                    m[c] = files.sumOf { it.size }
                }
            }
            categorySizes = m
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.nav_storage),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        item {
            StorageBigCard(stats = stats)
        }
        item {
            Text(
                text = stringResource(R.string.storage_by_category),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        items(categorySizes.entries.toList().sortedByDescending { it.value }) { entry ->
            CategorySizeRow(entry.key, entry.value, stats?.total ?: 1)
        }
    }
}

@Composable
private fun StorageBigCard(stats: StorageStats?) {
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.home_internal_storage),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(16.dp))
            if (stats == null) {
                Text(
                    text = stringResource(R.string.loading),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatColumn(stringResource(R.string.storage_used), formatSize(stats.used), MaterialTheme.colorScheme.primary)
                    StatColumn(stringResource(R.string.storage_free), formatSize(stats.free), MaterialTheme.colorScheme.tertiary)
                    StatColumn(stringResource(R.string.storage_total), formatSize(stats.total), MaterialTheme.colorScheme.secondary)
                }
                Spacer(Modifier.height(16.dp))
                val pct = if (stats.total > 0) (stats.used.toFloat() / stats.total.toFloat()) else 0f
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(pct)
                            .height(12.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CategorySizeRow(category: FileCategory, size: Long, total: Long) {
    val (icon, color) = when (category) {
        FileCategory.IMAGES -> Icons.Rounded.Photo to MaterialTheme.colorScheme.primary
        FileCategory.VIDEOS -> Icons.Rounded.Movie to MaterialTheme.colorScheme.tertiary
        FileCategory.AUDIO -> Icons.Rounded.MusicNote to MaterialTheme.colorScheme.secondary
        FileCategory.DOCUMENTS -> Icons.Rounded.Description to MaterialTheme.colorScheme.primaryContainer
        FileCategory.APKS -> Icons.Rounded.Android to MaterialTheme.colorScheme.tertiaryContainer
        FileCategory.ARCHIVES -> Icons.Rounded.FolderZip to MaterialTheme.colorScheme.errorContainer
        FileCategory.DOWNLOADS -> Icons.Rounded.Download to MaterialTheme.colorScheme.secondaryContainer
        else -> Icons.Rounded.Download to MaterialTheme.colorScheme.surfaceVariant
    }
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = category.label, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = formatSize(size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            val pct = if (total > 0) (size.toFloat() / total.toFloat()) else 0f
            Text(
                text = "${(pct * 100).toInt()}%",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

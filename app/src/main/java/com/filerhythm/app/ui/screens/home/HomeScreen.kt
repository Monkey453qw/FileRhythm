package com.filerhythm.app.ui.screens.home

import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.FolderZip
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.InternalDrive
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.filerhythm.app.R
import com.filerhythm.app.data.model.FileCategory
import com.filerhythm.app.data.model.FileIcon
import com.filerhythm.app.data.model.formatSize
import com.filerhythm.app.data.repository.FileRepository
import com.filerhythm.app.data.repository.StorageStats
import com.filerhythm.app.ui.theme.AppSettings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    settings: AppSettings,
    onNavigateToSearch: () -> Unit,
    onNavigateToCategory: (FileCategory) -> Unit,
    onNavigateToPath: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { FileRepository(context) }
    var storageStats by remember { mutableStateOf<StorageStats?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            storageStats = repo.getStorageStats()
        }
    }

    val quickAccess = remember {
        listOf(
            QuickAccessItem(stringResource(R.string.home_internal_storage), Environment.getExternalStorageDirectory()?.absolutePath ?: "", FileIcon.INTERNAL_STORAGE),
            QuickAccessItem(stringResource(R.string.home_downloads), "${Environment.getExternalStorageDirectory()?.absolutePath}/Download", FileIcon.DOWNLOAD),
            QuickAccessItem(stringResource(R.string.home_dcim), "${Environment.getExternalStorageDirectory()?.absolutePath}/DCIM", FileIcon.FOLDER),
            QuickAccessItem(stringResource(R.string.home_music), "${Environment.getExternalStorageDirectory()?.absolutePath}/Music", FileIcon.FOLDER),
            QuickAccessItem(stringResource(R.string.home_documents), "${Environment.getExternalStorageDirectory()?.absolutePath}/Documents", FileIcon.FOLDER)
        )
    }

    val categories = listOf(
        CategoryChip(FileCategory.IMAGES, Icons.Rounded.Photo, Icons.Rounded.Photo),
        CategoryChip(FileCategory.VIDEOS, Icons.Rounded.Movie, Icons.Rounded.Movie),
        CategoryChip(FileCategory.AUDIO, Icons.Rounded.MusicNote, Icons.Rounded.MusicNote),
        CategoryChip(FileCategory.DOCUMENTS, Icons.Rounded.Description, Icons.Rounded.Description),
        CategoryChip(FileCategory.APKS, Icons.Rounded.Android, Icons.Rounded.Android),
        CategoryChip(FileCategory.ARCHIVES, Icons.Rounded.FolderZip, Icons.Rounded.FolderZip),
        CategoryChip(FileCategory.DOWNLOADS, Icons.Rounded.Download, Icons.Rounded.Download)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Hero header — app name + search
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.app_tagline),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onNavigateToSearch) {
                    Icon(
                        Icons.Rounded.Search,
                        contentDescription = stringResource(R.string.search_hint),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // Storage usage hero card
        item {
            StorageCard(stats = storageStats, onClick = onNavigateToCategory)
        }

        // Quick access chips
        item {
            Text(
                text = stringResource(R.string.home_quick_access),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(quickAccess) { qa ->
                    QuickAccessCard(item = qa, onClick = { onNavigateToPath(qa.path) })
                }
            }
        }

        // Categories
        item {
            Text(
                text = stringResource(R.string.nav_categories),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
            )
            categories.chunked(2).forEach { rowCats ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    rowCats.forEach { cat ->
                        CategoryCard(category = cat, onClick = { onNavigateToCategory(cat.category) }, modifier = Modifier.weight(1f))
                    }
                    if (rowCats.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun StorageCard(stats: StorageStats?, onClick: (FileCategory) -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val cardBrush = Brush.linearGradient(listOf(primary, tertiary))

    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.home_storage_usage),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    if (stats != null) {
                        Text(
                            text = "${formatSize(stats.used)} / ${formatSize(stats.total)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.loading),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                // Circular progress (simplified)
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(8.dp, MaterialTheme.colorScheme.surfaceContainerHighest, CircleShape)
                )
            }
            if (stats != null) {
                Spacer(Modifier.height(16.dp))
                val pct = if (stats.total > 0) (stats.used.toFloat() / stats.total.toFloat()) else 0f
                LinearProgressIndicatorM3(
                    progress = { pct },
                    brush = cardBrush,
                    modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape)
                )
            }
        }
    }
}

@Composable
private fun LinearProgressIndicatorM3(
    progress: () -> Float,
    brush: Brush,
    modifier: Modifier = Modifier
) {
    // Using M3 LinearProgressIndicator with brush via Box fallback
    val p = progress()
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(p)
                .height(10.dp)
                .clip(CircleShape)
                .background(brush)
        )
    }
}

@Composable
private fun QuickAccessCard(item: QuickAccessItem, onClick: () -> Unit) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        onClick = onClick,
        modifier = Modifier.width(140.dp).wrapContentHeight()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.Folder,
                    contentDescription = item.name,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Folder",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CategoryCard(category: CategoryChip, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    category.icon,
                    contentDescription = category.category.label,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = category.category.label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private data class QuickAccessItem(val name: String, val path: String, val icon: FileIcon)
private data class CategoryChip(val category: FileCategory, val icon: ImageVector, val selectedIcon: ImageVector)

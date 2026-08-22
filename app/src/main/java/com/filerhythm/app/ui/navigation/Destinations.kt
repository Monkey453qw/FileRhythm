package com.filerhythm.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class TopLevelDestination(
    val route: String,
    val label: String,
    val outlinedIcon: ImageVector,
    val selectedIcon: ImageVector
) {
    HOME(
        route = "home",
        label = "Home",
        outlinedIcon = Icons.Outlined.Home,
        selectedIcon = Icons.Rounded.Home
    ),
    CATEGORIES(
        route = "categories",
        label = "Categories",
        outlinedIcon = Icons.Outlined.Category,
        selectedIcon = Icons.Rounded.Category
    ),
    STORAGE(
        route = "storage",
        label = "Storage",
        outlinedIcon = Icons.Outlined.Storage,
        selectedIcon = Icons.Rounded.Storage
    ),
    SETTINGS(
        route = "settings",
        label = "Settings",
        outlinedIcon = Icons.Outlined.Settings,
        selectedIcon = Icons.Rounded.Settings
    )
}

object Routes {
    const val SEARCH = "search"
    const val FILE_BROWSER = "file_browser/{path}"
    fun fileBrowser(path: String) = "file_browser/${java.net.URLEncoder.encode(path, "UTF-8")}"
}

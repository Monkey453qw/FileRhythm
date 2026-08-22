package com.filerhythm.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.filerhythm.app.R
import com.filerhythm.app.ui.navigation.Routes
import com.filerhythm.app.ui.navigation.TopLevelDestination
import com.filerhythm.app.ui.screens.categories.CategoriesScreen
import com.filerhythm.app.ui.screens.filebrowser.FileBrowserScreen
import com.filerhythm.app.ui.screens.home.HomeScreen
import com.filerhythm.app.ui.screens.search.SearchScreen
import com.filerhythm.app.ui.screens.settings.SettingsScreen
import com.filerhythm.app.ui.screens.storage.StorageScreen
import com.filerhythm.app.ui.theme.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileRhythmApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.factory(context))
    val settings by settingsViewModel.settings.collectAsState()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val topLevelDestinations = TopLevelDestination.entries
    val isTopLevel = currentRoute in topLevelDestinations.map { it.route }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AnimatedVisibility(
                visible = isTopLevel,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                // Expressive NavigationBar (Rhythm-style — pill indicator, rounded top corners)
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    tonalElevation = 3.dp
                ) {
                    topLevelDestinations.forEach { dest ->
                        val selected = currentRoute == dest.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.outlinedIcon,
                                        contentDescription = dest.label,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = dest.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.HOME.route,
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            composable(TopLevelDestination.HOME.route) {
                HomeScreen(
                    settings = settings,
                    onNavigateToSearch = { navController.navigate(Routes.SEARCH) },
                    onNavigateToCategory = { cat ->
                        navController.navigate(Routes.fileBrowser("category:${cat.name}"))
                    },
                    onNavigateToPath = { path ->
                        navController.navigate(Routes.fileBrowser(path))
                    }
                )
            }
            composable(TopLevelDestination.CATEGORIES.route) {
                CategoriesScreen(
                    settings = settings,
                    onCategoryClick = { cat ->
                        navController.navigate(Routes.fileBrowser("category:${cat.name}"))
                    }
                )
            }
            composable(TopLevelDestination.STORAGE.route) {
                StorageScreen()
            }
            composable(TopLevelDestination.SETTINGS.route) {
                SettingsScreen(
                    settingsViewModel = settingsViewModel,
                    settings = settings
                )
            }
            composable(Routes.SEARCH) {
                SearchScreen(
                    onBack = { navController.popBackStack() },
                    onFileClick = { /* open with */ }
                )
            }
            composable(Routes.FILE_BROWSER) { backStackEntry ->
                val pathArg = backStackEntry.arguments?.getString("path") ?: ""
                FileBrowserScreen(
                    pathOrCategory = pathArg,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

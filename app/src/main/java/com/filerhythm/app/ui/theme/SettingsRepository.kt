package com.filerhythm.app.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Settings persisted across app launches.
 * Stored via DataStore — survives app reinstalls (but not "clear data").
 */
data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val showHiddenFiles: Boolean = false,
    val defaultView: ViewMode = ViewMode.LIST,
    val foldersFirst: Boolean = true,
    val sortBy: SortBy = SortBy.NAME,
    val sortDescending: Boolean = false
)

enum class ViewMode { LIST, GRID }

enum class SortBy { NAME, SIZE, MODIFIED, TYPE }

private val Context.dataStore by preferencesDataStore(name = "filerhythm_settings")

class SettingsRepository(private val context: Context) {
    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val SHOW_HIDDEN = booleanPreferencesKey("show_hidden")
        val DEFAULT_VIEW = stringPreferencesKey("default_view")
        val FOLDERS_FIRST = booleanPreferencesKey("folders_first")
        val SORT_BY = stringPreferencesKey("sort_by")
        val SORT_DESC = booleanPreferencesKey("sort_desc")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            themeMode = prefs[Keys.THEME_MODE]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM,
            dynamicColor = prefs[Keys.DYNAMIC_COLOR] ?: true,
            showHiddenFiles = prefs[Keys.SHOW_HIDDEN] ?: false,
            defaultView = prefs[Keys.DEFAULT_VIEW]?.let { runCatching { ViewMode.valueOf(it) }.getOrNull() } ?: ViewMode.LIST,
            foldersFirst = prefs[Keys.FOLDERS_FIRST] ?: true,
            sortBy = prefs[Keys.SORT_BY]?.let { runCatching { SortBy.valueOf(it) }.getOrNull() } ?: SortBy.NAME,
            sortDescending = prefs[Keys.SORT_DESC] ?: false
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) =
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    suspend fun setDynamicColor(enabled: Boolean) =
        context.dataStore.edit { it[Keys.DYNAMIC_COLOR] = enabled }
    suspend fun setShowHidden(show: Boolean) =
        context.dataStore.edit { it[Keys.SHOW_HIDDEN] = show }
    suspend fun setDefaultView(view: ViewMode) =
        context.dataStore.edit { it[Keys.DEFAULT_VIEW] = view.name }
    suspend fun setFoldersFirst(first: Boolean) =
        context.dataStore.edit { it[Keys.FOLDERS_FIRST] = first }
    suspend fun setSortBy(sort: SortBy) =
        context.dataStore.edit { it[Keys.SORT_BY] = sort.name }
    suspend fun setSortDescending(desc: Boolean) =
        context.dataStore.edit { it[Keys.SORT_DESC] = desc }
}

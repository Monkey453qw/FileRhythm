package com.filerhythm.app.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repo: SettingsRepository) : ViewModel() {
    val settings: StateFlow<AppSettings> = repo.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AppSettings()
    )

    fun setThemeMode(mode: ThemeMode) = viewModelScope.launch { repo.setThemeMode(mode) }
    fun setDynamicColor(enabled: Boolean) = viewModelScope.launch { repo.setDynamicColor(enabled) }
    fun setShowHidden(show: Boolean) = viewModelScope.launch { repo.setShowHidden(show) }
    fun setDefaultView(view: ViewMode) = viewModelScope.launch { repo.setDefaultView(view) }
    fun setFoldersFirst(first: Boolean) = viewModelScope.launch { repo.setFoldersFirst(first) }
    fun setSortBy(sort: SortBy) = viewModelScope.launch { repo.setSortBy(sort) }
    fun setSortDescending(desc: Boolean) = viewModelScope.launch { repo.setSortDescending(desc) }

    companion object {
        fun factory(context: android.content.Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repo = SettingsRepository(context.applicationContext as android.content.Context)
                    return SettingsViewModel(repo) as T
                }
            }
    }
}

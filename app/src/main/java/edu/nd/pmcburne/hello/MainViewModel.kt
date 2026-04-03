package edu.nd.pmcburne.hello

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.nd.pmcburne.hello.data.local.LocationEntity
import edu.nd.pmcburne.hello.data.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class MapsUiState(
    val locations: List<LocationEntity> = emptyList(),
    val allTags: List<String> = emptyList(),
    val selectedTag: String = "core",
    val isLoading: Boolean = true,
    val error: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LocationRepository(application.applicationContext)
    
    private val _uiState = MutableStateFlow(MapsUiState())
    val uiState: StateFlow<MapsUiState> = _uiState.asStateFlow()
    
    private val _selectedTag = MutableStateFlow("core")
    
    init {
        viewModelScope.launch {
            try {
                // Sync data on first launch
                repository.syncLocationsIfNeeded()
                
                // Get all unique tags
                val tags = repository.getAllTags()
                _uiState.value = _uiState.value.copy(allTags = tags)
                
                // Combine selected tag with filtered locations
                combine(
                    _selectedTag,
                    repository.allLocations
                ) { tag, locations ->
                    val filteredLocations = if (tag.isEmpty()) {
                        locations
                    } else {
                        locations.filter { location ->
                            location.tags.split(",").map { it.trim() }.contains(tag)
                        }
                    }
                    tag to filteredLocations
                }.collect { (tag, locations) ->
                    _uiState.value = _uiState.value.copy(
                        selectedTag = tag,
                        locations = locations,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun selectTag(tag: String) {
        _selectedTag.value = tag
    }
}

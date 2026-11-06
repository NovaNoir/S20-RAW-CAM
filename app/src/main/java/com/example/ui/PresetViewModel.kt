package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CameraPreset
import com.example.data.PresetCategory
import com.example.data.SceneFamilyInfo
import com.example.data.db.PresetEntity
import com.example.data.db.RoomPresetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PresetsUiState(
    val presets: List<CameraPreset> = emptyList(),
    val totalCount: Int = 1008,
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedCategory: PresetCategory = PresetCategory.ALL,
    val selectedFamilyId: Int? = null,
    val selectedVariantCode: String? = null,
    val showOnlyFavorites: Boolean = false,
    val favoriteIds: Set<Int> = emptySet(),
    val notesMap: Map<Int, String> = emptyMap(),
    val suggestedFamilies: List<SceneFamilyInfo> = emptyList(),
    val suggestedCategories: List<PresetCategory> = emptyList(),
    val topPredictiveFamily: SceneFamilyInfo? = null,
    val predictiveHint: String? = null,
    val activeFamilyName: String? = null
)

data class FilterParams(
    val query: String = "",
    val category: PresetCategory = PresetCategory.ALL,
    val familyId: Int? = null,
    val variantCode: String? = null,
    val onlyFavorites: Boolean = false
)

data class SearchSuggestionsResult(
    val suggestedFamilies: List<SceneFamilyInfo>,
    val suggestedCategories: List<PresetCategory>,
    val topPredictiveFamily: SceneFamilyInfo?,
    val predictiveHint: String?
)

class PresetViewModel(
    private val repository: RoomPresetRepository
) : ViewModel() {

    val sceneFamilies: List<SceneFamilyInfo> = repository.sceneFamilies

    private val _filterParams = MutableStateFlow(FilterParams())
    val filterParams = _filterParams.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    val uiState: StateFlow<PresetsUiState> = combine(
        repository.allPresets,
        _filterParams
    ) { allEntities: List<PresetEntity>, filter: FilterParams ->
        if (allEntities.isEmpty()) {
            PresetsUiState(isLoading = true)
        } else {
            val favoritesSet = allEntities.filter { it.isFavorite }.map { it.id }.toSet()
            val notes = allEntities.filter { it.userNote.isNotBlank() }.associate { it.id to it.userNote }

            val suggestions = computeSuggestions(filter.query, sceneFamilies)

            val filtered = allEntities.filter { entity ->
                val matchesCategory = (filter.category == PresetCategory.ALL) || (entity.category == filter.category.name)
                val matchesFamily = (filter.familyId == null) || (entity.familyId == filter.familyId)
                val matchesVariant = (filter.variantCode == null) || (entity.variantCode == filter.variantCode)
                val matchesFavorites = !filter.onlyFavorites || entity.isFavorite
                val matchesSearch = if (filter.query.isBlank()) {
                    true
                } else {
                    val q = filter.query.trim().lowercase()
                    val qPadded = q.removePrefix("#").trim()
                    entity.name.lowercase().contains(q) ||
                        entity.familyName.lowercase().contains(q) ||
                        entity.category.lowercase().contains(q) ||
                        categoryTitle(entity.category).contains(q) ||
                        entity.variant.lowercase().contains(q) ||
                        entity.variantCode.lowercase().contains(q) ||
                        entity.meter.lowercase().contains(q) ||
                        entity.bestUse.lowercase().contains(q) ||
                        entity.lens.lowercase().contains(q) ||
                        entity.iso.lowercase().contains(q) ||
                        entity.shutter.lowercase().contains(q) ||
                        entity.wb.lowercase().contains(q) ||
                        entity.tips.lowercase().contains(q) ||
                        entity.id.toString() == qPadded ||
                        String.format("%02d", entity.familyId) == qPadded ||
                        String.format("%03d", entity.id).contains(qPadded)
                }
                matchesCategory && matchesFamily && matchesVariant && matchesFavorites && matchesSearch
            }.map { it.toCameraPreset() }

            val activeFamily = filter.familyId?.let { fid -> sceneFamilies.find { it.id == fid } }

            PresetsUiState(
                presets = filtered,
                totalCount = allEntities.size,
                isLoading = false,
                searchQuery = filter.query,
                selectedCategory = filter.category,
                selectedFamilyId = filter.familyId,
                selectedVariantCode = filter.variantCode,
                showOnlyFavorites = filter.onlyFavorites,
                favoriteIds = favoritesSet,
                notesMap = notes,
                suggestedFamilies = suggestions.suggestedFamilies,
                suggestedCategories = suggestions.suggestedCategories,
                topPredictiveFamily = suggestions.topPredictiveFamily,
                predictiveHint = suggestions.predictiveHint,
                activeFamilyName = activeFamily?.name
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PresetsUiState(isLoading = true)
    )

    fun setSearchQuery(query: String) {
        _filterParams.value = _filterParams.value.copy(query = query)
    }

    fun setCategory(category: PresetCategory) {
        _filterParams.value = _filterParams.value.copy(category = category, familyId = null)
    }

    fun selectFamily(familyId: Int?) {
        var cat = _filterParams.value.category
        if (familyId != null) {
            val family = sceneFamilies.find { it.id == familyId }
            if (family != null && cat != PresetCategory.ALL && cat != family.category) {
                cat = family.category
            }
        }
        _filterParams.value = _filterParams.value.copy(familyId = familyId, category = cat)
    }

    fun applyFamilySuggestion(familyId: Int) {
        val family = sceneFamilies.find { it.id == familyId }
        _filterParams.value = _filterParams.value.copy(
            familyId = familyId,
            category = family?.category ?: _filterParams.value.category,
            query = ""
        )
    }

    fun autocompleteSearch(text: String) {
        _filterParams.value = _filterParams.value.copy(query = text)
    }

    fun applyCategorySuggestion(category: PresetCategory) {
        _filterParams.value = _filterParams.value.copy(
            category = category,
            familyId = null,
            query = ""
        )
    }

    fun clearFamilyScope() {
        _filterParams.value = _filterParams.value.copy(familyId = null)
    }

    fun nextFamily() {
        val current = _filterParams.value.familyId ?: 0
        val next = if (current >= 56) 1 else current + 1
        selectFamily(next)
    }

    fun previousFamily() {
        val current = _filterParams.value.familyId ?: 2
        val prev = if (current <= 1) 56 else current - 1
        selectFamily(prev)
    }

    fun setVariantCode(code: String?) {
        _filterParams.value = _filterParams.value.copy(variantCode = code)
    }

    fun toggleShowFavorites() {
        _filterParams.value = _filterParams.value.copy(onlyFavorites = !_filterParams.value.onlyFavorites)
    }

    fun toggleFavorite(presetId: Int, isCurrentFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(presetId, isCurrentFavorite)
        }
    }

    fun saveNote(presetId: Int, note: String) {
        viewModelScope.launch {
            repository.saveNote(presetId, note)
        }
    }

    fun resetFilters() {
        _filterParams.value = FilterParams()
    }

    companion object {
        private val POPULAR_FAMILY_IDS = listOf(1, 7, 9, 17, 31, 35, 41, 51)

        private fun categoryTitle(categoryName: String): String {
            return when (categoryName) {
                "DAYLIGHT" -> "daylight outdoor standard sun"
                "LANDSCAPE" -> "landscape nature outdoor scenery mountain sunrise sunset golden hour"
                "PORTRAIT" -> "portrait people face model studio skin tone"
                "NIGHT_ASTRO" -> "night astro stars milky way moon sky darkness aurora fireworks"
                "AUTOMOTIVE" -> "automotive cars speed panning rolling tracking road track vehicle"
                "ARCHITECTURE" -> "architecture interior building facade lines geometry city urban"
                "INDOOR" -> "indoor low light concert venue museum warm tungsten candle"
                "CREATIVE" -> "creative black and white b&w artistic monochrome silhouette vintage"
                else -> categoryName.lowercase()
            }
        }

        fun computeSuggestions(
            rawQuery: String,
            families: List<SceneFamilyInfo>
        ): SearchSuggestionsResult {
            val q = rawQuery.trim().lowercase()
            if (q.isBlank()) {
                val popular = POPULAR_FAMILY_IDS.mapNotNull { id -> families.find { it.id == id } }
                return SearchSuggestionsResult(
                    suggestedFamilies = popular,
                    suggestedCategories = emptyList(),
                    topPredictiveFamily = null,
                    predictiveHint = null
                )
            }

            val qPadded = q.removePrefix("#").trim()
            val qNum = qPadded.toIntOrNull()

            val scoredFamilies = families.mapNotNull { family ->
                val nameLower = family.name.lowercase()
                val bestUseLower = family.bestUse.lowercase()
                val catTitle = family.category.title.lowercase()
                val idPadded = String.format("%02d", family.id)
                val idStr = family.id.toString()

                var score = 0
                if (qNum != null && family.id == qNum) {
                    score += 100
                } else if (idPadded == qPadded || idStr == qPadded) {
                    score += 95
                } else if (idPadded.startsWith(qPadded) || idStr.startsWith(qPadded)) {
                    score += 80
                } else if (nameLower.startsWith(q)) {
                    score += 90
                } else if (nameLower.split(" ").any { it.startsWith(q) }) {
                    score += 75
                } else if (nameLower.contains(q)) {
                    score += 55
                } else if (catTitle.contains(q)) {
                    score += 40
                } else if (bestUseLower.contains(q)) {
                    score += 30
                }

                if (score > 0) family to score else null
            }
            .sortedWith(compareByDescending<Pair<SceneFamilyInfo, Int>> { it.second }.thenBy { it.first.id })
            .map { it.first }

            val matchedCategories = PresetCategory.entries.filter { cat ->
                if (cat == PresetCategory.ALL) return@filter false
                val title = cat.title.lowercase()
                val name = cat.name.lowercase()
                title.contains(q) || name.contains(q) || when (cat) {
                    PresetCategory.NIGHT_ASTRO -> q in listOf("night", "astro", "sky", "star", "dark", "moon", "aurora", "milky")
                    PresetCategory.PORTRAIT -> q in listOf("port", "face", "skin", "people", "person", "model", "human")
                    PresetCategory.LANDSCAPE -> q in listOf("land", "nature", "mountain", "beach", "sunset", "sun", "scenery", "gold")
                    PresetCategory.AUTOMOTIVE -> q in listOf("car", "auto", "vehicle", "track", "speed", "road", "panning")
                    PresetCategory.ARCHITECTURE -> q in listOf("arch", "build", "city", "urban", "facade", "interior")
                    PresetCategory.INDOOR -> q in listOf("in", "room", "museum", "concert", "stage", "venue", "candle")
                    PresetCategory.CREATIVE -> q in listOf("creat", "art", "bw", "black", "mono", "silh", "vintage")
                    PresetCategory.DAYLIGHT -> q in listOf("day", "sun", "noon", "bright", "outdoor")
                    else -> false
                }
            }.take(3)

            val topMatch = scoredFamilies.firstOrNull()
            val hint = if (topMatch != null && topMatch.name.lowercase().startsWith(q)) {
                topMatch.name
            } else null

            return SearchSuggestionsResult(
                suggestedFamilies = scoredFamilies.take(8),
                suggestedCategories = matchedCategories,
                topPredictiveFamily = topMatch,
                predictiveHint = hint
            )
        }
    }

    class Factory(private val repository: RoomPresetRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PresetViewModel(repository) as T
        }
    }
}

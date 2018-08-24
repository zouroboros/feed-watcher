package me.murks.podcastwatcher.activities.model

import me.murks.podcastwatcher.model.Filter
import me.murks.podcastwatcher.model.FilterType

interface FilterModel {
    val filterType: FilterType
    val parameterModel: List<ParameterModel>
}

class ParameterModel(val name: String, val type: ParameterType)

enum class ParameterType {
    STRING
}

class FeedFilterModel(): FilterModel {
    override val filterType = FilterType.FEED
    override val parameterModel = listOf(ParameterModel(FeedFilterModel.FEED_NAME_PARAMETER, ParameterType.STRING))

    companion object {
        const val FEED_NAME_PARAMETER = "feed-name"
    }
}

class ContainsFilterModel(): FilterModel {
    override val filterType = FilterType.CONTAINS
    override val parameterModel = listOf(ParameterModel(ContainsFilterModel.TEXT_PARAMETER, ParameterType.STRING))

    companion object {
        const val TEXT_PARAMETER = "text"
    }
}

object FilterModels {
    fun filterModels(filter: List<Filter>) = filter.map(this::filterModel)

    fun filterModel(filter: Filter): FilterModel {
        return when(filter.type) {
            FilterType.CONTAINS -> ContainsFilterModel()
            FilterType.FEED -> FeedFilterModel()
        }
    }
}
package me.murks.feedwatcher.model

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
    override val parameterModel = listOf(ParameterModel(FEED_NAME_PARAMETER, ParameterType.STRING))

    companion object {
        const val FEED_NAME_PARAMETER = "feed-name"
    }
}

class ContainsFilterModel(): FilterModel {
    override val filterType = FilterType.CONTAINS
    override val parameterModel = listOf(ParameterModel(TEXT_PARAMETER, ParameterType.STRING))

    companion object {
        const val TEXT_PARAMETER = "text"
    }
}

object FilterModels {

    fun filterModel(type: FilterType) : FilterModel {
        return when(type) {
            FilterType.CONTAINS -> ContainsFilterModel()
            FilterType.FEED -> FeedFilterModel()
        }
    }

    fun filterModel(filter: Filter): FilterModel {
        return filterModel(filter.type)
    }

    fun defaultParameter(type: FilterType) : List<FilterParameter> {
        val parameter = filterModel(type).parameterModel
        return parameter.map { FilterParameter(it.name, "") }
    }
}
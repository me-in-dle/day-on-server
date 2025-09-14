package com.day.on.convert

import com.day.on.recommend.type.FeedbackAction
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class ListFeedbackActionToStringConverter : AttributeConverter<List<FeedbackAction>, String> {
    override fun convertToDatabaseColumn(attribute: List<FeedbackAction>?): String {
        return if (attribute.isNullOrEmpty()) {
            ""
        } else {
            attribute.joinToString(separator = ",")
        }
    }

    override fun convertToEntityAttribute(dbData: String?): List<FeedbackAction> {
        return if (dbData.isNullOrEmpty()) {
            emptyList()
        } else {
            dbData.split(",").map { FeedbackAction.from(it) }
        }
    }
}
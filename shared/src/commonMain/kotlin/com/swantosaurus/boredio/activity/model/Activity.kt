package com.swantosaurus.boredio.activity.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * in app representation of an activity
 */
data class Activity(
    val activity: String,
    val type: ActivityType,
    val participants: Int,
    val price: Double,
    val link: String,
    val key: String,
    val accessibility: Double,
    val favorite: Boolean,
    val userRating: Int?,
    val fetchDate: LocalDateTime,
    val completed: Boolean,
    val completeDate: LocalDateTime?,
    val ignore: Boolean,
    val isDailyFeed: Boolean,
    val path: String?,
    val isStored: Boolean,
)

@Serializable
enum class ActivityType {
    EDUCATION,
    RECREATIONAL,
    SOCIAL,
    DIY,
    CHARITY,
    COOKING,
    RELAXATION,
    MUSIC,
    BUSYWORK
}

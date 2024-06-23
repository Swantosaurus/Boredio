package com.swantosaurus.boredio.activity.model

import kotlinx.datetime.LocalDateTime

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
    val path: String?
)

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

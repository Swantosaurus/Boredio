package com.swantosaurus.boredio.dataSource.activity.model

import kotlinx.datetime.LocalDateTime

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
    val completeDate: LocalDateTime?
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

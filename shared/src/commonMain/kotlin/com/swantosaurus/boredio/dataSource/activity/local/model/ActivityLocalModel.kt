package com.swantosaurus.boredio.dataSource.activity.local.model

import com.swantosaurus.boredio.dataSource.activity.model.ActivityType



data class ActivityLocalModel(
    val activity: String,
    val type: ActivityType,
    val participants: Int,
    val price: Double,
    val link: String,
    val key: String,
    val accessibility: Double,
    val favorite: Boolean,
    val userRating: Int?,
    val fetchDate: Long,
    val completed: Boolean,
    val completeDate: Long?
)

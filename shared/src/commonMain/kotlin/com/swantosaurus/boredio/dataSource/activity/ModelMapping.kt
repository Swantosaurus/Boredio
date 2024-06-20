package com.swantosaurus.boredio.dataSource.activity

import com.swantosaurus.boredio.dataSource.activity.local.model.ActivityLocalModel
import com.swantosaurus.boredio.dataSource.activity.model.Activity
import com.swantosaurus.boredio.dataSource.activity.model.ActivityType
import com.swantosaurus.boredio.dataSource.activity.remote.model.ActivityRemoteModel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime


fun ActivityRemoteModel.toActivity(): Activity {
    val fetchDate = Clock.System.now().toLocalDateTime(timeZone = TimeZone.currentSystemDefault())
    val type = ActivityType.valueOf(type)

    return Activity(
        activity = activity,
        type = type,
        participants = participants,
        price = price,
        link = link,
        key = key,
        accessibility = accessibility,
        favorite = false,
        userRating = null,
        fetchDate = fetchDate,
        completed = false,
        completeDate = null
    )
}

fun ActivityRemoteModel.toLocalModel(): ActivityLocalModel {
    val fetchDate = Clock.System.now().toEpochMilliseconds()
    val type = ActivityType.valueOf(type)

    return ActivityLocalModel(
        activity = activity,
        type = type,
        participants = participants,
        price = price,
        link = link,
        key = key,
        accessibility = accessibility,
        favorite = false,
        userRating = null,
        fetchDate = fetchDate,
        completed = false,
        completeDate = null
    )
}

fun Activity.toLocalModel(): ActivityLocalModel {
    val fetchMillis = fetchDate.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    val completeMillis = completeDate?.toInstant(TimeZone.currentSystemDefault())?.toEpochMilliseconds()

    return ActivityLocalModel(
        activity = activity,
        type = type,
        participants = participants,
        price = price,
        link = link,
        key = key,
        accessibility = accessibility,
        favorite = favorite,
        userRating = userRating,
        fetchDate = fetchMillis,
        completed = completed,
        completeDate = completeMillis
    )
}
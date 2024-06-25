package com.swantosaurus.boredio.activity.dataSource

import com.swantosaurus.boredio.activity.dataSource.remote.model.ActivityRemoteModel
import com.swantosaurus.boredio.activity.model.Activity
import com.swantosaurus.boredio.activity.model.ActivityType
import com.swantosaurus.boredio.dataSource.activity.local.db.ActivityDatabaseModel
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime


fun ActivityRemoteModel.toActivity(isDailyFeed: Boolean, isSaved: Boolean): Activity {
    val fetchDate = Clock.System.now().toLocalDateTime(timeZone = TimeZone.currentSystemDefault())
    val type = ActivityType.valueOf(type.uppercase())

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
        completeDate = null,
        ignore = false,
        isDailyFeed = isDailyFeed,
        path = null,
        isStored = isSaved
    )
}

fun Activity.toDatabaseModel(): ActivityDatabaseModel {
    val fetchMillis = fetchDate.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    val completeMillis = completeDate?.toInstant(TimeZone.currentSystemDefault())?.toEpochMilliseconds()

    return ActivityDatabaseModel(
        activity = activity,
        type = type.toString(),
        participants = participants.toLong(),
        price = price,
        link = link,
        key = key,
        accessibility = accessibility,
        favorite = if (favorite) 1 else 0,
        userRating = userRating?.toLong(),
        fetchDate = fetchMillis,
        completed = completed,
        completeDate = completeMillis,
        ignore = ignore,
        isDailyFeed = isDailyFeed,
        path = path
    )
}

fun ActivityDatabaseModel.toActivity(): Activity {
    val fetchDate =
        Instant.fromEpochMilliseconds(fetchDate).toLocalDateTime(TimeZone.currentSystemDefault())
    val completeDate = completeDate?.let {
        Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault())
    }

    return Activity(
        activity = activity,
        type = ActivityType.valueOf(type.uppercase()),
        participants = participants.toInt(),
        price = price,
        link = link,
        key = key,
        accessibility = accessibility,
        favorite = favorite == 1L,
        userRating = userRating?.toInt(),
        fetchDate = fetchDate,
        completed = completed,
        completeDate = completeDate,
        ignore = ignore,
        isDailyFeed = isDailyFeed,
        path = path,
        isStored = true
    )
}
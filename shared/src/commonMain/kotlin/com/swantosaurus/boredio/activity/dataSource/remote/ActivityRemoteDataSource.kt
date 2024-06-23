package com.swantosaurus.boredio.activity.dataSource.remote

import com.swantosaurus.boredio.activity.dataSource.remote.model.ActivityRemoteModel
import com.swantosaurus.boredio.activity.model.ActivityType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.parameters

private const val DOMAIN = "https://bored.api.lewagon.com/api"

internal class ActivityRemoteDataSource(private val client: HttpClient){
    internal suspend fun getNewRandom(): ActivityRemoteModel =
        client.get("$DOMAIN/activity"){
           parameters {
                append("price", "0")
           }
        }.body()

    internal suspend fun getActivityByKey(key: String): ActivityRemoteModel =
        client.get {
            url {
                host = "$DOMAIN/activity"
                parameters.append("key", key)
            }
        }.body()

    internal suspend fun getActivityByType(type: ActivityType): ActivityRemoteModel =
        client.get {
            url {
                host = "$DOMAIN/activity"
                parameters.append("type", type.name)
            }
        }.body()

    internal suspend fun getActivityByPrice(price: Double): ActivityRemoteModel =
        getActivityByPrice(price..price)

    internal suspend fun getActivityByPrice(range: ClosedFloatingPointRange<Double>): ActivityRemoteModel =
        client.get {
            url {
                host = "$DOMAIN/activity"
                parameters.append("minprice", "${range.start}")
                parameters.append("maxprice", "${range.endInclusive}")
            }
        }.body()

    internal suspend fun getActivityByParticipants(participants: Int): ActivityRemoteModel =
        getActivityByParticipants(participants..participants)


    internal suspend fun getActivityByParticipants(range: IntRange): ActivityRemoteModel =
        client.get {
            url {
                host = "$DOMAIN/activity"
                parameters.append("minparticipants", "${range.first}")
                parameters.append("maxparticipants", "${range.last}")
            }
        }.body()
}
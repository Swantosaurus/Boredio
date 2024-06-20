package com.swantosaurus.boredio.dataSource.activity.remote

import com.swantosaurus.boredio.dataSource.activity.model.ActivityType
import com.swantosaurus.boredio.dataSource.activity.remote.model.ActivityRemoteModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

private val DOMAIN = "https://boredapi.com/api"

class ActivityRemoteDataSource(private val client: HttpClient){
    suspend fun getNewRandom(): ActivityRemoteModel =
        client.get {
            url {
                host = "$DOMAIN/activity"
            }
        }.body()

    suspend fun getActivityByKey(key: String): ActivityRemoteModel =
        client.get {
            url {
                host = "$DOMAIN/activity"
                parameters.append("key", key)
            }
        }.body()

    suspend fun getActivityByType(type: ActivityType): ActivityRemoteModel =
        client.get {
            url {
                host = "$DOMAIN/activity"
                parameters.append("type", type.name)
            }
        }.body()

    suspend fun getActivityByPrice(price: Double): ActivityRemoteModel =
        getActivityByPrice(price..price)

    suspend fun getActivityByPrice(range: ClosedFloatingPointRange<Double>): ActivityRemoteModel =
        client.get {
            url {
                host = "$DOMAIN/activity"
                parameters.append("minprice", "${range.start}")
                parameters.append("maxprice", "${range.endInclusive}")
            }
        }.body()

    suspend fun getActivityByParticipants(participants: Int): ActivityRemoteModel =
        getActivityByParticipants(participants..participants)


    suspend fun getActivityByParticipants(range: IntRange): ActivityRemoteModel =
        client.get {
            url {
                host = "$DOMAIN/activity"
                parameters.append("minparticipants", "${range.first}")
                parameters.append("maxparticipants", "${range.last}")
            }
        }.body()
}
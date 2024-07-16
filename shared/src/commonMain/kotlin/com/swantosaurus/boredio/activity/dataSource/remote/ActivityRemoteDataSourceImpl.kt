package com.swantosaurus.boredio.activity.dataSource.remote

import co.touchlab.kermit.Logger
import com.swantosaurus.boredio.activity.dataSource.remote.model.ActivityRemoteModel
import com.swantosaurus.boredio.activity.model.ActivityType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.parameters

private const val DOMAIN = "https://bored.api.lewagon.com/api"

private val logger = Logger.withTag("ActivityRemoteDataSource")

internal class ActivityRemoteDataSourceImpl(private val client: HttpClient)
    :ActivityRemoteDataSource{
    override suspend fun getNewRandom(): ActivityRemoteModel =
        client.get("$DOMAIN/activity"){
           parameters {
                append("price", "0")
           }
        }.body()

    /**
     * only query that should handle every possible case of parameters
     * look at https://bored.api.lewagon.com/documentation parameter for more info
     *
     * @param types list of [ActivityType]
     * @param minParticipants 0+
     * @param maxParticipants minParticipants+
     * @param minPrice 0.0..1.0
     * @param maxPrice minPrice+
     * @param minAccessibility 0.0..1.0
     * @param maxAccessibility minAccessibility+
     * @return [ActivityRemoteModel]
     */
    override suspend fun getRandomByParameters(
        types: List<ActivityType>,
        minParticipants: Int?,
        maxParticipants: Int?,
        minPrice: Double?,
        maxPrice: Double?,
        minAccessibility: Double?,
        maxAccessibility: Double?
    ): ActivityRemoteModel {
        val minParticipantsX: Int = minParticipants?.takeIf { it >= 0 } ?: 0
        val maxParticipantsX =
            maxParticipants?.takeIf { it >= minParticipantsX } ?: (minParticipantsX + 10)
        val minPriceX = minPrice?.takeIf { it in 0.0..1.0 } ?: 0.0
        val maxPriceX = maxPrice?.takeIf { it >= minPriceX } ?: 1.0
        val minAccessibilityX = minAccessibility?.takeIf { it in 0.0..1.0 } ?: 0.0
        val maxAccessibilityX = maxAccessibility?.takeIf { it >= minAccessibilityX } ?: 1.0


        val request = client.get("$DOMAIN/activity") {
            url {
                if(types.isNotEmpty()) {
                    parameters.appendAll("type", types.map { it.name.lowercase() })
                }
                parameters.append("minparticipants", minParticipants.toString())
                parameters.append("maxparticipants", maxParticipantsX.toString())
                parameters.append("minprice", minPriceX.toString())
                parameters.append("maxprice", maxPriceX.toString())
                parameters.append("minaccessibility", minAccessibilityX.toString())
                parameters.append("maxaccessibility", maxAccessibilityX.toString())

            }
        }
        return request.body()
    }

    override suspend fun getActivityByKey(key: String): ActivityRemoteModel =
        client.get {
            url {
                host = "$DOMAIN/activity"
                parameters.append("key", key)
            }
        }.body()

    override suspend fun getActivityByType(type: ActivityType): ActivityRemoteModel =
        getActivityByTypes(listOf(type))

    override suspend fun getActivityByTypes(types: List<ActivityType>): ActivityRemoteModel =
        getRandomByParameters(types = types)

    override suspend fun getActivityByPrice(price: Double): ActivityRemoteModel =
        getActivityByPrice(price..price)

    override suspend fun getActivityByPrice(range: ClosedFloatingPointRange<Double>): ActivityRemoteModel =
        getRandomByParameters(minPrice = range.start, maxPrice = range.endInclusive)

    override suspend fun getActivityByParticipants(participants: Int): ActivityRemoteModel =
        getActivityByParticipants(participants..participants)


    override suspend fun getActivityByParticipants(range: IntRange): ActivityRemoteModel =
        getRandomByParameters(minParticipants = range.first, maxParticipants = range.last)
}
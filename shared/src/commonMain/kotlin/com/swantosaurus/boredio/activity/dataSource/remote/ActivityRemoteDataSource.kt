package com.swantosaurus.boredio.activity.dataSource.remote

import com.swantosaurus.boredio.activity.dataSource.remote.model.ActivityRemoteModel
import com.swantosaurus.boredio.activity.model.ActivityType


internal interface ActivityRemoteDataSource {
    suspend fun getNewRandom(): ActivityRemoteModel

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
     suspend fun getRandomByParameters(
        types: List<ActivityType> = emptyList(),
        minParticipants: Int? = null,
        maxParticipants: Int? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        minAccessibility: Double? = null,
        maxAccessibility: Double? = null,
    ): ActivityRemoteModel

    suspend fun getActivityByKey(key: String): ActivityRemoteModel

    suspend fun getActivityByType(type: ActivityType): ActivityRemoteModel

    suspend fun getActivityByTypes(types: List<ActivityType>): ActivityRemoteModel

    suspend fun getActivityByPrice(price: Double): ActivityRemoteModel

    suspend fun getActivityByPrice(range: ClosedFloatingPointRange<Double>): ActivityRemoteModel

    suspend fun getActivityByParticipants(participants: Int): ActivityRemoteModel =
        getActivityByParticipants(participants..participants)


    suspend fun getActivityByParticipants(range: IntRange): ActivityRemoteModel
}
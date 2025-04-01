package com.alexiaapps.retrofit.data

import com.alexiaapps.retrofit.data.model.ObjectID
import com.alexiaapps.retrofit.data.model.ObjectIDs
import kotlinx.coroutines.flow.Flow

interface ObjectsRepository {
    suspend fun getObjectIDsList(): Flow<Result<ObjectIDs>>
    suspend fun getObjectByID(id: Int): Result<ObjectID>
}

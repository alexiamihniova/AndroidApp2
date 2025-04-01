package com.alexiaapps.retrofit.data

import com.alexiaapps.retrofit.data.Api
import com.alexiaapps.retrofit.data.model.ObjectID
import com.alexiaapps.retrofit.data.model.ObjectIDs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException

class ObjectsRepositoryImpl(
    private val api: Api
) : ObjectsRepository {

    override suspend fun getObjectIDsList(): Flow<Result<ObjectIDs>> {
        return flow {
            val objectsFromApi = try {
                api.getObjectIDsList()
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Result.Error<ObjectIDs>(message = "Error loading products"))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Result.Error(message = "Error loading products"))
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Result.Error(message = "Error loading products"))
                return@flow
            }
            emit(Result.Success(objectsFromApi))
        }
    }

    override suspend fun getObjectByID(id: Int): Result<ObjectID> {
        return try {
            val objectFromApi = api.getObjectByID(id)
            Result.Success(objectFromApi)
        } catch (e: IOException) {
            e.printStackTrace()
            Result.Error(message = "Error loading object: network issue")
        } catch (e: HttpException) {
            e.printStackTrace()
            Result.Error(message = "Error loading object: server issue")
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(message = "Error loading object")
        }
    }
}

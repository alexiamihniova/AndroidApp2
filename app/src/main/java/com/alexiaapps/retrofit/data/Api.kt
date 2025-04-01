package com.alexiaapps.retrofit.data  // ✅ Adaugă această linie

import com.alexiaapps.retrofit.data.model.ObjectID
import com.alexiaapps.retrofit.data.model.ObjectIDs
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {
    @GET("objects")
    suspend fun getObjectIDsList(): ObjectIDs

    @GET("objects/{objectID}")
    suspend fun getObjectByID(@Path("objectID") objectID: Int): ObjectID

    companion object {
        const val BASE_URL = "https://collectionapi.metmuseum.org/public/collection/v1/"
    }
}

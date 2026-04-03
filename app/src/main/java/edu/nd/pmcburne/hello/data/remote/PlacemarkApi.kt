package edu.nd.pmcburne.hello.data.remote

import edu.nd.pmcburne.hello.data.model.PlacemarkResponse
import retrofit2.http.GET

interface PlacemarkApi {
    @GET("placemarks.json")
    suspend fun getPlacemarks(): List<PlacemarkResponse>
    
    companion object {
        const val BASE_URL = "https://www.cs.virginia.edu/~wxt4gm/"
    }
}

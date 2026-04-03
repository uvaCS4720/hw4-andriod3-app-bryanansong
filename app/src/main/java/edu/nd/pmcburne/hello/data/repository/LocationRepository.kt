package edu.nd.pmcburne.hello.data.repository

import android.content.Context
import edu.nd.pmcburne.hello.data.local.AppDatabase
import edu.nd.pmcburne.hello.data.local.LocationEntity
import edu.nd.pmcburne.hello.data.model.PlacemarkResponse
import edu.nd.pmcburne.hello.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class LocationRepository(context: Context) {
    private val locationDao = AppDatabase.getDatabase(context).locationDao()
    private val api = RetrofitClient.placemarkApi
    
    val allLocations: Flow<List<LocationEntity>> = locationDao.getAllLocations()
    
    fun getLocationsByTag(tag: String): Flow<List<LocationEntity>> {
        return locationDao.getLocationsByTag(tag)
    }
    
    suspend fun syncLocationsIfNeeded() {
        val count = locationDao.getLocationCount()
        if (count == 0) {
            // First launch - fetch from API and save to database
            val placemarks = api.getPlacemarks()
            val locationEntities = placemarks.map { it.toEntity() }
            locationDao.insertLocations(locationEntities)
        }
        // If count > 0, data already exists - use local database only
    }
    
    suspend fun getAllTags(): List<String> {
        val locations = locationDao.getAllLocations().first()
        return locations.flatMap { entity ->
            entity.tags.split(",").map { it.trim() }
        }.distinct().sorted()
    }
    
    private fun PlacemarkResponse.toEntity(): LocationEntity {
        return LocationEntity(
            id = id,
            name = name,
            description = description,
            latitude = visualCenter.latitude,
            longitude = visualCenter.longitude,
            tags = tagList.joinToString(",")
        )
    }
}

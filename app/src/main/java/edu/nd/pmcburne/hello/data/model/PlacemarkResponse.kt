package edu.nd.pmcburne.hello.data.model

import com.google.gson.annotations.SerializedName

data class PlacemarkResponse(
    val id: Int,
    val name: String,
    @SerializedName("tag_list")
    val tagList: List<String>,
    val description: String,
    @SerializedName("visual_center")
    val visualCenter: VisualCenter
)

data class VisualCenter(
    val latitude: Double,
    val longitude: Double
)

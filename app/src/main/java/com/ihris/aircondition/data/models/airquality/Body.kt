package com.ihris.aircondition.data.models.airquality


import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("items")
    val measureValues: List<MeasureValue>?,
    @SerializedName("numOfRows")
    val numOfRows: Int?,
    @SerializedName("pageNo")
    val pageNo: Int?,
    @SerializedName("totalCount")
    val totalCount: Int?
)
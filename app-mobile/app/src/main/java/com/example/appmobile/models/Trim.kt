package com.example.appmobile.models

import com.google.gson.annotations.SerializedName

data class Trim(
    val make: String,
    val model: String,
    val generation: String?,
    @SerializedName("generation_year_begin") val generationYearBegin: String?,
    @SerializedName("generation_year_end") val generationYearEnd: String?,
    val serie: String?,
    val trim: String?,
    @SerializedName("trim_start_production_year") val trimStartYear: Int?,
    @SerializedName("trim_end_production_year") val trimEndYear: Int?,
    @SerializedName("car_type") val carType: String?
)

package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class ProjectListModel(
    @SerializedName("success") val success: Boolean,
    @SerializedName("count") val count: Int,
    @SerializedName("result") val result: List<ProjectData>
)

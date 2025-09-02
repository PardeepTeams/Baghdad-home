package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class ProjectListModel(
    @SerializedName("success") val success: Boolean? = false,
    @SerializedName("data") val data: ProjectDataModel? = null
)

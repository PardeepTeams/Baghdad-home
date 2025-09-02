package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class ProjectDataModel(
    @SerializedName("all_project_sliders")
    val allProjectSliders: List<ProjectSlider>? = ArrayList(),

    @SerializedName("all_project_cities")
    val allProjectCities: List<ProjectCity>? = ArrayList(),

    @SerializedName("projects_listing")
    val projectsListing: List<ProjectData>? = ArrayList()
)

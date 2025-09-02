package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class ProjectFloorPlan(
    @SerializedName("fave_plan_title")
    val favePlanTitle: String? = null,

    @SerializedName("fave_plan_rooms")
    val favePlanRooms: String? = null,

    @SerializedName("fave_plan_bathrooms")
    val favePlanBathrooms: String? = null,

    @SerializedName("fave_plan_price")
    val favePlanPrice: String? = null,

    @SerializedName("fave_plan_price_postfix")
    val favePlanPricePostfix: String? = null,

    @SerializedName("fave_plan_size")
    val favePlanSize: String? = null,

    @SerializedName("fave_plan_image")
    val favePlanImage: String? = null,

    @SerializedName("fave_plan_description")
    val favePlanDescription: String? = null
)

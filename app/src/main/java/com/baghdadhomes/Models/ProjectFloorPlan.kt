package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class ProjectFloorPlan(
    @SerializedName("fave_plan_title") val favePlanTitle: String,
    @SerializedName("fave_plan_rooms") val favePlanRooms: String,
    @SerializedName("fave_plan_bathrooms") val favePlanBathrooms: String,
    @SerializedName("fave_plan_price") val favePlanPrice: String,
    @SerializedName("fave_plan_price_postfix") val favePlanPricePostfix: String,
    @SerializedName("fave_plan_size") val favePlanSize: String,
    @SerializedName("fave_plan_image") val favePlanImage: String,
    @SerializedName("fave_plan_description") val favePlanDescription: String
)

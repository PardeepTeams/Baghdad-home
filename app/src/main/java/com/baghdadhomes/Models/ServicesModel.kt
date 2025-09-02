package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

class ServicesModel (
    @SerializedName("code"     ) var code     : Int? = null,
    @SerializedName("success"  ) var success  : String? = null,
    @SerializedName("response" ) var response : List<ServicesResponse>
)

data class ServicesResponse (
    @SerializedName("id" ) var id : Int?    = null,
    @SerializedName("post_title") var postTitle   : String? = null,
    @SerializedName("post_content") var postContent : String? = null,
    @SerializedName("permalink") var permalink   : String? = null,
    @SerializedName("thumbnail") var thumbnail   : String? = null,
    @SerializedName("arabic_title") val arabic_title : String,
    @SerializedName("service_orderid" ) var serviceOrderid : String? = null,
    @SerializedName("icon" ) var icon : String? = null
)
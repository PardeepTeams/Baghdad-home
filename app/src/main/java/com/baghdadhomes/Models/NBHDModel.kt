package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class NBHDModel(
    @SerializedName("code") val code : Int,
    @SerializedName("success") val success : Boolean,
    @SerializedName("response") val response : List<NBHDDataResponse>
)

data class NBHDDataResponse(
    /*@SerializedName("term_id") val term_id : String,*/
    @SerializedName("name") val name : String,
    @SerializedName("description") val description : String,
    @SerializedName("slug") val slug : String,
    /*@SerializedName("term_group") val term_group : String,
    @SerializedName("term_taxonomy_id") val term_taxonomy_id : String,
    @SerializedName("taxonomy") val taxonomy : String,
    @SerializedName("description") val description : String,
    @SerializedName("parent") val parent : String,
    @SerializedName("count") val count : Int,
    @SerializedName("filter") val filter : String,*/
    @SerializedName("area") var area : List<NBHDArea>
)

data class NBHDArea(
    /*@SerializedName("term_id") val term_id : String,*/
    @SerializedName("name") val name : String,
    @SerializedName("slug") val slug : String,
    @SerializedName("description") val description : String
    /*@SerializedName("term_group") val term_group : String,
    @SerializedName("term_taxonomy_id") val term_taxonomy_id : String,
    @SerializedName("taxonomy") val taxonomy : String,
    @SerializedName("description") val description : String,
    @SerializedName("parent") val parent : String,
    @SerializedName("count") val count : Int,
    @SerializedName("filter") val filter : String*/
)

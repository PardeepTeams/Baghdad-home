package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

class ModelAiSearchResponse (
    @SerializedName("success") var success : Boolean? = null,
    @SerializedName("message") var message : String? = null,
    @SerializedName("search_text") var searchText : String? = null,
    @SerializedName("count") var count : Int? = null,
    @SerializedName("result") var result : ArrayList<Result>? = null,
)
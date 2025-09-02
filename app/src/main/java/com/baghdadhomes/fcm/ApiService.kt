package com.baghdadhomes.fcm

import com.google.gson.JsonObject
import com.baghdadhomes.Models.AdsDetailModel
import com.baghdadhomes.Models.NewFeatureModel
import com.baghdadhomes.Models.ProjectDetailResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @Headers(
        "Content-Type:application/json"
    )
    @POST("v1/projects/baghdadhomes/messages:send")
    fun sendNotification(@Header ("Authorization") token : String, @Body root: RootModel?): Call<ResponseBody?>?


    @GET
    fun hitGetApiWithouToken(@Url url: String): Call<JsonObject>

    @GET
    fun hitGetApiWithouTokenWithParams(@Url url: String, @QueryMap parameters: HashMap<String, String>): Call<JsonObject>

    @POST
    fun hitPostApiWithouTokenParams(@Url url: String,@Body parameters: HashMap<Any, Any>): Call<JsonObject>
    @FormUrlEncoded
    @POST
    fun hitPostApiWithouTokenFieldParams(@Url url: String,@FieldMap map:Map<String,String>/*@Field("per_page") per_page :String,@Field("page") page :String*/): Call<JsonObject>

    @FormUrlEncoded
    @POST
    fun hitPostApiFilter(@Url url: String,@FieldMap map:Map<String,String>,@Field ("area[]") area : ArrayList<String>): Call<JsonObject>

    @FormUrlEncoded
    @POST
    fun hitPostApiNotificationRemove(@Url url: String,@FieldMap map:Map<String,String>,@Field ("notification_id[]") ids : ArrayList<String>): Call<JsonObject>

    @Multipart
    @POST
    fun hitMultipartApi(@Url url: String, @PartMap parameters: HashMap<String, RequestBody>, @Part imageBody: MultipartBody.Part?= null): Call<JsonObject>

    @Multipart
    @POST("houzez-mobile-api/v1/media_upload")
    fun uploadsImages(@Part image: MultipartBody.Part): Call<JsonObject>

    @Multipart
    @POST
    fun hitMultipartApiWithoutParams(@Url url : String,@Part video : MultipartBody.Part): Call<JsonObject>

    @FormUrlEncoded
    @POST
    fun hitAddPostApiWithouTokenFieldParams(@Url url: String,@FieldMap map:Map<String,String>,@Field("propperty_image_ids[]") imagesList:ArrayList<String>): Call<JsonObject>

    @POST
    fun hitSendOtpApi(@Header ("Authorization") token: String, @Url url: String, @Body map : HashMap<Any, Any>): Call<JsonObject>

    @POST
    fun getViewCount(@Url url: String, @Body parameters: HashMap<String, String>): Call<AdsDetailModel>

    @GET
    suspend fun getProperties(@Url url: String, @QueryMap parameters: HashMap<String, String>): Response<NewFeatureModel>

    @GET
    suspend fun getView(@Url url: String, @QueryMap parameters: HashMap<String, String>): Response<AdsDetailModel>

    @POST
    suspend fun getPropertyDetails(@Url url: String, @QueryMap parameters: HashMap<String, String>): Response<ProjectDetailResponse>
}
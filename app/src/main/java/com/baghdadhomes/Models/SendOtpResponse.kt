package com.baghdadhomes.Models

data class SendOtpResponse(
    val status : String ?= null,
    val message : String ?= null,
    val data : SendOtpData ?= null,
)

data class SendOtpData(
    val user_id : String ?= null,
    val to : String ?= null,
    val message : String ?= null,
    val sms_type : String ?= null,
    val status : String ?= null,
    val sms_count : String ?= null,
    val sending_server_id : String ?= null,
    val from : String ?= null,
    val api_key : String ?= null,
    val send_by : String ?= null,
    val lang : String ?= null,
    val uid : String ?= null,
    val updated_at : String ?= null,
    val created_at : String ?= null,
    val id : String ?= null
)

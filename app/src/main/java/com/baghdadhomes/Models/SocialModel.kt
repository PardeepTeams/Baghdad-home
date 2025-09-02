package com.baghdadhomes.Models

data class SocialModel(
    var email : String ?= null,
    var phone : String ?= null,
    var source : String ?= null,
    var user_id : String ?= null,
    var login_type : String ?= null,
    var profile_url : String ?= null,
    var display_name : String ?= null,
    var device_token : String ?= null,
)

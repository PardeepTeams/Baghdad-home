package com.baghdadhomes.Utils

import com.baghdadhomes.Models.ReelResult

class Constants {
    companion object {
        const val STATUS = "success"
        const val STATUES = "status"
        const val MESSAGE = "message"
        // 1 const val GETFEATUREDPROPERTIES = "houzez-mobile-api/v1/get-custom-properties"
        //https://najafhome.com/wp-json/houzez-mobile-api/v1/get-custom-list-properties-new
        // 2 const val GETFEATUREDPROPERTIES = "houzez-mobile-api/v1/get-custom-list-properties"
        const val GETFEATUREDPROPERTIES = "houzez-mobile-api/v1/get-custom-list-properties-new"
        const val GETFEATUREDPROPERTIESNEW = "houzez-mobile-api/v1/home-list-properties-new"
        const val GETFEATURED = "GET_featured"
        const val GET_PROPERTIES = "GET_PROPERTIES"
        const val GET_PROPERTIES_DETAIL = "GET_PROPERTIES_DETAIL"
        const val GET_PROPERTIES_SEARCH = "GET_PROPERTIES_SEARCH"
       // https://najafhome.com/wp-json/wp/v2/users/register
        const val GET_HOME_BANNER = "houzez-mobile-api/v1/get-home-slider"
        const val GET_BANNER = "GET_BANNER"
        const val ADD_POST = "ADD_POST"
        const val ADD_POST_URL = "houzez-mobile-api/v1/add-property"
        const val LOGIN_API = "wp/v2/users/login"
        const val LOGIN = "login"
        const val REGISTER = "Register"
        const val REGISTER_API = "wp/v2/users/register"
        const val UPLOAD_IMAGE = "UPLOAD_IMAGE"
        const val UPLOAD_IMAGE_API = "houzez-mobile-api/v1/save-property-image"
        const val GET_NBHD = "GET_NBHD"
        const val GET_NBHD_URL = "wp/v2/all-terms?term=property_area"
        const val GET_CITY = "GET_CITY"
        const val GET_CITY_URL = "wp/v2/all-terms?term=property_city"
        const val SOCIAL_LOGIN = "social_login"
        const val SOCIAL_LOGIN_API = "houzez-mobile-api/v1/social-sign-on"
        const val MY_ADD = "MY_ADD"
        const val MY_ADD_URL = "houzez-mobile-api/v1/my-properties"
        const val DELETE_ADD = "DELETE_ADD"
        const val DELETE_ADD_URL = "houzez-mobile-api/v1/delete-property"
        //https://najafhome.com/wp-json/houzez-mobile-api/v1/get-custom-search
        const val UPDATE_POST_URL = "houzez-mobile-api/v1/update-property"
        const val WHATSAPP_REGISTER = "WHATSAPP_REGISTER"
        const val UPDATE_ADD = "UPDATE_ADD"

        const val CHECK_WHATSAPP_LOGIN_API = "wp/v2/users/number-verification"
        const val CHECK_WHATSAPP_LOGIN = "CHECK_WHATSAPP_LOGIN"
        const val GET_FAVORITE = "GET_FAVORITE"
        const val GET_FAVORITE_API = "houzez-mobile-api/v1/favorite-properties?user_id="
        const val ADD_REMOVE_FAV = "ADD_REMOVE_FAV"
        const val ADD_REMOVE_FAV_DETAIL = "ADD_REMOVE_FAV_DETAIL"
        const val ADD_REMOVE_FAV_API = "houzez-mobile-api/v1/like-property"
        const val PROFILE_UPDATE = "PROFILE_UPDATE"
        const val PROFILE_UPDATE_API = "wp/v2/users/user-profile-update"
        const val FILTER = "wp/v2/users/user-profile-update"
        const val FILTER_API = "houzez-mobile-api/v1/get-custom-search"
        const val SERVICES = "SERVICES"
      //  const val SERVICES_API = "wp/v2/all-services"
        const val SERVICES_API = "wp/v2/all-services-get"
        const val SERVICES_COMPANY = "SERVICES_COMPANY"
        const val SERVICES_COMPANY_API = "wp/v2/all-companies-by-service?service_id="
        const val REPORT_PROPERTY = "REPORT_PROPERTY"
        const val REPORT_PROPERTY_API = "wp/v2/report-property"
        const val VIEW_COUNT = "VIEW_COUNT"
        const val VIEW_COUNT_API = "wp/v2/update-property-view"
        const val VIEW_COUNT_API_NEW = "wp/v2/update-property-view-get"

        const val NEIGHBORHOOD = "NEIGHBORHOOD"
        const val NEIGHBORHOOD_API = "wp/v2/property_city_data_area"


        const val AMINITY_API = "houzez-mobile-api/v1/features"
        const val AMINITY = "AMINITY"

        const val SEND_TOKEN = "SEND_TOKEN"
        const val SEND_TOKEN_API = "wp/v2/send-token"

        const val GET_NOTIFICATIONS = "NEIGHBORHOOD"
        const val GET_NOTIFICATIONS_API = "wp/v2/all-notification?user_id="

        const val DELETE_NOTIFICATIONS = "DELETE_NOTIFICATIONS"
        const val DELETE_NOTIFICATIONS_API = "wp/v2/remove-notification"

        const val DELETE_ACCOUNT = "DELETE_ACCOUNT"
        const val DELETE_ACCOUNT_API = "wp/v2/account-remove"

        const val UPLOAD_VIDEO = "UPLOAD_VIDEO"
        const val UPLOAD_VIDEO_API = "wp/v2/upload-video"

        const val GET_REELS = "GET_REELS"
        const val GET_REELS_API = "wp/v2/reels-listing"

        const val REEL_VIEW_UPDATE = "REEL_VIEW_UPDATE"
        const val REEL_VIEW_UPDATE_API = "wp/v2/view-reel"

        const val APP_UPDATE_CHECK = "APP_UPDATE_CHECK"
        const val APP_UPDATE_CHECK_API = "wp/v2/version-check?device_type=android"

        const val GET_CHAT_LIST = "GET_CHAT_LIST"
        // const val GET_CHAT_LIST_API = "wp/v3/users-chat-listing?user_id="
        const val GET_CHAT_LIST_API = "wp/v3/users-chat-listing-post"

        const val DELETE_MULTIPLE_NOTIFICATIONS = "DELETE_MULTIPLE_NOTIFICATIONS"
        const val DELETE_MULTIPLE_NOTIFICATIONS_API = "wp/v2/remove-notification-multiple"

        const val AGENCIES = "AGENCIES"
        const val AGENCIES_API = "wp/v2/agencies"

        const val AGENCY_ADS = "AGENCY_ADS"
        const val AGENCY_ADS_API = "wp/v2/agency_ads"

        const val CHECK_SOCIAL_LOGIN = "CHECK_SOCIAL_LOGIN"
        const val CHECK_SOCIAL_LOGIN_API = "wp/v2/social_login_check"

        const val UPLOAD_USER_IMAGE = "UPLOAD_USER_IMAGE"
        const val UPLOAD_USER_IMAGE_API = "wp/v2/social-image-upload"

        const val SEND_SMS_OTP = "SEND_SMS_OTP"
        const val SEND_SMS_OTP_API = "api/v4/sms/send"
        const val SEND_SMS_OTP_TOKEN = "Bearer 124|CfIp4WiWB0CAMlv0PRjap1c877RWMNToMCzl46V34b99cae4"

        const val GET_USER_PROFILE = "GET_USER_PROFILE"
       // const val GET_USER_PROFILE_API = "wp/v2/get_user_profile"
        const val GET_USER_PROFILE_API = "wp/v2/user_profile"

        const val FOLLOW_UNFOLLOW_AGENCY = "FOLLOW_UNFOLLOW_AGENCY"
        const val FOLLOW_UNFOLLOW_AGENCY_API = "wp/v2/agency-follow"

        const val GET_HOME = "HOME_API"
        const val GET_HOME_API = "houzez-mobile-api/v1/get-home"

        const val GET_Project_Main = "Project_main_API"
        const val GET_Project_Main_API = "houzez-mobile-api/v1/get-projects"

        const val GET_Project_CITY = "Project_city_API"
        const val GET_Project_City_API = "houzez-mobile-api/v1/get-projects-by-city"


        const val GET_FAV_Project = "Project_FAV_API"
        const val GET_FAV_Project_API = "houzez-mobile-api/v1/favorite-projects"

        const val GET_PROJECT_DETAILS = "houzez-mobile-api/v1/get-project-detail"

        var reelsModel: ReelResult? = null

        const val AISEARCH = "AISEARCH"
        const val AISEARCHAPI = "houzez-mobile-api/v1/ai-search"
    }
}
package com.baghdadhomes

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.baghdadhomes.Models.HomeModel
import com.baghdadhomes.Models.LoginResponse
import com.baghdadhomes.Models.NewFeatureModel
import com.baghdadhomes.Models.Result

class PreferencesService  constructor()
{
    companion object {

        private val PREFS_NAME = "Najaf_Homes"
        @SuppressLint("StaticFieldLeak")
        val instance = PreferencesService()
        @SuppressLint("StaticFieldLeak")
        private var mContext: Context? = null

        fun init(context: Context) {
            mContext = context
        }
    }
    fun getInstance(): PreferencesService? {
        return instance
    }

    val prefs: SharedPreferences
        get() = mContext!!.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val getToken: String
        get() {
            val json =  prefs.getString("user_token", "")

            return json!!
        }
    val getPrivacydata: String
        get() {
            val json = prefs.getString("privacy_policy", "")

            return json!!
        }

    val getTermsdata: String
        get() {
            val json = prefs.getString("terms_conditions", "")

            return json!!
        }
    fun saveUserToken(saveStrData: String?) {
        val editor = prefs.edit()
        editor.putString("user_token", saveStrData!!)
        editor.apply()
    }

    fun saveUserInterest(saveStrData: String?) {

        val editor = prefs.edit()
        editor.putString("user_interst", saveStrData!!)
        editor.apply()
    }

    val getUserInterest: String
        get() {
            val json =  prefs.getString("user_token", "")

            return json!!
        }

    fun saveTermsdata(saveStrData: String?) {
        val editor = prefs.edit()
        editor.putString("terms_conditions", saveStrData!!)
        editor.apply()
    }

    fun savePrivacyData(saveStrData: String?){
        val editor = prefs.edit()
        editor.putString("privacy_policy", saveStrData!!)
        editor.apply()
    }


    fun saveShareCode(saveStrData: String?) {
        val editor = prefs.edit()
        editor.putString("share_kyroz_code", saveStrData!!)
        editor.apply()
    }

    val getShareCode: String
        get() {
            val json = prefs.getString("share_kyroz_code", "")

            return json!!
        }

    val userLoginStatus: Boolean?
        get() = prefs.getBoolean("login_status", false)

    fun saveUserLoginStatus(loginStatus: Boolean?) {
        val editor = prefs.edit()
        editor.putBoolean("login_status", loginStatus!!)
        editor.apply()
    }

    fun saveUserLoginMethod(loginMethod: String){
        val editor = prefs.edit()
        editor.putString("loginMethod", loginMethod)
        editor.apply()
    }

    fun getLoginMethod(): String {
        return prefs.getString("loginMethod", "").toString()
    }

    fun isLanguageSaved(languageSaved: Boolean?) {
        val editor = prefs.edit()
        editor.putBoolean("language_saved", languageSaved!!)
        editor.apply()
    }

    val languageSaved: Boolean
        get() = prefs.getBoolean("language_saved", false)

    fun saveUserProfile(userdata: LoginResponse) {
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(userdata)
        editor.putString("user_paint_details", json)
        editor.apply()
    }

    val getUserData: LoginResponse?
        get() = Gson().fromJson(
            prefs.getString("user_paint_details", ""),
            LoginResponse::class.java
        )

    fun saveChangedPropertyData(data: Result){
        val editor = prefs.edit()
        val json = Gson().toJson(data)
        editor.putString("changedProperty", json)
        editor.apply()
    }

    fun saveChangedPropertyDataId(id:String,boolean: Boolean){
        val editor = prefs.edit()
        editor.putString("ads_id", id)
        editor.putBoolean("isFav", boolean)
        editor.apply()
    }
    fun getIsFav(): Boolean {
        return prefs.getBoolean("isFav",false);
    }
    fun getAdsId(): String? {
        return  prefs.getString("ads_id","");
    }

    val getChangedPropertyData: Result?
    get() = Gson().fromJson(prefs.getString("changedProperty",""),
    Result::class.java)

    fun clearChangedData(){
        val editor = prefs.edit()
        editor.putString("changedProperty", "")
        editor.apply()
    }

    fun clearPreference() {
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.clear()
        editor.apply()
    }


    fun saveNoWeddingPopUpDuration(date: Long) {
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putLong("date", date)
        editor.commit()
    }


    fun getNoWeddingPopUpDuration(): Long {
        return prefs.getLong("date", 0)
    }


    fun saveLanguage(language: String) {
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString("lang", language)
        editor.commit()
    }

    fun getLanguage(): String {
        return prefs.getString("lang", "ar").toString()
    }

    fun saveHomeData(userdata: HomeModel) {
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(userdata)
        editor.putString("home_data", json)
        editor.apply()
    }

    val getHomeData: HomeModel?
        get() = Gson().fromJson(
            prefs.getString("home_data", ""),
            HomeModel::class.java
        )

    fun saveFeaturedData(userdata: NewFeatureModel) {
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(userdata)
        editor.putString("featured_data", json)
        editor.apply()
    }

    val getFeaturedData: NewFeatureModel?
        get() = Gson().fromJson(
            prefs.getString("featured_data", ""),
            NewFeatureModel::class.java
        )

}
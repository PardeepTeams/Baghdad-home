package com.baghdadhomes.Fragments

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.gson.JsonObject
import com.baghdadhomes.Activities.BaseActivity
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.ProgressHud
import com.baghdadhomes.Utils.Utility
import com.baghdadhomes.fcm.ApiClient
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Part
import retrofit2.http.PartMap

abstract class BaseFragment: Fragment() {
    internal var progressHUD: ProgressHud? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            PreferencesService.init(requireActivity())

            if(PreferencesService.instance.getLanguage()!=null && PreferencesService.instance.getLanguage().isNotEmpty()){
                var code  = PreferencesService.instance.getLanguage()
                ((requireContext()) as BaseActivity).setAppLocale(requireActivity(), code)
            }else{
                ((requireContext()) as BaseActivity).setAppLocale(requireActivity(), "ar")
            }
        } catch (e : Exception){
            println("MyException:-${e.localizedMessage}")
        }
    }

    fun Context.dismissKeyboard(view: View?) {
        view?.let {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    fun showToast(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun hasPermissions(vararg permissions: String?): Boolean {
        if (permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(requireActivity(), permission!!)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    return true
                }
            }
        }
        return false
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }


    fun hitGetApiWithoutTokenWithParams(
        type: String,
        showLoader: Boolean,
        url: String,
        params: java.util.HashMap<String, String>
    ) {
        if (showLoader) {
            progressHUD = ProgressHud.show(
                requireActivity(),
                false,
                false
            )
        }

        Thread{
            ApiClient.api!!.hitGetApiWithouTokenWithParams(
                ApiClient.baseUrl + url, params).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                   activity?.runOnUiThread {
                       getData(response, type, showLoader)
                   }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    activity?.runOnUiThread {
                        if (showLoader) {
                            progressHUD!!.dismiss()
                        }
                        Toast.makeText(
                            context,
                            resources.getString(R.string.something_went_wrong),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                }
            })
        }.start()
    }

    fun hitGetApiWithoutToken(type: String, showLoader: Boolean, url: String) {
        if (showLoader) {
            progressHUD = ProgressHud.show(requireActivity(), false, false)
        }

        ApiClient.api!!.hitGetApiWithouToken(ApiClient.baseUrl + url)
            .enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                getData(response, type, showLoader)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                if (showLoader) {
                    progressHUD!!.dismiss()
                }

                Toast.makeText(
                    activity,
                    resources.getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })

    }

    fun hitMultipartApi(
        type: String,
        showLoader: Boolean,
        url: String,
        @PartMap parameters: HashMap<String, RequestBody>,
        @Part imageBody: MultipartBody.Part?= null
    ) {
        if (showLoader) {
            progressHUD = ProgressHud.show(
                requireActivity(),
                false,
                false
            )
        }

        ApiClient.api!!.hitMultipartApi(
            ApiClient.baseUrl + url,parameters,imageBody).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d("responseBody",response.body().toString())
                getData(response, type, showLoader)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                if (showLoader) {
                    progressHUD!!.dismiss()
                }
                Log.d("responseBody",t.toString())
                Toast.makeText(
                    requireActivity(),
                    resources.getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })

    }

    fun hitPostApiWithoutTokenParams(
        type: String,
        showLoader: Boolean,
        url: String,
        params: HashMap<Any,Any>
    ) {
        if (showLoader) {
            progressHUD = ProgressHud.show(
                requireActivity(),
                false,
                false
            )
        }

        ApiClient.api!!.hitPostApiWithouTokenParams(
            ApiClient.baseUrl + url,params).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                getData(response, type, showLoader)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                if (showLoader) {
                    progressHUD!!.dismiss()
                }

                Toast.makeText(
                    activity,
                    resources.getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })

    }


    fun hitPostApi(
        type: String,
        showLoader: Boolean,
        url: String,
     /*   page:String,*/
        map:HashMap<String,String>
        /*perPage:String*/
    ) {
        if (showLoader) {
            try {
                if (progressHUD != null && progressHUD!!.isShowing()) {
                    progressHUD!!.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            progressHUD = ProgressHud.show(
                requireActivity(),
                false,
                false
            )
        }

        Thread{
            ApiClient.api!!.hitPostApiWithouTokenFieldParams(
                ApiClient.baseUrl + url,map).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    activity?.runOnUiThread{
                        getData(response, type, showLoader)
                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    activity?.runOnUiThread{
                        if (showLoader) {
                            progressHUD!!.dismiss()
                        }

                        Toast.makeText(
                            activity,
                            resources.getString(R.string.something_went_wrong),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                }
            })
        }.start()



    }

    abstract fun getResponse(apiType: String, respopnse: JsonObject)

    //get api response
    fun getData(response: Response<JsonObject>, type: String, showLoader: Boolean) {
        if (response.isSuccessful() && response.code() == 200) {
            if ((response.body() as JsonObject).get(Constants.STATUS).asString.equals("true")) {
                if(response.body()!=null)
                    getResponse(type, response.body() as JsonObject)

            } else {
                // Utility.showSnackBar(this.findViewById(android.R.id.content),(response.body() as JsonObject).get(Constants.MESSAGE).asString,this)
                    try {
                        Utility.showToast(requireActivity(), (response.body() as JsonObject).get(Constants.MESSAGE).asString)
                    }catch (e:Exception){
                        e.message
                    }

            }
        }

        else if (response.code()==400)
        {
            try {
                val jObjError = JSONObject(response.errorBody()!!.string())
                //  Utility.showSnackBar(this.findViewById(android.R.id.content),jObjError.getString(Constants.MESSAGE),this)
                Utility.showToast(requireActivity(), jObjError.getString(Constants.MESSAGE))
            } catch (e: Exception) {
            }
        }
        else if (response.code() == 401) {

        } else if (response.code() == 403 || response.code() == 404) {


        } else if (response.code() == 409) {
            try {
                val jObjError = JSONObject(response.errorBody()!!.string());
                // Utility.showSnackBar(this.findViewById(android.R.id.content),jObjError.getString(Constants.MESSAGE),this)
                Utility.showToast(requireActivity(), jObjError.getString(Constants.MESSAGE))
            } catch (e: Exception) {
            }
        } else {
            try {
                if (progressHUD != null && progressHUD!!.isShowing()) {
                    progressHUD!!.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Toast.makeText(
                requireActivity(),
                resources.getString(R.string.something_went_wrong),
                Toast.LENGTH_SHORT
            ).show()
        }
        if (showLoader) {
            if(type == Constants.GET_HOME){
                try {
                    Handler(Looper.getMainLooper()).postDelayed(kotlinx.coroutines.Runnable {
                        if (progressHUD != null && progressHUD!!.isShowing()) {
                            progressHUD!!.dismiss()
                        }
                    },1000)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }else{
                try {
                    if (progressHUD != null && progressHUD!!.isShowing()) {
                        progressHUD!!.dismiss()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
    }
}
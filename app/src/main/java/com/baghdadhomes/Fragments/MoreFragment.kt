package com.baghdadhomes.Fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.Activities.*
import com.baghdadhomes.BuildConfig
import com.baghdadhomes.Models.LoginModel
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.Utility
import de.hdodenhof.circleimageview.CircleImageView
import java.net.URLEncoder


class MoreFragment : BaseFragment() {
    private lateinit var sign_up: TextView
    private lateinit var log_in: TextView
    private lateinit var tv_logout: TextView
    private lateinit var user_name: TextView
    private lateinit var user_email: TextView
    private lateinit var tv_version: TextView
    private lateinit var ll_after_login: LinearLayout
    private lateinit var ll_login_signup: LinearLayout
    private lateinit var rl_my_ads: RelativeLayout
    private lateinit var rl_language: RelativeLayout
    private lateinit var card_view: CardView
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var img_user: CircleImageView
    var versionName: String = BuildConfig.VERSION_NAME
    lateinit var rl_call:RelativeLayout
    lateinit var rl_wp:RelativeLayout
    lateinit var rl_insta:RelativeLayout
    lateinit var rl_fb:RelativeLayout
    lateinit var rl_web:RelativeLayout
    lateinit var rl_my_fav:RelativeLayout
    lateinit var rl_notification:RelativeLayout
    lateinit var rl_about_us:RelativeLayout
    lateinit var rl_delete_account:RelativeLayout
    lateinit var ll_delete_account:LinearLayout
    lateinit var rl_my_chats:RelativeLayout
    lateinit var rlServices:RelativeLayout
    lateinit var rl_realtors:RelativeLayout
    lateinit var rl_icon:RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_more, container, false)
    }

    private fun setUserData(){
        val name = PreferencesService.instance.getUserData!!.displayName
        val email = PreferencesService.instance.getUserData!!.userEmail
        var photo:String? = null
        if(PreferencesService.instance.getUserData!!.userImage!=null){
            photo = PreferencesService.instance.getUserData!!.userImage
        }

        if(photo != null){
            //Log.d("photo",photo)
            Glide.with(this).load(photo).placeholder(
                ContextCompat.getDrawable(requireContext(),
                    R.drawable.img_placeholder)).into(img_user)
        } else {
            img_user.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.img_placeholder))
            //Glide.with(this).load(resources.getDrawable(R.drawable.img_placeholder)).into(img_user)
        }

        if (name != null){
            user_name.text = name
        } else{
            user_name.visibility = View.GONE
        }

        if (email!= null){
            user_email.text = email
        } else{
            user_email.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        val isLogged = PreferencesService.instance.userLoginStatus
        if (isLogged == true){
            setUserData()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseApp.initializeApp(requireActivity())
        rl_delete_account = view.findViewById(R.id.rl_delete_account)
        ll_delete_account = view.findViewById(R.id.ll_delete_account)
        rl_about_us = view.findViewById(R.id.rl_about_us)
        sign_up = view.findViewById(R.id.sign_up)
        log_in = view.findViewById(R.id.log_in)
        user_name = view.findViewById(R.id.user_name)
        user_email = view.findViewById(R.id.user_email)
        tv_logout = view.findViewById(R.id.tv_logout)
        ll_after_login = view.findViewById(R.id.ll_after_login)
        ll_login_signup = view.findViewById(R.id.ll_login_signup)
        card_view = view.findViewById(R.id.card_view)
        rl_my_ads = view.findViewById(R.id.rl_my_ads)
        rl_language = view.findViewById(R.id.rl_language)
        img_user = view.findViewById(R.id.img_user)
        tv_version = view.findViewById(R.id.tv_version)
        rl_my_fav = view.findViewById(R.id.rl_my_fav)
        rl_notification = view.findViewById(R.id.rl_notification)
        rl_my_chats = view.findViewById(R.id.rl_my_chats)
        rlServices = view.findViewById(R.id.rlServices)
        rl_icon = view.findViewById(R.id.rl_icon)

        rl_wp = view.findViewById(R.id.rl_wp)
        rl_call = view.findViewById(R.id.rl_call)
        rl_insta = view.findViewById(R.id.rl_insta)
        rl_web = view.findViewById(R.id.rl_web)
        rl_fb = view.findViewById(R.id.rl_fb)
        rl_realtors = view.findViewById(R.id.rl_realtors)

        tv_version.setText(resources.getString(R.string.version)+" "+ versionName)
        sign_up.getPaint().isUnderlineText = true
        log_in.getPaint().isUnderlineText = true

        rl_icon.setOnClickListener {
            ((context) as HomeActivity).setHomeFragment()
        }


        val isLogged = PreferencesService.instance.userLoginStatus

        if (!isLogged!!){
            ll_login_signup.setVisibility(View.VISIBLE)
            card_view.setVisibility(View.GONE)
            tv_logout.setVisibility(View.GONE)
            ll_after_login.setVisibility(View.GONE)
            ll_delete_account.setVisibility(View.GONE)
        } else{

            ll_login_signup.setVisibility(View.GONE)
            card_view.setVisibility(View.VISIBLE)
            tv_logout.setVisibility(View.VISIBLE)
            ll_after_login.setVisibility(View.VISIBLE)
            ll_delete_account.setVisibility(View.VISIBLE)

            if (isNetworkAvailable()){
                val userID = PreferencesService.instance.getUserData!!.ID.orEmpty()
                val map : HashMap<String, String> = HashMap()
                map["user_id"] = userID
                /*val map : HashMap<Any, Any> = HashMap()
                map["user_id"] = userID*/
                hitGetApiWithoutTokenWithParams(Constants.GET_USER_PROFILE, true, Constants.GET_USER_PROFILE_API, map)
            } else {
                Utility.showToast(requireContext(), requireContext().getString(R.string.intenet_error))
            }

        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        rl_my_chats.setOnClickListener {
            ((context) as HomeActivity).setChatFragment()
        }

        rl_realtors.setOnClickListener {
            // ((context) as HomeActivity).setRealEstateFragment()
            startActivity(Intent(context, RealEstateFragment::class.java))
          //  requireActivity().overridePendingTransition(0,0)
        }

        card_view.setOnClickListener {
            startActivity(Intent(context, UpdateProfileActivity::class.java))
           // requireActivity().overridePendingTransition(0,0)
        }

        sign_up.setOnClickListener{
            //startActivity(Intent(context, RegisterActivity::class.java))
            //requireActivity().//overridePendingTransition(0,0)
            (context as BaseActivity).loginTypeDialog(true)
        }

        log_in.setOnClickListener{
            //startActivity(Intent(context, LoginActivity::class.java))
            //requireActivity().//overridePendingTransition(0,0)
            (context as BaseActivity).loginTypeDialog(false)
        }

        rlServices.setOnClickListener{
            startActivity(Intent(context, ServicesActivity::class.java))
          //  requireActivity().//overridePendingTransition(0,0)
        }

        tv_logout.setOnClickListener{
            /*val sharedPreferences =
                requireContext().getSharedPreferences("Notifications", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()*/
            try {
                val senderId = PreferencesService.instance.getUserData?.ID
                val timestamp = System.currentTimeMillis().toString()
                ((context) as BaseActivity).updateMyOnlineStatus(timestamp,senderId.toString())
            } catch (e : Exception){
                e.localizedMessage
            }
            FirebaseAuth.getInstance().signOut()
            LoginManager.getInstance().logOut()
            GoogleSignIn.getClient(
                requireActivity(),
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            ).signOut()
            // PreferencesService.instance.clear()
            PreferencesService.instance.saveUserLoginStatus(false)
            startActivity(Intent(context, HomeActivity::class.java))
          //  requireActivity().overridePendingTransition(0,0)
            /*   mGoogleSignInClient.signOut().addOnCompleteListener{
                   if (it.isSuccessful){
                      //  PreferencesService.instance.clearPreference()
                       PreferencesService.instance.saveUserLoginStatus(false)
                       startActivity(Intent(context, HomeActivity::class.java))
                   }
               }*/
            /*ll_login_signup.setVisibility(View.VISIBLE)
            card_view.setVisibility(View.GONE)
            tv_logout.setVisibility(View.GONE)
            ll_after_login.setVisibility(View.GONE)*/
        }

        rl_notification.setOnClickListener {
            startActivity(Intent(context, NotificationsActivity::class.java))
        //    requireActivity().//overridePendingTransition(0,0)
        }

        rl_my_fav.setOnClickListener {
            startActivity(Intent(context, MyFavoriteActivity::class.java))
          //  requireActivity().//overridePendingTransition(0,0)
        }

        rl_my_ads.setOnClickListener{
            startActivity(Intent(context, MyAdsActivity::class.java))
          //  requireActivity().//overridePendingTransition(0,0)
        }

        rl_language.setOnClickListener {
          //  ((activity as BaseActivity).chooseLanguage(activity as BaseActivity))
            ((activity as BaseActivity).showBottomSheetDialog(activity as BaseActivity))
        }

        rl_about_us.setOnClickListener {
            startActivity(Intent(context, AboutUsActivity::class.java))
          //  requireActivity().//overridePendingTransition(0,0)
        }

        rl_delete_account.setOnClickListener {
            val alertDialog = AlertDialog.Builder(context)
                .setTitle(resources.getString(R.string.delete))
                .setMessage(resources.getString(R.string.delete_account_meeasge))
                .setPositiveButton(resources.getString(R.string.yes), object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        val userID = PreferencesService.instance.getUserData!!.ID.toString()

                        val map: HashMap<Any, Any> = HashMap()
                        map.put("user_id", userID)
                        hitPostApiWithoutTokenParams(Constants.DELETE_ACCOUNT,
                            true, Constants.DELETE_ACCOUNT_API,map)
                    }
                })
                .setNegativeButton(resources.getString(R.string.no), object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog!!.dismiss()
                    }
                })
            alertDialog.show()
        }

        rl_wp.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            val url = "https://api.whatsapp.com/send?phone=+9647735032549&text=${URLEncoder.encode("", "UTF-8")}"
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        rl_call.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + "+9647735032549")
            startActivity(intent)
        }
        rl_insta.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://www.instagram.com/najaf_home_app/")
            startActivity(i)
        }

        rl_web.setOnClickListener {

            val appUrl = "https://play.google.com/store/apps/details?id=com.baghdadhomes"
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "$appUrl")
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"))
           /* val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://www.baghdadhome.com")
            startActivity(i)*/
        }

        rl_fb.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://web.facebook.com/najafhome12")
            startActivity(i)
        }
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if (apiType.equals(Constants.DELETE_ACCOUNT)){
            val model = Gson().fromJson(respopnse, LoginModel::class.java)
            if (model.success){
                Toast.makeText(context, model.message.toString(), Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
                LoginManager.getInstance().logOut()

                GoogleSignIn.getClient(requireActivity(),
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .build()).signOut()

                PreferencesService.instance.saveUserLoginStatus(false)
                startActivity(Intent(context, HomeActivity::class.java))
             //   requireActivity().overridePendingTransition(0,0)
            }
        } else if (apiType.equals(Constants.GET_USER_PROFILE)){
            val model = Gson().fromJson(respopnse, LoginModel::class.java)
            if (model.success){
                if(model.response!=null){
                    PreferencesService.instance.saveUserProfile(model.response)
                    setUserData()
                }
            }
        }
    }
}
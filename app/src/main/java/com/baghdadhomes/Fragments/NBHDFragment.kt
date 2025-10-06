package com.baghdadhomes.Fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.baghdadhomes.Activities.HomeActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.Activities.PropertiesSearchActivity
import com.baghdadhomes.Adapters.AdapterNBHDItems
import com.baghdadhomes.Models.NBHDArea
import com.baghdadhomes.Models.NBHDDataResponse
import com.baghdadhomes.Models.NBHDModel
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NBHDFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NBHDFragment : BaseFragment(), AdapterNBHDItems.ChildItemClick {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var adapter: AdapterNBHDItems
    lateinit var expandableListView:ExpandableListView
    var nbhdList:ArrayList<NBHDDataResponse> = ArrayList()
    private lateinit var et_search : EditText
    private lateinit var img_clear_search : ImageView
    private lateinit var rl_icon : RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_n_b_h_d, container, false)
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if(apiType.equals(Constants.NEIGHBORHOOD)){
            val model = Gson().fromJson(respopnse, NBHDModel::class.java)
            if(model.success){
                //nbhdList.addAll(model.response)
                val list : java.util.ArrayList<NBHDDataResponse> = java.util.ArrayList()
                if (PreferencesService.instance.getLanguage() != "ar") {
                    for (i in 0 until model.response.size) {
                        val listArea1 : java.util.ArrayList<NBHDArea> = java.util.ArrayList()
                        val listArea : java.util.ArrayList<NBHDArea> = java.util.ArrayList()
                        for (j in 0 until model.response.get(i).area.size){
                            if (!model.response.get(i).area.get(j).description.isNullOrEmpty()){
                                listArea.add(model.response.get(i).area.get(j))
                            }
                        }
                        listArea.sortWith{ lhs, rhs ->
                            lhs!!.description.compareTo(rhs!!.description)
                        }

                        listArea1.add(NBHDArea(getString(R.string.all),"all",getString(R.string.all)))
                        listArea1.addAll(listArea)
                        list.add(NBHDDataResponse(model.response[i].name,model.response[i].description,model.response[i].slug,listArea1))
                    }
                } else{
                    //list.addAll(model.response)
                    for (i in 0 until model.response.size) {
                        val listArea1 : java.util.ArrayList<NBHDArea> = java.util.ArrayList()
                        val listArea : java.util.ArrayList<NBHDArea> = java.util.ArrayList()
                        for (j in 0 until model.response.get(i).area.size){
                            listArea.add(model.response.get(i).area.get(j))
                        }
                        listArea.sortWith{ lhs, rhs ->
                            lhs!!.name.compareTo(rhs!!.name)
                        }

                        listArea1.add(NBHDArea(getString(R.string.all),"all",getString(R.string.all)))
                        listArea1.addAll(listArea)
                        list.add(NBHDDataResponse(model.response[i].name,model.response[i].description,model.response[i].slug,listArea1))
                    }
                }
                nbhdList.addAll(list)
                /*if(PreferencesService.instance.getLanguage().equals("ar")){
                    for(i in nbhdList){
                        Collections.sort(
                            i.area,
                            Comparator<NBHDArea?> { lhs, rhs ->
                                lhs!!.name.compareTo(rhs!!.name)
                            })
                    }
                }else{
                    for(i in nbhdList){
                        Collections.sort(
                            i.area,
                            Comparator<NBHDArea?> { lhs, rhs ->
                                lhs!!.description.lowercase().compareTo(rhs!!.description.lowercase())
                            })
                    }
                }*/
                adapter = AdapterNBHDItems(requireActivity(),nbhdList,this,false)
                expandableListView.setAdapter(adapter)
                //expandableListView.expandGroup(0)

            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expandableListView = view.findViewById(R.id.expandableListView)
        img_clear_search = view.findViewById(R.id.img_clear_search)
        et_search = view.findViewById(R.id.et_search)
        rl_icon = view.findViewById(R.id.rl_icon)

        rl_icon.setOnClickListener {
            ((context) as HomeActivity).setHomeFragment()
        }


        img_clear_search.setOnClickListener {
            et_search.setText("")
            requireContext().dismissKeyboard(et_search)
            if(this::adapter.isInitialized){
                adapter.filter.filter(et_search.text.toString())
            }

        }

        expandableListView.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                context!!.dismissKeyboard(v)
                return false
            }
        })

        et_search.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                requireActivity().dismissKeyboard(et_search)
                if(this::adapter.isInitialized){
                    adapter.filter.filter(et_search.text.toString())
                }

                true

            }else{
                false
            }

        }
        et_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(this@NBHDFragment::adapter.isInitialized){
                    adapter.filter.filter(s.toString())
                }


                if (et_search.text.isNotEmpty()){
                    img_clear_search.visibility = View.VISIBLE
                } else{
                    img_clear_search.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        if(isNetworkAvailable()){
            hitGetApiWithoutToken(Constants.NEIGHBORHOOD, true, Constants.NEIGHBORHOOD_API)
        }else{
            showToast(requireContext(), resources.getString(R.string.intenet_error))
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NBHDFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onChildClick(slug: ArrayList<String>) {
        Constants.slug = slug
        val intent = Intent(requireActivity(), PropertiesSearchActivity::class.java)
       // intent.putStringArrayListExtra("slug",slug)
        startActivity(intent)
    }
}
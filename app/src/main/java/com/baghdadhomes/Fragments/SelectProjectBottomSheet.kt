package com.baghdadhomes.Fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Activities.CityDetailActivity
import com.baghdadhomes.Activities.ProjectDetailActivity
import com.baghdadhomes.Adapters.ProjectsNameAdapter
import com.baghdadhomes.Models.ProjectData
import com.baghdadhomes.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SelectProjectBottomSheet : BottomSheetDialogFragment() {

    private lateinit var adapter: ProjectsNameAdapter
    private var projectList: List<ProjectData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve list from arguments
        arguments?.getString(ARG_PROJECT_LIST)?.let { json ->
            projectList = Gson().fromJson(json, object : TypeToken<List<ProjectData>>() {}.type)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.projects_bottom_sheet, container, false)

        val rv = v.findViewById<RecyclerView>(R.id.rvProjects)
        val  et_search = v.findViewById<EditText>(R.id.et_search_product)


        val close = v.findViewById<Button>(R.id.btnClose)

        adapter = ProjectsNameAdapter { selected ->
            dismiss()
            val intent:Intent = Intent(requireContext(), ProjectDetailActivity::class.java)
            intent.putExtra("propertyId",selected.id.toString())
            startActivity(intent)
            requireActivity().overridePendingTransition(0, 0)
        }
        rv.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        rv.adapter = adapter

        // data (normally from VM/Repo/args)

        projectList?.let { adapter.submit(it) }

        et_search.addTextChangedListener {text ->
            val query = text?.toString().orEmpty().trim()

            val filteredList = if (query.isEmpty()) {
                projectList
            } else {
                projectList!!.filter { project ->
                    // Use the property you want to filter by (example: postTitle or property_type)
                    project.postTitle?.contains(query, ignoreCase = true) == true ||
                            project.propertyType?.contains(query, ignoreCase = true) == true
                }
            }

            if (filteredList != null) {
                adapter.submit(filteredList)
            }

        }

        close.setOnClickListener { dismiss() }
        return v
    }

    companion object {
        private const val ARG_PROJECT_LIST = "project_list"

        fun show(
            fragmentManager: FragmentManager,
            list: List<ProjectData>
        ) {
            val bottomSheet = SelectProjectBottomSheet()
            val args = Bundle().apply {
                putString(ARG_PROJECT_LIST, Gson().toJson(list)) // Serialize list to JSON
            }
            bottomSheet.arguments = args
            bottomSheet.show(fragmentManager, "SelectProjectBottomSheet")
        }
    }
}

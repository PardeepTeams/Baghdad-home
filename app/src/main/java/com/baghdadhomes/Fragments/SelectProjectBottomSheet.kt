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
import com.baghdadhomes.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class SelectProjectBottomSheet : BottomSheetDialogFragment() {

    private lateinit var adapter: ProjectsNameAdapter
    private val allItems = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.projects_bottom_sheet, container, false)

        val rv = v.findViewById<RecyclerView>(R.id.rvProjects)
        val  et_search = v.findViewById<EditText>(R.id.et_search_product)


        val close = v.findViewById<Button>(R.id.btnClose)

        adapter = ProjectsNameAdapter { selected ->
            dismiss()
            startActivity(Intent(context, ProjectDetailActivity::class.java))
        }
        rv.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        rv.adapter = adapter

        // data (normally from VM/Repo/args)
        allItems.clear()
        allItems.addAll(
            listOf(
                "Al-Qudat 2", "Aram Village2", "Al-Jawahiri City", "Emerald City",
                "Lou Loua Residential Compound", "Baghdad View", "Alaska Towers",
                "English Tower", "Kurdistan City", "Buruj Residential Compound",
                "Al-Qudat 2", "Aram Village2", "Al-Jawahiri City", "Emerald City",
                "Lou Loua Residential Compound", "Baghdad View", "Alaska Towers",
                "English Tower", "Kurdistan City", "Buruj Residential Compound"
            )
        )
        adapter.submit(allItems)

        et_search.addTextChangedListener {
            val q = it?.toString().orEmpty().trim()
            val filtered = if (q.isEmpty()) allItems
            else allItems.filter { name -> name.contains(q, ignoreCase = true) }
            adapter.submit(filtered)
        }

        close.setOnClickListener { dismiss() }
        return v
    }

    companion object {
        fun show(fm: FragmentManager) =
            SelectProjectBottomSheet().show(fm, "SelectProjectBottomSheet")
    }
}

package com.sergiobelda.androidtodometer.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.sergiobelda.androidtodometer.R
import com.sergiobelda.androidtodometer.databinding.AddToDoFragmentBinding
import com.sergiobelda.androidtodometer.model.Project
import com.sergiobelda.androidtodometer.model.Task
import com.sergiobelda.androidtodometer.viewmodel.MainViewModel

/**
 *
 */
class AddToDoFragment : Fragment() {
    private var _binding: AddToDoFragmentBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()

    private var projectId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddToDoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    TASK -> {
                        binding.projectDropdownInput.visibility = View.VISIBLE
                        activity?.findViewById<FloatingActionButton>(R.id.create_button)?.setOnClickListener {
                            insertTask()
                        }
                    }
                    PROJECT -> {
                        binding.projectDropdownInput.visibility = View.GONE
                        activity?.findViewById<FloatingActionButton>(R.id.create_button)?.setOnClickListener {
                            insertProject()
                        }
                    }
                }
            }
        })
        activity?.findViewById<FloatingActionButton>(R.id.create_button)?.setOnClickListener {
            insertTask()
        }

        mainViewModel.projects.observe(viewLifecycleOwner, Observer {
            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.item_dropdown,
                it
            )
            binding.projectDropdown.setAdapter(adapter)
            binding.projectDropdown.onItemClickListener = object : AdapterView.OnItemClickListener {
                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    projectId = it[position].projectId
                }
            }
        })
    }

    private fun insertProject() {
        val name = binding.todoNameEditText.text.toString()
        val description = binding.todoDescriptionEditText.text.toString()
        mainViewModel.insertProject(Project(name, description))
        findNavController().navigateUp()
    }

    private fun insertTask() {
        val name = binding.todoNameEditText.text.toString()
        val description = binding.todoDescriptionEditText.text.toString()
        if (projectId < 1 || binding.projectDropdown.text.isNullOrBlank()) {
            (activity as? MainActivity)?.showSnackbar("Error")
        } else {
            mainViewModel.insertTask(Task(name, description, projectId))
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val TASK = 0
        private const val PROJECT = 1
        private const val TAG = "AddToDo"
    }
}

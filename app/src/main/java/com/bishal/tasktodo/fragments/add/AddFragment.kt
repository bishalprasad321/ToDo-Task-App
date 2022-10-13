package com.bishal.tasktodo.fragments.add

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bishal.tasktodo.R
import com.bishal.tasktodo.data.models.ToDoData
import com.bishal.tasktodo.data.viewmodel.ToDoViewModel
import com.bishal.tasktodo.fragments.SharedViewModel

@Suppress("DEPRECATION")
class AddFragment : Fragment() {
    private lateinit var title : EditText
    private lateinit var priority : Spinner
    private lateinit var description : EditText

    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel : SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)
        title = view.findViewById(R.id.titleInput)
        priority = view.findViewById(R.id.prioritySpinner)
        description = view.findViewById(R.id.descriptionText)
        // Set Menu
        setHasOptionsMenu(true)

        priority.onItemSelectedListener = mSharedViewModel.listener

        return view
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("inflater.inflate(R.menu.add_menu, menu)", "com.bishal.tasktodo.R")
    )
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_menu){
            insertDataToDb()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertDataToDb() {
        val mTitle = title.text.toString()
        val mPriority = priority.selectedItem.toString()
        val mDescription = description.text.toString()

        val validation = mSharedViewModel.verifyDataFromUser(mTitle, mDescription)
        if (validation){
            // Insert Data to Database
            val newData = ToDoData(
                0,
                mTitle,
                mSharedViewModel.parsePriority(mPriority),
                mDescription
            )
            mToDoViewModel.insertData(newData)
            Toast.makeText(requireContext(), "Successfully Added!!", Toast.LENGTH_SHORT).show()

            // Navigate Back
            findNavController().navigate(R.id.action_addFragment_to_toDoList)
        }
        else{
            Toast.makeText(requireContext(), "Please fill out all fields !!", Toast.LENGTH_SHORT).show()
        }
    }
}
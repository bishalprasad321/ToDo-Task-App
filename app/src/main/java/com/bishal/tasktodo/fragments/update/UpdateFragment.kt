package com.bishal.tasktodo.fragments.update

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bishal.tasktodo.R
import com.bishal.tasktodo.data.models.ToDoData
import com.bishal.tasktodo.data.viewmodel.ToDoViewModel
import com.bishal.tasktodo.fragments.SharedViewModel

@Suppress("DEPRECATION")
class UpdateFragment : Fragment() {

    private val args by navArgs<UpdateFragmentArgs>()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val mToDoViewModel: ToDoViewModel by viewModels()

    private lateinit var currentTittle : EditText
    private lateinit var currentDescription : EditText
    private lateinit var currentSpinner : Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update, container, false)

        currentTittle = view.findViewById(R.id.tittleUpdateInput)
        currentDescription = view.findViewById(R.id.descriptionUpdateText)
        currentSpinner = view.findViewById(R.id.priorityUpdateSpinner)

        // Set Menu
        setHasOptionsMenu(true)

        currentTittle.setText(args.currentItem.title)
        currentDescription.setText(args.currentItem.description)
        currentSpinner.setSelection(mSharedViewModel.parsePriorityToInt(args.currentItem.priority))
        currentSpinner.onItemSelectedListener = mSharedViewModel.listener

        return view
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "inflater.inflate(R.menu.update_menu, menu)",
        "com.bishal.tasktodo.R"
    )
    )
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_menu, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save_menu -> updateItem()
            R.id.delete_menu -> confirmDelete()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateItem(){
        val title = currentTittle.text.toString()
        val description = currentDescription.text.toString()
        val getPriority = currentSpinner.selectedItem.toString()

        val validation = mSharedViewModel.verifyDataFromUser(title, description)
        if (validation){
            val updatedItem = ToDoData(
                args.currentItem.id,
                title,
                mSharedViewModel.parsePriority(getPriority),
                description
            )
            mToDoViewModel.updateData(updatedItem)
            Toast.makeText(requireContext(), "Successfully updated!", Toast.LENGTH_SHORT).show()
            // Navigate back
            findNavController().navigate(R.id.action_updateFragment_to_toDoList)
        }
        else{
            Toast.makeText(requireContext(), "Please fill out all fields!", Toast.LENGTH_SHORT).show()
        }
    }

    // Show Alert Dialog to Delete Item and it's Data
    private fun confirmDelete() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_, _ ->
            mToDoViewModel.deleteData(args.currentItem)
            Toast.makeText(requireContext(), "Successfully Removed: '${args.currentItem.title}'", Toast.LENGTH_SHORT).show()
            // Navigate back to To Do List Fragment
            findNavController().navigate(R.id.action_updateFragment_to_toDoList)
        }
        builder.setNegativeButton("No"){_, _ ->}
        builder.setTitle("Delete '${args.currentItem.title}'?")
        builder.setMessage("Are you sure want to remove '${args.currentItem.title}'?")
        builder.create().show()
    }
}
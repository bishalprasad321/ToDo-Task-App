package com.bishal.tasktodo.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bishal.tasktodo.R
import com.bishal.tasktodo.data.viewmodel.ToDoViewModel
import com.bishal.tasktodo.fragments.SharedViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


@Suppress("DEPRECATION")
class ToDoListFragment : Fragment() {

    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val adapter: ToDoListAdapter by lazy { ToDoListAdapter() }

    private lateinit var emptyImage : ImageView
    private lateinit var emptyText : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_to_do_list, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        emptyImage = view.findViewById(R.id.emptyTaskView)
        emptyText = view.findViewById(R.id.emptyText)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        mToDoViewModel.getAllData.observe(viewLifecycleOwner) { data ->
            mSharedViewModel.checkIfDatabaseEmpty(data)
            adapter.setData(data)
        }

        mSharedViewModel.emptyDatabase.observe(viewLifecycleOwner) {
            showEmptyDatabase(it)
        }

        val addButton = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        addButton.setOnClickListener{
            findNavController().navigate(R.id.action_toDoList_to_addFragment)
        }

        setHasOptionsMenu(true)

        return view
    }

    private fun showEmptyDatabase(emptyDatabase: Boolean) {
        if (emptyDatabase){
            emptyImage.visibility = View.VISIBLE
            emptyText.visibility = View.VISIBLE
        }
        else{
            emptyImage.visibility = View.INVISIBLE
            emptyText.visibility = View.INVISIBLE
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "inflater.inflate(R.menu.to_do_list_menu, menu)",
        "com.bishal.tasktodo.R"
        )
    )
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.to_do_list_menu, menu)
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.onOptionsItemSelected(item)",
        "androidx.fragment.app.Fragment"
        )
    )
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_all_menu){
            confirmDeletion()
        }
        return super.onOptionsItemSelected(item)
    }

    // Show AlertDialog for confirmation of Deletion
    private fun confirmDeletion() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_, _ ->
            mToDoViewModel.deleteAll()
            Toast.makeText(requireContext(), "Successfully Removed Your ToDo Tasks!", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No"){_, _ ->}
        builder.setTitle("Delete All ToDo Tasks?")
        builder.setMessage("Are you sure you want to remove all ToDo Tasks?")
        builder.create().show()
    }
}
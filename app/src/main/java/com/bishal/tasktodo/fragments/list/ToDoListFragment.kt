package com.bishal.tasktodo.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bishal.tasktodo.R
import com.bishal.tasktodo.data.models.ToDoData
import com.bishal.tasktodo.data.viewmodel.ToDoViewModel
import com.bishal.tasktodo.fragments.SharedViewModel
import com.bishal.tasktodo.fragments.list.adapter.ToDoListAdapter
import com.bishal.tasktodo.utils.hideKeyboard
import com.bishal.tasktodo.utils.observeOnce
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.LandingAnimator


@Suppress("DEPRECATION")
class ToDoListFragment : Fragment(), SearchView.OnQueryTextListener {

    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val adapter: ToDoListAdapter by lazy { ToDoListAdapter() }

    private lateinit var emptyImage : ImageView
    private lateinit var emptyText : TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_to_do_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)

        emptyImage = view.findViewById(R.id.emptyTaskView)
        emptyText = view.findViewById(R.id.emptyText)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.itemAnimator = LandingAnimator().apply {
            addDuration = 300
        }

        swipeToDelete(recyclerView)

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

        // Set Menu
        setHasOptionsMenu(true)

        // Hide soft Keyboard
        hideKeyboard(requireActivity())

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

    private fun swipeToDelete(recyclerView: RecyclerView){
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = adapter.dataList[viewHolder.adapterPosition]
                mToDoViewModel.deleteData(deletedItem)
                adapter.notifyItemChanged(viewHolder.adapterPosition)
                // Restore the deleted item
                restoreDeletedData(viewHolder.itemView, deletedItem, viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedData(view: View, deletedItem: ToDoData, position: Int){
        val snackBar = Snackbar.make(
            view, "Deleted '${deletedItem.title}'",
            Snackbar.LENGTH_LONG
        )
        snackBar.setAction("Undo"){
            mToDoViewModel.insertData(deletedItem)
            adapter.notifyItemChanged(position)
            Toast.makeText(requireContext(), "'${deletedItem.title}' Restored!", Toast.LENGTH_SHORT).show()
        }
        snackBar.show()
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "inflater.inflate(R.menu.to_do_list_menu, menu)",
        "com.bishal.tasktodo.R"
        )
    )
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.to_do_list_menu, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.setOnQueryTextListener(this)
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.onOptionsItemSelected(item)",
        "androidx.fragment.app.Fragment"
        )
    )
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.delete_all_menu -> confirmDeletion()
            R.id.high_priority_menu -> mToDoViewModel.sortByHighPriority.observe(viewLifecycleOwner) { adapter.setData(it) }
            R.id.low_priority_menu -> mToDoViewModel.sortByLowPriority.observe(viewLifecycleOwner) { adapter.setData(it) }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null){
            searchThroughDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null){
            searchThroughDatabase(query)
        }
        return true
    }

    private fun searchThroughDatabase(query: String) {
        val searchQuery = "%$query%"

        mToDoViewModel.searchDatabase(searchQuery).observeOnce(viewLifecycleOwner) { list ->
            list?.let {
                adapter.setData(it)
            }
        }
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
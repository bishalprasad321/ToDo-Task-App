package com.bishal.tasktodo.fragments.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bishal.tasktodo.R
import com.bishal.tasktodo.data.models.Priority
import com.bishal.tasktodo.data.models.ToDoData


class ToDoListAdapter: RecyclerView.Adapter<ToDoListAdapter.ListViewHolder>() {

    private var dataList = emptyList<ToDoData>()

    class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.to_do_row_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val title = holder.itemView.findViewById<TextView>(R.id.titleText)
        val description = holder.itemView.findViewById<TextView>(R.id.description_text)
        val updateAction = holder.itemView.findViewById<ConstraintLayout>(R.id.to_do_row_background)

        title.text = dataList[position].title
        description.text = dataList[position].description

        updateAction.setOnClickListener{
            val action = ToDoListFragmentDirections.actionToDoListToUpdateFragment(dataList[position])
            holder.itemView.findNavController().navigate(action)
        }

        when(dataList[position].priority){
            Priority.HIGH -> holder.itemView.findViewById<CardView>(R.id.priority_indicator).setCardBackgroundColor(ContextCompat.getColor(
                holder.itemView.context,
                R.color.red
            ))
            Priority.MEDIUM -> holder.itemView.findViewById<CardView>(R.id.priority_indicator).setCardBackgroundColor(ContextCompat.getColor(
                holder.itemView.context,
                R.color.yellow
            ))
            Priority.LOW -> holder.itemView.findViewById<CardView>(R.id.priority_indicator).setCardBackgroundColor(ContextCompat.getColor(
                holder.itemView.context,
                R.color.green
            ))
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(toDoData: List<ToDoData>){
        this.dataList = toDoData
        notifyDataSetChanged()
    }
}
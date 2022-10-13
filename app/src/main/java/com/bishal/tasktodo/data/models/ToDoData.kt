package com.bishal.tasktodo.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Suppress("DEPRECATION")
@Entity(tableName = "to_do_table")
@Parcelize
data class ToDoData (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String,
    var priority: Priority,
    var description: String
): Parcelable
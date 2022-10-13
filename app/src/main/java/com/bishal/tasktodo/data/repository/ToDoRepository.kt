package com.bishal.tasktodo.data.repository

import androidx.lifecycle.LiveData
import com.bishal.tasktodo.data.ToDoDao
import com.bishal.tasktodo.data.models.ToDoData

class ToDoRepository(private val toDoDao: ToDoDao) {

    val getAllData: LiveData<List<ToDoData>> = toDoDao.getAllData()

    suspend fun insertData(toDoData: ToDoData){
        toDoDao.insertData(toDoData)
    }

    suspend fun updateData(toDoData: ToDoData){
        toDoDao.updateData(toDoData)
    }

    suspend fun deleteData(toDoData: ToDoData){
        toDoDao.deleteData(toDoData)
    }

    suspend fun deleteAll(){
        toDoDao.deleteAll()
    }

}
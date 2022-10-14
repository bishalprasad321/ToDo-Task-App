package com.bishal.tasktodo.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bishal.tasktodo.data.models.ToDoData

@Dao
interface ToDoDao {

    @Query(/* value = */ "SELECT * FROM to_do_table ORDER BY id ASC")
    fun getAllData(): LiveData<List<ToDoData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(toDoData: ToDoData)

    @Update
    suspend fun updateData(toDoData: ToDoData)

    @Delete
    suspend fun deleteData(toDoData: ToDoData)

    @Query(/* value = */ "DELETE from to_do_table")
    suspend fun deleteAll()

    @Query(/* value = */ "SELECT * FROM to_do_table WHERE title LIKE :searchQuery")
    fun searchDatabase(searchQuery: String): LiveData<List<ToDoData>>

    @Query(/* value = */ "SELECT * FROM to_do_table ORDER BY CASE WHEN priority LIKE 'H%' THEN 1 WHEN priority LIKE 'M%' THEN 2 WHEN priority LIKE 'L%' THEN 3 END")
    fun sortByHighPriority(): LiveData<List<ToDoData>>

    @Query(/* value = */ "SELECT * FROM to_do_table ORDER BY CASE WHEN priority LIKE 'L%' THEN 1 WHEN priority LIKE 'M%' THEN 2 WHEN priority LIKE 'H%' THEN 3 END")
    fun sortByLowPriority(): LiveData<List<ToDoData>>
}
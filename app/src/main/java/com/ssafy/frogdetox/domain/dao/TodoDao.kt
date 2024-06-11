package com.ssafy.frogdetox.domain.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ssafy.frogdetox.data.TodoDto

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo")
    suspend fun getAllTodo(): List<TodoDto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: TodoDto)

    @Query("UPDATE todo set " +
            "content = :content, " +
            "regTime = :regTime, " +
            "complete = :complete, " +
            "isAlarm = :isAlarm, " +
            "time = :time, " +
            "alarmCode = :alarmCode " +
            "WHERE id = :id")
    suspend fun update(
        id: String,
        content: String,
        regTime: Long,
        complete: Boolean,
        isAlarm: Boolean,
        time: String,
        alarmCode: Int
    )

    @Query("DELETE FROM todo WHERE id = :id")
    suspend fun delete(id: String)
}
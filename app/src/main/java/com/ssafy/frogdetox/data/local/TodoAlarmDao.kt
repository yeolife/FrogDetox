package com.ssafy.frogdetox.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ssafy.frogdetox.data.model.TodoAlarmDto

@Dao
interface TodoAlarmDao {
    @Query("SELECT * FROM active_alarms")
    suspend fun getAllTodoAlarm(): List<TodoAlarmDto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todoAlarm: TodoAlarmDto)

    @Query("DELETE FROM active_alarms WHERE alarm_code = :alarmcode")
    suspend fun delete(alarmcode: Int)
}
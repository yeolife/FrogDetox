package com.ssafy.frogdetox.domain

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ssafy.frogdetox.data.TodoAlarmDto
import com.ssafy.frogdetox.data.TodoDto
import com.ssafy.frogdetox.domain.dao.TodoAlarmDao
import com.ssafy.frogdetox.domain.dao.TodoDao

@Database(entities = [TodoDto::class, TodoAlarmDto::class], version = 1)
abstract class FrogDetoxDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao
    abstract fun todoAlarmDao(): TodoAlarmDao

    companion object {

        private var instance: FrogDetoxDatabase? = null

        fun getInstance(context: Context): FrogDetoxDatabase =
            instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    FrogDetoxDatabase::class.java,
                    "frogDetox.db"
                ).build().also { instance = it }
            }

        fun destroyInstance() {
            instance = null
        }
    }
}


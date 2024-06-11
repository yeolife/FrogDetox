package com.ssafy.frogdetox.domain

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ssafy.frogdetox.data.TodoDto
import com.ssafy.frogdetox.domain.dao.TodoDao

@Database(entities = [TodoDto::class], version = 1)
abstract class FrogDetoxDatabase : RoomDatabase(){

    abstract fun todoDao() : TodoDao

    companion object{

        private var instance : FrogDetoxDatabase? = null

        fun getInstance(context : Context) : FrogDetoxDatabase?{
            if(instance == null){
                synchronized(FrogDetoxDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        FrogDetoxDatabase::class.java,
                        "frogDetox.db")
                        .build()
                }
            }
            return instance
        }

        fun destroyInstance() {
            instance = null
        }
    }
}


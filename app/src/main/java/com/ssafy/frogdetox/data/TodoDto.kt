package com.ssafy.frogdetox.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "todo")
data class TodoDto(
    @PrimaryKey var id: String,
    @ColumnInfo(name = "uId") var uId : String,
    @ColumnInfo(name = "content") var content: String,
    @ColumnInfo(name = "regTime") var regTime: Long,
    @ColumnInfo(name = "complete") var complete: Boolean,
    @ColumnInfo(name = "isAlarm") var isAlarm : Boolean,
    @ColumnInfo(name = "time") var time : String,
    @ColumnInfo(name = "alarmCode") var alarmCode : Int,
): Serializable {
    constructor() : this("","","",0L,false,false,"",-1)
}
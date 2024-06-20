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
    @ColumnInfo(name = "time") var alarmTime : String,
    @ColumnInfo(name = "alarmCode") var alarmCode : Int,
    @ColumnInfo(name = "lastModified") var lastModified: Long, // 마지막 수정 시간
    @ColumnInfo(name = "isSynced") var isSynced: Boolean = false // 동기화 상태를 추적하는 필드
): Serializable {
    constructor() : this("","","",0L,false,false,"",-1, 0L, false)
}
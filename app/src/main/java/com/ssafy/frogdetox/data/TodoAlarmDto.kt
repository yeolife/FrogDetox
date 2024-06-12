package com.ssafy.frogdetox.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "active_alarms")
data class TodoAlarmDto (
    @PrimaryKey(autoGenerate = true)
    var id : Int, // 일련 번호
    var alarm_code : Int, // 알람 요청코드
    var time : String, // 시간
    var content : String // 알람 내용
): Serializable {
    constructor() : this(0,0,"","")
}
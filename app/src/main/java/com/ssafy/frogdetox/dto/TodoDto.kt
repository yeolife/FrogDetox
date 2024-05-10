package com.ssafy.frogdetox.dto

import java.io.Serializable

data class TodoDto(
    var id: String,
    var dateId: Int,
    var content: String,
    var regTime: Long,
    var isComplete: Boolean
): Serializable {
    constructor() : this("",0,"",0L,false)
}
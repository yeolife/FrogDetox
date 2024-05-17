package com.ssafy.frogdetox.dto

import java.io.Serializable

data class TodoDto(
    var id: String,
    var uId : String,
    var content: String,
    var regTime: Long,
    var complete: Boolean
): Serializable {
    constructor() : this("","","",0L,false)
}
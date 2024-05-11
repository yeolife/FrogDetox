package com.ssafy.frogdetox.dto

import java.io.Serializable

data class TodoDateDto (
    var id: String,
    var date: Long,
    var week: Int
) : Serializable {
    constructor() : this("",0L,0)
}
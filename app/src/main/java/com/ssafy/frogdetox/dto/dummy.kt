package com.ssafy.frogdetox.dto

object dummy {
    val todoList: MutableList<TodoDto> = mutableListOf(
        TodoDto(1, 1,"알고리즘 1문제", 1714213389, true),
        TodoDto(2, 1, "과목평가 공부", 1714213389, true),
        TodoDto(3, 1, "월말평가 공부", 1714213389, false),
        TodoDto(4, 2, "휴식하기", 1714213389, false),
        TodoDto(5, 2, "유튜브 보기", 1714213389, false),
        TodoDto(6, 2,"요리하기", 1714213389, false),
        TodoDto(7, 3, "밥 먹기", 1714213389, false),
        TodoDto(8, 3, "아아아아", 1714213389, false),
        TodoDto(9, 3,"할일이", 1714213389, false),
        TodoDto(10, 4, "없어용", 1714213389, false),
        TodoDto(11, 5, "!!!", 1714213389, false),
        TodoDto(12, 5, "히히", 1714213389, false),
        TodoDto(13, 7, "리사이클러뷰", 1714213389, false),
        TodoDto(14, 8, "내려가는지", 1714213389, false),
        TodoDto(15,  8, "테스트!!!!", 1714213389, false)
    )

    val todoDateList: MutableList<TodoDateDto> = mutableListOf(
        TodoDateDto(1, 1714213389, 1),
        TodoDateDto(2, 1714213389, 2),
        TodoDateDto(3, 1714213389, 3),
        TodoDateDto(4, 1714213389, 4),
        TodoDateDto(5, 1714213389, 5),
        TodoDateDto(6, 1714213389, 6),
        TodoDateDto(7, 1714213389, 7),
        TodoDateDto(8, 1714213389, 1),
        TodoDateDto(9, 1714213389, 2),
        TodoDateDto(10, 1714213389, 3)
    )
}

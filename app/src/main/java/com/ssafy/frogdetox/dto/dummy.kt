package com.ssafy.frogdetox.dto

object dummy {
    val todoList: MutableList<TodoDto> = mutableListOf(
        TodoDto(1, true, "알고리즘 1문제", 1714213389),
        TodoDto(2, false, "과목평가 공부", 1714213389),
        TodoDto(3, false, "월말평가 공부", 1714213389),
        TodoDto(4, false, "휴식하기", 1714213389),
        TodoDto(5, false, "유튜브 보기", 1714213389),
        TodoDto(6, true, "요리하기", 1714213389),
        TodoDto(7, false, "밥 먹기", 1714213389),
        TodoDto(8, false, "아아아아", 1714213389),
        TodoDto(9, true, "할일이", 1714213389),
        TodoDto(10, false, "없어용", 1714213389),
        TodoDto(11, false, "!!!", 1714213389),
        TodoDto(12, true, "히히", 1714213389),
        TodoDto(13, false, "리사이클러뷰", 1714213389),
        TodoDto(14, false, "내려가는지", 1714213389),
        TodoDto(15, true, "테스트!!!!", 1714213389)
    )

    val todoDateList: MutableList<TodoDateDto> = mutableListOf(
        TodoDateDto(1, true, 1),
        TodoDateDto(2, false, 2),
        TodoDateDto(3, true, 3),
        TodoDateDto(4, false, 4),
        TodoDateDto(5, true, 5),
        TodoDateDto(6, true, 6),
        TodoDateDto(7, true, 7),
        TodoDateDto(8, false, 1),
        TodoDateDto(9, true, 2),
        TodoDateDto(10, false, 3),
    )
}

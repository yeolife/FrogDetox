package com.ssafy.frogdetox.viewmodel

import androidx.lifecycle.ViewModel
import com.ssafy.frogdetox.dto.TodoDateDto
import com.ssafy.frogdetox.dto.TodoDto
import com.ssafy.frogdetox.dto.dummy

class TodoViewModel: ViewModel() {
    private var todoList: MutableList<TodoDto> = dummy.todoList
    private var todoDateList: MutableList<TodoDateDto> = dummy.todoDateList

    fun addTodo(todo: TodoDto) {
        todoList.add(todo)
    }

    fun deleteTodo(id: Int) {
        todoList.removeAt(id)
    }
}
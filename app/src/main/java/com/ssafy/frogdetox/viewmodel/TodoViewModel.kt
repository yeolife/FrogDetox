package com.ssafy.frogdetox.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssafy.frogdetox.dto.TodoDateDto
import com.ssafy.frogdetox.dto.TodoDto
import com.ssafy.frogdetox.dto.dummy

class TodoViewModel: ViewModel() {
    private var todoList: MutableList<TodoDto> = dummy.todoList
    private var todoDateList: MutableList<TodoDateDto> = dummy.todoDateList

    fun selectTodo(id: Int): TodoDto? {
        return todoList.find { it.id == id }
    }

    fun addTodo(todo: TodoDto) {
        todoList.add(todo)
    }

    fun deleteTodo(id: Int) {
        todoList.removeIf { it.id == id }
    }

    fun updateTodo(id: Int, todo: TodoDto) {
        todoList.find{it.id == id}?.let {
            it.content = todo.content
            it.dateId = todo.dateId
            it.time = todo.time
            it.isComplete = todo.isComplete
        }
    }

    fun selectTodoDate(id: Int): TodoDateDto? {
        return todoDateList.find { it.id == id }
    }

    fun addTodoDate(todoDate: TodoDateDto) {
        todoDateList.add(todoDate)
    }

    fun deleteTodoDate(id: Int) {
        todoDateList.removeIf { it.id == id }
    }

    fun updateTodoDate(id: Int, todoDate: TodoDateDto) {
        todoDateList.find{ it.id == id }?.let {
            it.date = todoDate.date
            it.week = todoDate.week
        }
    }
}
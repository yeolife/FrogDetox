package com.ssafy.frogdetox.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssafy.frogdetox.dto.TodoDateDto
import com.ssafy.frogdetox.dto.TodoDto
import com.ssafy.frogdetox.dto.dummy
import com.ssafy.frogdetox.network.TodoRepository
import com.ssafy.frogdetox.util.timeUtil.currentMillis

class TodoViewModel: ViewModel() {
    private val repo = TodoRepository()
    var todoList: MutableList<TodoDto> = dummy.todoList
    private var todoDateList: MutableList<TodoDateDto> = dummy.todoDateList

    private val _selectDay = MutableLiveData<Long>().apply {
        value = currentMillis
    }
    val selectDay : LiveData<Long>
        get() = _selectDay

    fun setSelectDay(day : Long){
        _selectDay.value = day
    }

    fun fetchData(): LiveData<MutableList<TodoDto>>{
        val mutableData = MutableLiveData<MutableList<TodoDto>>()
        selectDay.observeForever {
            repo.getData(it).observeForever {
                mutableData.value = it
            }
        }

        return mutableData
    }

    suspend fun selectTodo(id: String): TodoDto {
        return repo.todoSelect(id)
    }

    fun addTodo(todo: TodoDto) {
        repo.todoInsert(todo)
    }

    fun updateTodo(todo: TodoDto) {
        repo.todoUpdate(todo)
    }

    fun deleteTodo(id: String) {
        repo.todoDelete(id)
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
package com.ssafy.frogdetox.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssafy.frogdetox.dto.TodoDateDto
import com.ssafy.frogdetox.dto.TodoDto
import com.ssafy.frogdetox.dto.dummy
import com.ssafy.frogdetox.network.DateRepository
import com.ssafy.frogdetox.network.TodoRepository
import com.ssafy.frogdetox.util.timeUtil.currentMillis

class TodoViewModel: ViewModel() {
    private val todoRepo = TodoRepository()
    private val dateRepo = DateRepository()
//    var todoList: MutableList<TodoDto> = dummy.todoList
//    private var todoDateList: MutableList<TodoDateDto> = dummy.todoDateList

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
            todoRepo.getData(it).observeForever {
                mutableData.value = it
            }
        }
        return mutableData
    }

    suspend fun selectTodo(id: String): TodoDto {
        return todoRepo.todoSelect(id)
    }

    fun addTodo(todo: TodoDto) {
        todoRepo.todoInsert(todo)
    }

    fun updateTodo(todo: TodoDto) {
        todoRepo.todoUpdate(todo)
    }

    fun deleteTodo(id: String) {
        todoRepo.todoDelete(id)
    }

    fun fetchDateData(): LiveData<MutableList<TodoDateDto>>{
        return dateRepo.getData()
    }

    suspend fun selectTodoDate(id: String): TodoDateDto? {
        return dateRepo.dateSelect(id)
    }

    fun addTodoDate(todoDate: TodoDateDto) {
        return dateRepo.dateInsert(todoDate)
    }

    fun deleteTodoDate(id: String) {
        dateRepo.dateDelete(id)
    }

}
package com.ssafy.frogdetox.ui.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.frogdetox.data.model.TodoDto
import com.ssafy.frogdetox.data.remote.TodoRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset

class TodoViewModel: ViewModel() {
    private val todoRepo = TodoRepository()

    private val _selectDay = MutableLiveData<Long>().apply {
        value = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
    }

    val selectDay : LiveData<Long>
        get() = _selectDay

    fun setSelectDay(day : Long){
        _selectDay.value = day
    }

    fun fetchData(): LiveData<MutableList<TodoDto>> {
        val mutableData = MutableLiveData<MutableList<TodoDto>>().apply {
            value = mutableListOf()
        }

        selectDay.observeForever {
            viewModelScope.launch {
                todoRepo.getData(it).observeForever { data ->
                    mutableData.value = data ?: mutableListOf()
                }
            }
        }
        return mutableData
    }

    suspend fun currentTodo(): String{
        return todoRepo.getTodo()
    }
    suspend fun selectTodo(id: String): TodoDto {
        return todoRepo.todoSelect(id)
    }

    fun addTodo(todo: TodoDto) {
        viewModelScope.launch {
            todoRepo.todoInsert(todo)
        }
    }

    fun updateTodoContent(todo: TodoDto) {
        viewModelScope.launch {
            todoRepo.todoContentUpdate(todo)
        }
    }

    fun updateTodoComplete(id: String, isChecked: Boolean) {
        viewModelScope.launch {
            todoRepo.todoCheckUpdate(id, isChecked)
        }
    }

    fun deleteTodo(id: String) {
        viewModelScope.launch {
            todoRepo.todoDelete(id)
        }
    }

    fun deleteAllTodo() {
        viewModelScope.launch {
            todoRepo.todoDeleteAll()
        }
    }
}
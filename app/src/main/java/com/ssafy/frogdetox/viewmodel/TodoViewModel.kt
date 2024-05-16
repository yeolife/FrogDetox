package com.ssafy.frogdetox.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssafy.frogdetox.dto.TodoDto
import com.ssafy.frogdetox.network.TodoRepository
import java.time.LocalDate
import java.time.ZoneOffset

private const val TAG = "TodoViewModel_싸피"
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

    fun fetchData(): LiveData<MutableList<TodoDto>>{
        val mutableData = MutableLiveData<MutableList<TodoDto>>()
        selectDay.observeForever {
            Log.d(TAG, "fetchData: ${selectDay.value}")
            todoRepo.getData(it).observeForever {
                mutableData.value = it

                Log.d(TAG, "fetchData: $it")
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

    fun updateTodoContent(todo: TodoDto) {
        todoRepo.todoContentUpdate(todo)
    }

    fun updateTodoComplete(id: String, isChecked: Boolean) {
        todoRepo.todoCheckUpdate(id, isChecked)
    }

    fun deleteTodo(id: String) {
        todoRepo.todoDelete(id)
    }

}
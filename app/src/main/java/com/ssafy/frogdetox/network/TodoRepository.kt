package com.ssafy.frogdetox.network

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ssafy.frogdetox.dto.TodoDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.CountDownLatch


private const val TAG = "TodoRepository_싸피"
class TodoRepository {
    private val myRef = Firebase.database.getReference("Todo")

    fun getData(selectday : Long) : LiveData<MutableList<TodoDto>> {
        val mutableData = MutableLiveData<MutableList<TodoDto>>()

        myRef.addValueEventListener(object : ValueEventListener {
            val listData : MutableList<TodoDto> = mutableListOf()

            override fun onDataChange(snapshot: DataSnapshot) {
                val selectvalue = Instant.ofEpochMilli(selectday)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                lateinit var listvalue:LocalDate
                if(snapshot.exists()){
                    listData.clear()
                    for(curSnapshot in snapshot.children){
                        val getData = curSnapshot.getValue(TodoDto::class.java)
                        if (getData != null) {
                            listvalue = Instant.ofEpochMilli(getData.regTime)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            if(selectvalue==listvalue)
                                listData.add(getData)
                        }
                    }

                    mutableData.value = listData
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        return mutableData
    }

    suspend fun todoSelect(id: String): TodoDto {
        return withContext(Dispatchers.IO) {
            var todo = TodoDto()

            // count 만큼의 이벤트를 대기함
            val latch = CountDownLatch(1)

            myRef.child(id).get().addOnSuccessListener { it ->
                Log.i("firebase", "Got value ${it.value}")
                it.getValue(TodoDto::class.java)?.let { data ->
                    todo = data
                }
                latch.countDown()
            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
                latch.countDown()
            }

            // 모든 이벤트가 끝나면 await에서 대기하고 있던 스레드가 해제됨
            latch.await()

            return@withContext todo
        }
    }

    fun todoInsert(todo: TodoDto) {
        val key = myRef.push().key.toString()

        todo.id = key

        myRef.child(key).setValue(todo)
    }

    fun todoUpdate(todo: TodoDto) {
        val childUpdates: Map<String, Any> = mapOf("content" to todo.content)

        myRef.child(todo.id).updateChildren(childUpdates)
    }

    fun todoDelete(id: String) {
        myRef.child(id).removeValue()
    }
}
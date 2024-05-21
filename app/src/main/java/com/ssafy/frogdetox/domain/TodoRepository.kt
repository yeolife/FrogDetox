package com.ssafy.frogdetox.domain

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ssafy.frogdetox.view.LoginActivity
import com.ssafy.frogdetox.data.TodoDto
import com.ssafy.frogdetox.common.LongToLocalDate
import com.ssafy.frogdetox.common.SharedPreferencesManager
import com.ssafy.frogdetox.common.SharedPreferencesManager.getUId
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.concurrent.CountDownLatch

class TodoRepository {
    private val myRef = Firebase.database.getReference("Todo")

    fun getData(selectday : Long) : LiveData<MutableList<TodoDto>> {
        val mutableData = MutableLiveData<MutableList<TodoDto>>()

        myRef.addValueEventListener(object : ValueEventListener {
            val listData : MutableList<TodoDto> = mutableListOf()

            override fun onDataChange(snapshot: DataSnapshot) {
                val selectvalue = LongToLocalDate(selectday)
                lateinit var listvalue: LocalDate
                if(snapshot.exists()){
                    listData.clear()
                    for(curSnapshot in snapshot.children){
                        val getData = curSnapshot.getValue(TodoDto::class.java)
                        if (getData != null) {
                            if(getData.uId== getUId()){
                                listvalue = LongToLocalDate(getData.regTime)
                                if(selectvalue==listvalue)
                                    listData.add(getData)
                            }
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
    suspend fun getThreeTodo(): String {
        val deferred = CompletableDeferred<String>()

        var result = ""
        var count = 0

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (curSnapshot in snapshot.children) {
                        if (count >= 6) break
                        val getData = curSnapshot.getValue(TodoDto::class.java)
                        if (getData != null) {
                            if (getData.uId == getUId()) {
                                result += getData.content + ", "
                                count++
                            }
                        }
                    }
                    deferred.complete(result)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                deferred.completeExceptionally(error.toException())
            }
        })

        return deferred.await()
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
            }.addOnFailureListener {
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

    fun todoContentUpdate(todo: TodoDto) {
        val childUpdates: Map<String, Any> = mapOf("content" to todo.content, "alarm" to todo.isAlarm,"alarmCode" to todo.alarmCode,"time" to todo.time)

        myRef.child(todo.id).updateChildren(childUpdates)
    }

    fun todoCheckUpdate(id: String, complete: Boolean) {
        val childUpdates: Map<String, Any> = mapOf("complete" to complete)

        myRef.child(id).updateChildren(childUpdates)
    }

    fun todoDelete(id: String) {
        myRef.child(id).removeValue()
    }
}
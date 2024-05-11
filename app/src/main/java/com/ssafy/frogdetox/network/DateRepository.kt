package com.ssafy.frogdetox.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ssafy.frogdetox.dto.TodoDateDto
import com.ssafy.frogdetox.dto.TodoDto
import com.ssafy.frogdetox.dto.dummy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch

private const val TAG = "DateRepository"
class DateRepository {
    private val myRef = Firebase.database.getReference("Date")

    fun getData() : LiveData<MutableList<TodoDateDto>> {
        val mutableData = MutableLiveData<MutableList<TodoDateDto>>()

        myRef.addValueEventListener(object : ValueEventListener {
            val listData : MutableList<TodoDateDto> = mutableListOf()

            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onDataChange: 00000000000")
                if(snapshot.exists()){
                    listData.clear()
                    for(curSnapshot in snapshot.children){
                        val getData = curSnapshot.getValue(TodoDateDto::class.java)
                        Log.d(TAG, "onDataChange: ${getData}")
                        if (getData != null) {
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

    suspend fun dateSelect(id: String): TodoDateDto {
        return withContext(Dispatchers.IO) {
            var tempDate = TodoDateDto()

            // count 만큼의 이벤트를 대기함
            val latch = CountDownLatch(1)

            myRef.child(id).get().addOnSuccessListener { it ->
                Log.i("firebase", "Got value ${it.value}")
                it.getValue(TodoDateDto::class.java)?.let { data ->
                    tempDate = data
                }
                latch.countDown()
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
                latch.countDown()
            }

            // 모든 이벤트가 끝나면 await에서 대기하고 있던 스레드가 해제됨
            latch.await()

            return@withContext tempDate
        }
    }

    fun dateInsert(tempDate: TodoDateDto) {
        val key = myRef.push().key.toString()

        tempDate.id = key

        myRef.child(key).setValue(tempDate)
    }

    fun dateDelete(id: String) {
        myRef.child(id).removeValue()
    }
}
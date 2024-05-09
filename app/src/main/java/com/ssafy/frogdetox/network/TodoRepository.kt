package com.ssafy.frogdetox.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ssafy.frogdetox.dto.TodoDto
import com.ssafy.frogdetox.viewmodel.TodoViewModel

private const val TAG = "TodoRepository_싸피"
class TodoRepository {

    fun getData(selectday : Long) : LiveData<MutableList<TodoDto>> {
        val mutableData = MutableLiveData<MutableList<TodoDto>>()
        val myRef = Firebase.database.getReference("Todo")
        Log.d(TAG, "getData: $myRef")

        myRef.addValueEventListener(object : ValueEventListener {
            val listData : MutableList<TodoDto> = mutableListOf()

            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    listData.clear()
                    for(curSnapshot in snapshot.children){
                        val getData = curSnapshot.getValue(TodoDto::class.java)
                        if (getData != null) {
                            if((selectday>=(getData.regTime)&&selectday<(getData.regTime)+86400000))
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
}
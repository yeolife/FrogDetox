package com.ssafy.frogdetox.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.frogdetox.MainActivity
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.adapter.TodoDateAdapter
import com.ssafy.frogdetox.adapter.TodoListAdapter
import com.ssafy.frogdetox.databinding.FragmentTodoBinding
import com.ssafy.frogdetox.dto.TodoDateDto
import com.ssafy.frogdetox.dto.dummy
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

class TodoFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!

    private lateinit var todoRecycler: RecyclerView
    private lateinit var todoAdapter: TodoListAdapter

    private lateinit var todoDateRecycler: RecyclerView
    private lateinit var todoDateAdapter: TodoDateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }


    private fun initRecyclerView() {
        todoRecycler = binding.rvTodo
        todoAdapter = TodoListAdapter(dummy.todoList)

        todoDateRecycler = binding.rvDate
        todoDateAdapter = TodoDateAdapter(requireContext(),dummy.todoDateList)

        todoAdapter.itemClickListener = object: TodoListAdapter.ItemClickListener {
            override fun onClick(position: Int) {
                Toast.makeText(requireActivity(), "{$position}번 투두", Toast.LENGTH_SHORT).show()
            }
        }

        todoRecycler.apply {
            adapter = todoAdapter

            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }

        todoDateRecycler.apply {
            adapter = todoDateAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
//            layoutManager = GridLayoutManager(context, 7)
        }
    }

}
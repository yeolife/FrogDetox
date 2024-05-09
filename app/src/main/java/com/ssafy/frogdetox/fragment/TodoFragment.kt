package com.ssafy.frogdetox.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.frogdetox.MainActivity
import com.ssafy.frogdetox.adapter.TodoDateAdapter
import com.ssafy.frogdetox.adapter.TodoListAdapter
import com.ssafy.frogdetox.databinding.DialogTodomakeBinding
import com.ssafy.frogdetox.databinding.FragmentTodoBinding
import com.ssafy.frogdetox.dto.TodoDateDto
import com.ssafy.frogdetox.dto.dummy
import com.ssafy.frogdetox.viewmodel.TodoViewModel

private const val TAG = "TodoFragment_싸피"
class TodoFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!
    lateinit var binding2 : DialogTodomakeBinding
    private lateinit var todoRecycler: RecyclerView
    private lateinit var todoAdapter: TodoListAdapter

    private lateinit var todoDateRecycler: RecyclerView
    private lateinit var todoDateAdapter: TodoDateAdapter

    val viewModel : TodoViewModel by viewModels()


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
        binding2 = DialogTodomakeBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun observerData(){
        viewModel.fetchData().observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "observerData: ${todoAdapter.list}")
            todoAdapter.list=it
            todoAdapter.notifyDataSetChanged()
        })
    }
    private fun initRecyclerView() {
        todoRecycler = binding.rvTodo
        todoAdapter = TodoListAdapter(viewModel.todoList)
        observerData()

        todoDateRecycler = binding.rvDate
        todoDateAdapter = TodoDateAdapter(requireContext())
        todoDateAdapter.submitList(getTodoDateData())

        todoDateAdapter.itemClickListener = object :TodoDateAdapter.ItemClickListener{
            override fun onClick(dto: TodoDateDto) {
                Log.d(TAG, "onClick: ${dto.id} ${dto.date} ${dto.week}")
                viewModel.setSelectDay(dto.date)
                Log.d(TAG, "onClick: ${viewModel.selectDay.value}")
            }
        }
        
        todoAdapter.itemClickListener = object: TodoListAdapter.ItemClickListener {
            override fun onClick(position: Int) {
                Toast.makeText(requireActivity(), "{$position}번 투두", Toast.LENGTH_SHORT).show()
                val dialog = AlertDialog.Builder(requireContext())
                    .setPositiveButton("확인") { dialog, _ ->
                        // TODO: 알람시간 설정, db 수정
                        dialog.dismiss()
                    }
                    .setNegativeButton("취소") { dialog, _ ->
                        dialog.dismiss()
                    }
                binding2.switch2.setOnClickListener {
                    if(binding2.calendarView.visibility==View.VISIBLE){
                        binding2.calendarView.visibility = View.GONE
                    }
                    else{
                        binding2.calendarView.visibility = View.VISIBLE
                    }
                }

                if(binding2.root.parent != null){
                    ((binding2.root.parent) as ViewGroup).removeView(binding2.root)
                }

                dialog.setView(binding2.root)
                dialog.show()
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

    private fun getTodoDateData(): MutableList<TodoDateDto> {
        return dummy.todoDateList
    }
}
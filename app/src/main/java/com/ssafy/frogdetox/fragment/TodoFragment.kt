package com.ssafy.frogdetox.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekDayBinder
import com.ssafy.frogdetox.MainActivity
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.adapter.ItemClickListener
import com.ssafy.frogdetox.adapter.TodoDateAdapter
import com.ssafy.frogdetox.adapter.TodoListAdapter
import com.ssafy.frogdetox.databinding.DialogTodomakeBinding
import com.ssafy.frogdetox.databinding.FragmentTodoBinding
import com.ssafy.frogdetox.databinding.CalendarDayLayoutBinding
import com.ssafy.frogdetox.dto.TodoDateDto
import com.ssafy.frogdetox.dto.TodoDto
import com.ssafy.frogdetox.util.displayText
import com.ssafy.frogdetox.util.timeUtil
import com.ssafy.frogdetox.viewmodel.TodoViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter


private const val TAG = "TodoFragment_싸피"
@RequiresApi(Build.VERSION_CODES.O)
class TodoFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!
    private lateinit var bindingTMD : DialogTodomakeBinding

    private lateinit var todoRecycler: RecyclerView
    private lateinit var todoAdapter: TodoListAdapter

    private lateinit var todoDateRecycler: RecyclerView
    private lateinit var todoDateAdapter: TodoDateAdapter

    private var selectedDate = LocalDate.now()

    private val dateFormatter = DateTimeFormatter.ofPattern("dd")


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
        bindingTMD = DialogTodomakeBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTodoRecyclerView()
//        initTodoDateRecyclerView()
        class DayViewContainer(view: View) : ViewContainer(view) {
            val bind = CalendarDayLayoutBinding.bind(view)
            lateinit var day: WeekDay

            init {
                view.setOnClickListener {
                    if (selectedDate != day.date) {
                        val oldDate = selectedDate
                        selectedDate = day.date
                        binding.rvDate.notifyDateChanged(day.date)
                        oldDate?.let { binding.rvDate.notifyDateChanged(it) }
                    }
                }
            }

            fun bind(day: WeekDay) {
                this.day = day
                bind.exSevenDateText.text = dateFormatter.format(day.date)
                bind.exSevenDayText.text = day.date.dayOfWeek.displayText()

                val colorRes = if (day.date == selectedDate) {
                    R.color.LightGreen
                } else {
                    R.color.white
                }
//                bind.exSevenDateText.setTextColor(view.context.getColorCompat(colorRes))
                bind.exSevenSelectedView.isVisible = day.date == selectedDate
            }
        }

            binding.rvDate.dayBinder = object : WeekDayBinder<DayViewContainer> {
                override fun create(view: View) = DayViewContainer(view)
                override fun bind(container: DayViewContainer, data: WeekDay) = container.bind(data)
            }

//            binding.rvDate.weekScrollListener = { weekDays ->
//                binding.exSevenToolbar.title = getWeekPageTitle(weekDays)
//            }

            val currentMonth = YearMonth.now()
            binding.rvDate.setup(
                currentMonth.minusMonths(5).atStartOfMonth(),
                currentMonth.plusMonths(5).atEndOfMonth(),
                firstDayOfWeekFromLocale(),
            )
            binding.rvDate.scrollToDate(LocalDate.now())
        }

    private fun initTodoRecyclerView() {
        todoRecycler = binding.rvTodo

        todoAdapter = TodoListAdapter(ItemClickListener { id, state ->
            if(state == TODO_INSERT) {
                todoRegisterDialog(TODO_INSERT, id)
            } else if(state == TODO_UPDATE){
                todoRegisterDialog(TODO_UPDATE, id)
            }
        })

        todoRecycler.apply {
            adapter = todoAdapter

            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }

        observerData()
    }

    private fun todoRegisterDialog(state: Int, id: String) {
        var todo = TodoDto()

        bindingTMD.editTextText2.setText("")
        bindingTMD.switch2.isChecked = false
        bindingTMD.calendarView.visibility = View.GONE

        if(state == TODO_UPDATE) {
            lifecycleScope.launch {
                viewModel.selectTodo(id).let {
                    todo = it
                }

                bindingTMD.editTextText2.setText(todo.content)
            }
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setPositiveButton("확인") { dialog, _ ->
                todo.content = bindingTMD.editTextText2.text.toString()

                if(state == TODO_INSERT) {
                    viewModel.selectDay.value?.let {
                        todo.regTime = it
                    }

                    viewModel.addTodo(todo)

                } else {
                    viewModel.updateTodo(todo)
                }

                // TODO. 알람 등록

                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
        bindingTMD.switch2.setOnClickListener {
            if(bindingTMD.switch2.isChecked) {
                bindingTMD.calendarView.visibility = View.VISIBLE
            } else {
                bindingTMD.calendarView.visibility = View.GONE
            }
        }

        if(bindingTMD.root.parent != null){
            ((bindingTMD.root.parent) as ViewGroup).removeView(bindingTMD.root)
        }

        dialog.setView(bindingTMD.root)
        dialog.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun observerData() {
        viewModel.fetchData().observe(viewLifecycleOwner, Observer {
            todoAdapter.addHeaderAndSubmitList(it)
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initTodoDateRecyclerView() {
        todoDateRecycler = binding.rvDate
        todoDateAdapter = TodoDateAdapter(requireContext())
        observeDate()

        todoDateAdapter.itemClickListener = object :TodoDateAdapter.ItemClickListener {
            override fun onClick(dto: TodoDateDto) {
                Log.d(TAG, "onClick: ${dto.id} ${dto.date} ${dto.week}")
                viewModel.setSelectDay(dto.date)
                Log.d(TAG, "onClick: ${viewModel.selectDay.value}")
            }
        }

        todoDateRecycler.apply {
            adapter = todoDateAdapter

            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeDate() {
        viewModel.fetchDateData().observe(viewLifecycleOwner, Observer{
            todoDateAdapter.submitList(it)

            // Date 리사이클러뷰의 시작을 오늘로
            val display = mainActivity.applicationContext?.resources?.displayMetrics
            val screenWidthPx = display?.widthPixels ?: 0
            val itemWidthPx = 54
            val offset = (screenWidthPx / 2 + itemWidthPx)

            for (index in todoDateAdapter.currentList.indices) {
                if(timeUtil.compareDay(todoDateAdapter.currentList[index].date, timeUtil.currentMillis)) {
                    (todoDateRecycler.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(index + 1, if(offset >= 0) offset else 0);
                    
                    break
                }
            }
        })
    }

    companion object {
        const val TODO_INSERT = 0
        const val TODO_UPDATE = 1
    }
}
package com.ssafy.frogdetox.ui.todo

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekDayBinder
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.common.displayText
import com.ssafy.frogdetox.common.getWeekPageTitle
import com.ssafy.frogdetox.data.local.FrogDetoxDatabase
import com.ssafy.frogdetox.data.local.SharedPreferencesManager
import com.ssafy.frogdetox.databinding.CalendarDayLayoutBinding
import com.ssafy.frogdetox.databinding.FragmentTodoBinding
import com.ssafy.frogdetox.ui.MainActivity
import com.ssafy.frogdetox.ui.todo.todoDialog.PersonalDialogFragment
import com.ssafy.frogdetox.ui.todo.todoDialog.TodoRegisterDialog
import com.ssafy.frogdetox.ui.todo.todoListSwiper.SwipeController
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class TodoFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!

    private lateinit var todoRegisterDialog: TodoRegisterDialog

    private lateinit var todoRecycler: RecyclerView
    private lateinit var todoAdapter: TodoListAdapter
    private lateinit var alarmManager: AlarmManager

    private var selectedDate = LocalDate.now()

    private val dateFormatter = DateTimeFormatter.ofPattern("dd")

    private val viewModel: TodoViewModel by activityViewModels()
    private val db: FrogDetoxDatabase by lazy {
        FrogDetoxDatabase.getInstance(requireContext())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        alarmManager = AlarmManager(mainActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoBinding.inflate(inflater, container, false)

        todoRegisterDialog = TodoRegisterDialog(
            this,
            viewModel,
            db,
            alarmManager
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvName.text = "${SharedPreferencesManager.getUserName()}님"
        binding.ivFrog.load(SharedPreferencesManager.getUserProfile()) {
            transformations(CircleCropTransformation())
            placeholder(R.drawable.ic_launcher_foreground)
        }

        binding.lyPersonal.setOnClickListener {
            mainActivity.changeFragmentView(MainActivity.SETTING_FRAGMENT)
        }

        observerTodoList()
        initTodoRecyclerView()
        initTodoDateCalendar()
    }

    private fun initTodoRecyclerView() {
        todoRecycler = binding.rvTodo

        todoAdapter = TodoListAdapter()

        todoAdapter.todoClickListener = object : TodoListAdapter.TodoClickListener {
            override fun onTodoClick(id: String, state: Int) {
                if (state == TODO_UPDATE) {
                    showTodoRegisterDialog(TODO_UPDATE, id)
                    lifecycleScope.launch {
                        db.todoAlarmDao().delete(viewModel.selectTodo(id).alarmCode)
                    }
                } else if (state == TODO_INSERT) {
                    showTodoRegisterDialog(TODO_INSERT, "-1")
                }
            }
        }

        todoRecycler.apply {
            adapter = todoAdapter

            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }

        val itemTouchHelper = ItemTouchHelper(SwipeController(todoAdapter))
        itemTouchHelper.attachToRecyclerView(binding.rvTodo)

        todoAdapter.todoSwipeListener = object : TodoListAdapter.TodoSwipeListener {
            override fun onItemDelete(id: String) {
                lifecycleScope.launch {
                    viewModel.selectTodo(id).alarmCode.let {
                        alarmManager.cancelAlarm(it)
                    }
                    db.todoAlarmDao().delete(viewModel.selectTodo(id).alarmCode)
                    viewModel.deleteTodo(id)
                }
            }
        }

        todoAdapter.todoCompleteListener = object : TodoListAdapter.TodoCompleteListener {
            override fun onChecked(id: String, isChecked: Boolean) {
                lifecycleScope.launch {
                    db.todoAlarmDao().delete(viewModel.selectTodo(id).alarmCode)
                    viewModel.updateTodoComplete(id, isChecked)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun observerTodoList() {
        viewModel.fetchData().observe(viewLifecycleOwner, Observer {
            it.sortBy { it.complete }
            todoAdapter.addHeaderAndSubmitList(it)
        })
    }

    // ----------------------- TodoDate

    private fun initTodoDateCalendar() {
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
                    viewModel.setSelectDay(
                        day.date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
                    )
                }
            }

            fun bind(day: WeekDay) {
                this.day = day
                bind.exSevenDateText.text = dateFormatter.format(day.date)
                bind.exSevenDayText.text = day.date.dayOfWeek.displayText()

//                bind.exSevenDateText.setTextColor(view.context.getColorCompat(colorRes))
                bind.exSevenSelectedView.isVisible = (day.date == selectedDate)
            }
        }

        binding.rvDate.dayBinder = object : WeekDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: WeekDay) = container.bind(data)
        }

        binding.rvDate.weekScrollListener = { weekDays ->
//            binding.exSevenToolbar.title = getWeekPageTitle(weekDays)
            binding.tvWeek.text = getWeekPageTitle(weekDays)
        }

        val currentMonth = YearMonth.now()
        binding.rvDate.setup(
            currentMonth.minusMonths(30).atStartOfMonth(),
            currentMonth.plusMonths(30).atEndOfMonth(),
            firstDayOfWeekFromLocale(),
        )
        binding.rvDate.scrollToDate(LocalDate.now())
    }

    private fun showTodoRegisterDialog(state: Int, id: String) {
        todoRegisterDialog.alertDialog(state, id)
    }

    companion object {
        const val TODO_INSERT = 0
        const val TODO_UPDATE = 1
    }
}
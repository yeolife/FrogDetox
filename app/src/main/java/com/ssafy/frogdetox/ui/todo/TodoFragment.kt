package com.ssafy.frogdetox.ui.todo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekDayBinder
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.common.LocalAPIKey
import com.ssafy.frogdetox.common.LongToLocaldate
import com.ssafy.frogdetox.common.Permission
import com.ssafy.frogdetox.common.displayText
import com.ssafy.frogdetox.common.getTimeInMillis
import com.ssafy.frogdetox.common.getTodayInMillis
import com.ssafy.frogdetox.common.getWeekPageTitle
import com.ssafy.frogdetox.data.local.FrogDetoxDatabase
import com.ssafy.frogdetox.data.local.SharedPreferencesManager
import com.ssafy.frogdetox.data.local.SharedPreferencesManager.getUId
import com.ssafy.frogdetox.data.model.TodoAlarmDto
import com.ssafy.frogdetox.data.model.TodoDto
import com.ssafy.frogdetox.databinding.CalendarDayLayoutBinding
import com.ssafy.frogdetox.databinding.DialogTodomakeBinding
import com.ssafy.frogdetox.databinding.FragmentTodoBinding
import com.ssafy.frogdetox.ui.LoginActivity
import com.ssafy.frogdetox.ui.MainActivity
import com.ssafy.frogdetox.ui.detox.DetoxBlockingBottomSheetFragment
import com.ssafy.frogdetox.ui.todo.todoListSwiper.SwipeController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date

class TodoFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!
    private lateinit var bindingTMD: DialogTodomakeBinding

    private lateinit var todoRecycler: RecyclerView
    private lateinit var todoAdapter: TodoListAdapter
    private lateinit var alarmManager: AlarmManager

    private var selectedDate = LocalDate.now()

    private val dateFormatter = DateTimeFormatter.ofPattern("dd")

    val viewModel: TodoViewModel by viewModels()
    private val db: FrogDetoxDatabase by lazy {
        FrogDetoxDatabase.getInstance(requireContext())
    }

    private var userImgUrl: String? = null
    private var userName: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userImgUrl = SharedPreferencesManager.getUserProfile()
        userName = SharedPreferencesManager.getUserName()
        alarmManager = AlarmManager(mainActivity)
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
        binding.tvName.text = userName + "님"
        binding.ivFrog.load(userImgUrl) {
            transformations(CircleCropTransformation())
            placeholder(R.drawable.ic_launcher_foreground)
        }
        binding.btnLogout.setOnClickListener {
            goLoginWithState(1)
        }

        binding.lyPersonal.setOnClickListener {
            val dialog = PersonalDialogFragment()
            dialog.show(childFragmentManager, "PersonalDialogFragment")
        }

        observerTodoList()
        initTodoRecyclerView()
        initTodoDateCalendar()
    }

    private fun checkPermission(): Boolean {
        var notiPermission = NotificationManagerCompat.from(mainActivity).areNotificationsEnabled()

        var reminderPermission = Permission.isExactAlarmPermissionGranted(mainActivity)

        if (!notiPermission || !reminderPermission) {
            Toast.makeText(
                requireContext(),
                "알림 받기 기능을 사용하시려면 아래 권한을 허용하셔야합니다.",
                Toast.LENGTH_SHORT
            ).show()
            val bottomSheet = DetoxBlockingBottomSheetFragment.newInstance(DetoxBlockingBottomSheetFragment.TODO_PERMISSION)
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }
        notiPermission = NotificationManagerCompat.from(mainActivity).areNotificationsEnabled()

        reminderPermission = Permission.isExactAlarmPermissionGranted(mainActivity)
        return notiPermission && reminderPermission
    }

    private fun initTodoRecyclerView() {
        todoRecycler = binding.rvTodo

        todoAdapter = TodoListAdapter()

        todoAdapter.todoClickListener = object : TodoListAdapter.TodoClickListener {
            override fun onTodoClick(id: String, state: Int) {
                if (state == TODO_UPDATE) {
                    todoRegisterDialog(TODO_UPDATE, id)
                    lifecycleScope.launch {
                        db.todoAlarmDao().delete(viewModel.selectTodo(id).alarmCode)
                    }
                } else if (state == TODO_INSERT) {
                    todoRegisterDialog(TODO_INSERT, "-1")
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

    @OptIn(DelicateCoroutinesApi::class)
    private fun todoRegisterDialog(state: Int, id: String) {
        var todo = TodoDto()

        bindingTMD.etTodo.setText("")
        bindingTMD.switch2.isChecked = false
        bindingTMD.calendarView.isVisible = false
        bindingTMD.lyAiText.isVisible = false
        bindingTMD.lyResult.isVisible = false
        bindingTMD.saylayout.isVisible = false
        //network작업 Runnable --> lambda
        bindingTMD.tvAiText.setOnClickListener {
            val apiKey = LocalAPIKey.getSecretKey(mainActivity, "chatgpt_api_key")
            GlobalScope.launch(Dispatchers.IO) {
                val job = CoroutineScope(Dispatchers.Main).launch {
                    bindingTMD.tvloading.text = "흠..."
                    bindingTMD.tvloading.isVisible = true
                    bindingTMD.lyAiText.isVisible = false

                    for (i in 0 .. 5) {
                        bindingTMD.tvloading.text = bindingTMD.tvloading.text.toString() + " 🤔"
                        delay(500) // 1초마다 일시 중지
                    }
                }

                runCatching {
                    val todoString = viewModel.currentTodo()
                    val prompt = if (todoString != "") {
                        "평소 ${todoString} 같은 일을 하는 사람에게 할 일을 다양한 느낌으로'~~하기' 형식으로 10글자 내외로 하나만 추천해줘. 출력은 본론만 간결히 한줄로."
                    } else {
                        "일상적인 할 일 하나 '~~하기' 형식으로 추천해줘. 출력은 본론만 간결히 한줄로."
                    }
                    val url = URL("https://api.openai.com/v1/chat/completions")
                    val connection = url.openConnection() as HttpURLConnection

                    connection.requestMethod = "POST"
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.setRequestProperty("Authorization", "Bearer $apiKey")
                    connection.doOutput = true // outputStream으로 post로 데이터 전송

                    val out = BufferedWriter(OutputStreamWriter(connection.outputStream))
                    out.write(
                        """
                {    "model": "gpt-3.5-turbo",    "messages": [{"role": "user", "content": "$prompt"}],    "temperature": 0.7}
                """.trimIndent()
                    )
                    out.flush()
                    out.close()

                    val reader = BufferedReader(InputStreamReader(connection.inputStream))

                    val read = StringBuilder()
                    var temp: String? = ""
                    while (reader.readLine().also { temp = it } != null) {
                        read.append(temp)
                    }
                    val jsonResponse: JsonObject = JsonParser.parseString(read.toString()).asJsonObject
                    val choices: JsonArray = jsonResponse.getAsJsonArray("choices")
                    val firstChoice: JsonObject = choices.get(0).asJsonObject
                    val message: JsonObject = firstChoice.getAsJsonObject("message")
                    var content: String = message.get("content").asString
                    if (content.get(content.length - 1) == '.') {
                        content = content.subSequence(0, content.length - 1).toString()
                    }
                    content
                } .onSuccess { content ->
                    withContext(Dispatchers.Main) {
                        job.cancel()
                        bindingTMD.tvResultText.text = content
                        bindingTMD.lyResult.isVisible = true
                        bindingTMD.tvResultClick.isVisible = true
                        bindingTMD.tvloading.isVisible = false

                        bindingTMD.lyResult.isEnabled = false
                        delay(1000) // 클릭 1초 막기
                        bindingTMD.lyResult.isEnabled = true
                    }
                } .onFailure { exception ->
                    withContext(Dispatchers.Main) {
                        job.cancel()

                        bindingTMD.tvloading.isVisible = false
                        bindingTMD.lyAiText.isVisible = true
                        Toast.makeText(mainActivity, "ChatGPT가 유효하지 않습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        bindingTMD.lyResult.setOnClickListener {
            bindingTMD.etTodo.setText(bindingTMD.tvResultText.text)
            bindingTMD.lyResult.visibility = View.GONE
            bindingTMD.tvResultClick.visibility = View.GONE
            bindingTMD.lyAiText.visibility = View.VISIBLE
        }

        if (state == TODO_UPDATE) {
            lifecycleScope.launch {
                viewModel.selectTodo(id).let {
                    todo = it
                }

                bindingTMD.etTodo.setText(todo.content)
                bindingTMD.switch2.isChecked = todo.isAlarm

                calendarPermissionCheck()
            }
        }

        val dialogBuilder = AlertDialog.Builder(requireContext())
        val dialog = dialogBuilder
            .setView(bindingTMD.root)
            .setCancelable(false) // 다이얼로그 바깥을 클릭해도 닫히지 않도록 설정
            .create()

        dialog.setOnShowListener {
            // Positive Button 커스텀 추가
            val positiveButton = bindingTMD.positiveButton
            positiveButton.setOnClickListener {
                if (bindingTMD.etTodo.text.isBlank()) {
                    Toast.makeText(requireContext(), "내용을 입력하세요. 개굴!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    todo.uId = getUId().toString()
                    todo.content = bindingTMD.etTodo.text.toString()
                    todo.isAlarm = bindingTMD.switch2.isChecked
                    viewModel.selectDay.value?.let {
                        todo.regTime = it
                    }

                    if (todo.alarmCode != -1) {
                        alarmManager.cancelAlarm(todo.alarmCode)
                        lifecycleScope.launch {
                            db.todoAlarmDao().delete(viewModel.selectTodo(id).alarmCode)
                        }
                    }

                    if(bindingTMD.switch2.isChecked) {
                        val hour = bindingTMD.calendarView.hour
                        val minute = bindingTMD.calendarView.minute
                        var strMinute = minute.toString()

                        if (bindingTMD.calendarView.minute < 10)
                            strMinute = "0$strMinute"
                        if (hour >= 12)
                            todo.alarmTime = "⏰ PM " + (hour - 12).toString() + ":" + strMinute
                        else
                            todo.alarmTime = "⏰ AM " + bindingTMD.calendarView.hour + ":" + strMinute

                        //다음날 or 오늘 지금 시간 이후
                        if((viewModel.selectDay.value!! >= getTodayInMillis()) or ((getTodayInMillis()- viewModel.selectDay.value!! <=86400000) and (getTimeInMillis(hour, minute) >= getTodayInMillis()))){
                            todo.alarmCode = registerAlarm()
                            lifecycleScope.launch {
                                val alarmdto = TodoAlarmDto()
                                alarmdto.alarm_code=todo.alarmCode
                                alarmdto.time = "${LongToLocaldate(viewModel.selectDay.value ?: Date().time)} $hour:$minute:00" // 알람이 울리는 시간
                                alarmdto.content = bindingTMD.etTodo.text.toString()
                                db.todoAlarmDao().insert(alarmdto)
                            }
                        }
                    } else {
                        todo.alarmCode = -1
                        todo.alarmTime = ""
                    }

                    if (state == TODO_INSERT) {
                        viewModel.addTodo(todo)
                    } else if (state == TODO_UPDATE) {
                        viewModel.updateTodoContent(todo)
                    }

                    dialog.dismiss()
                }
            }

            // Negative Button 커스텀 추가
            val negativeButton = bindingTMD.negativeButton
            negativeButton.setOnClickListener {
                dialog.dismiss()
            }
        }

        bindingTMD.switch2.setOnCheckedChangeListener { buttonView, isChecked ->
            calendarPermissionCheck()
        }

        if (bindingTMD.root.parent != null) {
            ((bindingTMD.root.parent) as ViewGroup).removeView(bindingTMD.root)
        }

        dialog.show()
    }

    private fun calendarPermissionCheck() {
        bindingTMD.calendarView.isVisible = false
        if(!checkPermission()) {
            bindingTMD.switch2.isChecked = false
            bindingTMD.calendarView.isVisible = false
        } else {
            if (bindingTMD.switch2.isChecked) {
                bindingTMD.calendarView.isVisible = true
            } else {
                bindingTMD.calendarView.isVisible = false
            }
        }
    }

    fun goLoginWithState(state : Int){
        val intent3 = Intent(requireContext(), LoginActivity::class.java)
        intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent3.putExtra("state", state)
        startActivity(intent3)
    }
    private fun registerAlarm(): Int {
        val hour = bindingTMD.calendarView.hour.toString()
        val minute = bindingTMD.calendarView.minute.toString()
        val time =
            "${LongToLocaldate(viewModel.selectDay.value ?: Date().time)} $hour:$minute:00" // 알람이 울리는 시간
        val random = (1 .. 100000) // 1~100000 범위에서 알람코드 랜덤으로 생성
        val alarmCode = random.random()
        setAlarm(alarmCode, bindingTMD.etTodo.text.toString(), time)

        return alarmCode
    }

    private fun setAlarm(alarmCode: Int, content: String, time: String) {
        alarmManager.callAlarm(time, alarmCode, content)
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

                val colorRes = if (day.date == selectedDate) {
                    R.color.LightGreen
                } else {
                    R.color.white
                }
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

    companion object {
        const val TODO_INSERT = 0
        const val TODO_UPDATE = 1
    }
}
package com.ssafy.frogdetox.ui.todo.todoDialog

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.ssafy.frogdetox.common.LocalAPIKey
import com.ssafy.frogdetox.common.LongToLocaldate
import com.ssafy.frogdetox.common.Permission
import com.ssafy.frogdetox.common.getTimeInMillis
import com.ssafy.frogdetox.common.getTodayInMillis
import com.ssafy.frogdetox.data.local.FrogDetoxDatabase
import com.ssafy.frogdetox.data.local.SharedPreferencesManager
import com.ssafy.frogdetox.data.model.TodoAlarmDto
import com.ssafy.frogdetox.data.model.TodoDto
import com.ssafy.frogdetox.databinding.DialogTodomakeBinding
import com.ssafy.frogdetox.ui.detox.DetoxBlockingBottomSheetFragment
import com.ssafy.frogdetox.ui.todo.AlarmManager
import com.ssafy.frogdetox.ui.todo.TodoFragment
import com.ssafy.frogdetox.ui.todo.TodoViewModel
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
import java.util.Date

class TodoRegisterDialog(
    private val fragment: TodoFragment,
    private val viewModel: TodoViewModel,
    private val db: FrogDetoxDatabase,
    private val alarmManager: AlarmManager
) {
    private var _binding: DialogTodomakeBinding? = null
    private val binding get() = _binding!!

    init {
        _binding = DialogTodomakeBinding.inflate(LayoutInflater.from(fragment.requireContext()))
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun alertDialog(state: Int, id: String) {
        var todo = TodoDto()

        binding.etTodo.setText("")
        binding.switch2.isChecked = false
        binding.calendarView.isVisible = false
        binding.lyAiText.isVisible = false
        binding.lyResult.isVisible = false
        binding.saylayout.isVisible = false
        //network작업 Runnable --> lambda
        binding.tvAiText.setOnClickListener {
            val apiKey = LocalAPIKey.getSecretKey(fragment.requireContext(), "chatgpt_api_key")
            GlobalScope.launch(Dispatchers.IO) {
                val job = CoroutineScope(Dispatchers.Main).launch {
                    binding.tvloading.text = "흠..."
                    binding.tvloading.isVisible = true
                    binding.lyAiText.isVisible = false

                    for (i in 0 .. 5) {
                        binding.tvloading.text = binding.tvloading.text.toString() + " 🤔"
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
                        binding.tvResultText.text = content
                        binding.lyResult.isVisible = true
                        binding.tvResultClick.isVisible = true
                        binding.tvloading.isVisible = false

                        binding.lyResult.isEnabled = false
                        delay(1000) // 클릭 1초 막기
                        binding.lyResult.isEnabled = true
                    }
                } .onFailure { exception ->
                    withContext(Dispatchers.Main) {
                        job.cancel()

                        binding.tvloading.isVisible = false
                        binding.lyAiText.isVisible = true
                        Toast.makeText(fragment.requireContext(), "ChatGPT가 유효하지 않습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.lyResult.setOnClickListener {
            binding.etTodo.setText(binding.tvResultText.text)
            binding.lyResult.visibility = View.GONE
            binding.tvResultClick.visibility = View.GONE
            binding.lyAiText.visibility = View.VISIBLE
        }

        if (state == TodoFragment.TODO_UPDATE) {
            fragment.lifecycleScope.launch {
                viewModel.selectTodo(id).let {
                    todo = it
                }

                binding.etTodo.setText(todo.content)
                binding.switch2.isChecked = todo.isAlarm
                if (todo.isAlarm) {
                    binding.calendarView.visibility = View.VISIBLE
                } else {
                    binding.calendarView.visibility = View.GONE
                }
            }
        }

        val dialogBuilder = AlertDialog.Builder(fragment.requireContext())
        val dialog = dialogBuilder
            .setView(binding.root)
            .setCancelable(false) // 다이얼로그 바깥을 클릭해도 닫히지 않도록 설정
            .create()

        dialog.setOnShowListener {
            // Positive Button 커스텀 추가
            val positiveButton = binding.positiveButton
            positiveButton.setOnClickListener {
                if (binding.etTodo.text.isBlank()) {
                    Toast.makeText(fragment.requireContext(), "내용을 입력하세요. 개굴!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    todo.uId = SharedPreferencesManager.getUId().toString()
                    todo.content = binding.etTodo.text.toString()
                    todo.isAlarm = binding.switch2.isChecked
                    viewModel.selectDay.value?.let {
                        todo.regTime = it
                    }

                    if (todo.alarmCode != -1) {
                        alarmManager.cancelAlarm(todo.alarmCode)
                        fragment.lifecycleScope.launch {
                            db.todoAlarmDao().delete(viewModel.selectTodo(id).alarmCode)
                        }
                    }

                    if(binding.switch2.isChecked) {
                        val hour = binding.calendarView.hour
                        val minute = binding.calendarView.minute
                        var strMinute = minute.toString()

                        if (binding.calendarView.minute < 10)
                            strMinute = "0$strMinute"
                        if (hour >= 12)
                            todo.alarmTime = "⏰ PM " + (hour - 12).toString() + ":" + strMinute
                        else
                            todo.alarmTime = "⏰ AM " + binding.calendarView.hour + ":" + strMinute

                        //다음날 or 오늘 지금 시간 이후
                        if((viewModel.selectDay.value!! >= getTodayInMillis()) or ((getTodayInMillis() - viewModel.selectDay.value!! <=86400000) and (getTimeInMillis(hour, minute) >= getTodayInMillis()))){
                            todo.alarmCode = registerAlarm()
                            fragment.lifecycleScope.launch {
                                val alarmdto = TodoAlarmDto()
                                alarmdto.alarm_code=todo.alarmCode
                                alarmdto.time = "${LongToLocaldate(viewModel.selectDay.value ?: Date().time)} $hour:$minute:00" // 알람이 울리는 시간
                                alarmdto.content = binding.etTodo.text.toString()
                                db.todoAlarmDao().insert(alarmdto)
                            }
                        }
                    } else {
                        todo.alarmCode = -1
                        todo.alarmTime = ""
                    }

                    if (state == TodoFragment.TODO_INSERT) {
                        viewModel.addTodo(todo)
                    } else if (state == TodoFragment.TODO_UPDATE) {
                        viewModel.updateTodoContent(todo)
                    }

                    dialog.dismiss()
                }
            }

            // Negative Button 커스텀 추가
            val negativeButton = binding.negativeButton
            negativeButton.setOnClickListener {
                dialog.dismiss()
            }
        }

        binding.switch2.setOnCheckedChangeListener { buttonView, isChecked ->
            if(!checkPermission()) {
                binding.switch2.isChecked = false
            } else {
                if (binding.switch2.isChecked) {
                    binding.calendarView.isVisible = true
                } else {
                    binding.calendarView.isVisible = false
                }
            }
        }

        if (binding.root.parent != null) {
            ((binding.root.parent) as ViewGroup).removeView(binding.root)
        }

        dialog.show()
    }

    private fun registerAlarm(): Int {
        val hour = binding.calendarView.hour.toString()
        val minute = binding.calendarView.minute.toString()
        val time =
            "${LongToLocaldate(viewModel.selectDay.value ?: Date().time)} $hour:$minute:00" // 알람이 울리는 시간
        val random = (1 .. 100000) // 1~100000 범위에서 알람코드 랜덤으로 생성
        val alarmCode = random.random()
        setAlarm(alarmCode, binding.etTodo.text.toString(), time)

        return alarmCode
    }

    private fun setAlarm(alarmCode: Int, content: String, time: String) {
        alarmManager.callAlarm(time, alarmCode, content)
    }

    private fun checkPermission(): Boolean {
        var notiPermission = NotificationManagerCompat.from(fragment.requireContext()).areNotificationsEnabled()

        var reminderPermission = Permission.isExactAlarmPermissionGranted(fragment.requireContext())

        if (!notiPermission || !reminderPermission) {
            Toast.makeText(
                fragment.requireContext(),
                "알림 받기 기능을 사용하시려면 아래 권한을 허용하셔야합니다.",
                Toast.LENGTH_SHORT
            ).show()
            val bottomSheet = DetoxBlockingBottomSheetFragment.newInstance(
                DetoxBlockingBottomSheetFragment.TODO_PERMISSION)
            bottomSheet.show(fragment.childFragmentManager, bottomSheet.tag)
        }
        notiPermission = NotificationManagerCompat.from(fragment.requireContext()).areNotificationsEnabled()

        reminderPermission = Permission.isExactAlarmPermissionGranted(fragment.requireContext())
        return notiPermission && reminderPermission
    }
}
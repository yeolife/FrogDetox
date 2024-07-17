package com.ssafy.frogdetox.ui.setting

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import com.ssafy.frogdetox.data.local.SharedPreferencesManager
import com.ssafy.frogdetox.databinding.DialogCountBinding
import com.ssafy.frogdetox.databinding.FragmentSettingBinding
import com.ssafy.frogdetox.ui.LoginActivity
import com.ssafy.frogdetox.ui.MainActivity
import com.ssafy.frogdetox.ui.todo.todoDialog.PersonalDialogFragment

class SettingFragment : Fragment() {
    private lateinit var mainActivity: MainActivity

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var listView: ListView
    private val menuItems = arrayOf("🐸 개구리 개수 설정하기", "💻 개인정보처리방침", "🧐 개발자한테 문의하기","🚀 로그아웃","😭 탈퇴하기")

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        mainActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mainActivity.changeFragmentView(MainActivity.TODO_FRAGMENT)
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = binding.lvUserInteraction
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, menuItems)
        listView.adapter = adapter

        binding.backBtn.setOnClickListener{
            mainActivity.changeFragmentView(MainActivity.TODO_FRAGMENT)
        }

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> setFrogCount()
                1 -> openPrivacyPolicy()
                2 -> contactDeveloper()
                3 -> goLoginWithState()
                4 -> bye()
            }
        }
    }

    private fun bye(){
        val dialog = PersonalDialogFragment()
        dialog.show(childFragmentManager, "PersonalDialogFragment")
    }

    @SuppressLint("SetTextI18n")
    private fun setFrogCount() {
        val binding2 = DialogCountBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = AlertDialog.Builder(requireContext())
            .setPositiveButton("확인") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }

        dialog.setView(binding2.root)
        dialog.show()

        // SharedPreferences에서 초기 count 값을 불러옴
        val initialCount = SharedPreferencesManager.getCount()
        binding2.seekBar.max = 50
        binding2.seekBar.min = 10
        binding2.seekBar.progress = initialCount
        binding2.tvCount.text = "$initialCount 개"

        // SeekBar의 값이 변경될 때마다 TextView를 업데이트하고 SharedPreferences에 저장
        binding2.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding2.tvCount.text = "$progress 개"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // 필요한 경우 여기에 코드 추가
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // SeekBar의 값이 변경되면 SharedPreferences에 저장
                SharedPreferencesManager.putCount(binding2.seekBar.progress)
            }
        })
    }


    private fun goLoginWithState(){
        val intent3 = Intent(requireContext(), LoginActivity::class.java)
        intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent3.putExtra("state", 1)
        startActivity(intent3)
    }

    private fun openPrivacyPolicy() {
        // 개인정보처리방침 페이지 URL
        val url = "https://www.notion.so/cab8db22c85c4b0eb62de92769976b7a"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun contactDeveloper() {
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "hyejjang1203@gmail.com", null))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "문의하기")
        startActivity(Intent.createChooser(emailIntent, "이메일 클라이언트를 선택하세요."))
    }
}
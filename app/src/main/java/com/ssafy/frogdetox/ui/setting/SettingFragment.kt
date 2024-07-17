package com.ssafy.frogdetox.ui.setting

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
import com.ssafy.frogdetox.databinding.FragmentSettingBinding
import com.ssafy.frogdetox.ui.LoginActivity
import com.ssafy.frogdetox.ui.MainActivity

class SettingFragment : Fragment() {
    private lateinit var mainActivity: MainActivity

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var listView: ListView
    private val menuItems = arrayOf("로그아웃", "개인정보처리방침", "개발자한테 문의하기")

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
                0 -> goLoginWithState()
                1 -> openPrivacyPolicy()
                2 -> contactDeveloper()
            }
        }
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
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "hyejjang120300@gmail.com", null))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "문의하기")
        startActivity(Intent.createChooser(emailIntent, "이메일 클라이언트를 선택하세요."))
    }
}
package com.ssafy.frogdetox.ui.todo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import coil.load
import coil.transform.CircleCropTransformation
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.data.local.SharedPreferencesManager
import com.ssafy.frogdetox.databinding.FragmentPersonalDialogBinding
import com.ssafy.frogdetox.ui.LoginActivity

class PersonalDialogFragment : DialogFragment() {
    private var _binding: FragmentPersonalDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val url = SharedPreferencesManager.getUserProfile()
        val name = SharedPreferencesManager.getUserName()

        binding.ivUrl.load(url) {
            transformations(CircleCropTransformation())
            placeholder(R.drawable.ic_launcher_foreground)
        }
        binding.tvName.text = "${name}님"
        binding.lyRealBye.visibility = View.GONE
        binding.lyBye.setOnClickListener {
            binding.lyRealBye.visibility = View.VISIBLE
        }
        binding.btnYes.setOnClickListener {
            // 다이얼로그에서 액티비티로 결과를 전달하는 예제입니다. 구현 필요
            goLoginWithState(2)
            dismiss()
        }
        binding.btnNo.setOnClickListener {
            binding.lyRealBye.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun goLoginWithState(state : Int){
        val intent3 = Intent(requireContext(), LoginActivity::class.java)
        intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent3.putExtra("state", state)
        startActivity(intent3)
    }
}
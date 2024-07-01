package com.ssafy.frogdetox.ui.todo.todoDialog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.data.local.SharedPreferencesManager
import com.ssafy.frogdetox.databinding.FragmentPersonalDialogBinding
import com.ssafy.frogdetox.ui.LoginActivity
import com.ssafy.frogdetox.ui.todo.TodoViewModel

private const val TAG = "PersonalDialogFragment"
class PersonalDialogFragment : DialogFragment() {
    private var _binding: FragmentPersonalDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: TodoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[TodoViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

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
            val user = auth.currentUser
            Log.d(TAG, "onViewCreated: ${requireContext()}")
            user?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 사용자 삭제 성공
                    Log.d(TAG, "onViewCreated: 성공")
                    // viewModel.deleteAllTodo()
                    Toast.makeText(requireContext(), "회원 탈퇴 되었습니다.", Toast.LENGTH_SHORT).show()
                    goLoginWithState()
                } else {                    
                    Log.d(TAG, "onViewCreated: 실패")
                    Toast.makeText(requireContext(), "회원 탈퇴 실패 ${task.exception}", Toast.LENGTH_SHORT).show()
                }
            }

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

    private fun goLoginWithState(){
        val intent3 = Intent(requireContext(), LoginActivity::class.java)
        intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent3.putExtra("state", 1)
        startActivity(intent3)
    }
}
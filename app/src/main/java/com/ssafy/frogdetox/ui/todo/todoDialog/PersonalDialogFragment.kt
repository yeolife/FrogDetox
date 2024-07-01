package com.ssafy.frogdetox.ui.todo.todoDialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.data.local.SharedPreferencesManager
import com.ssafy.frogdetox.databinding.FragmentPersonalDialogBinding
import com.ssafy.frogdetox.ui.LoginActivity
import com.ssafy.frogdetox.ui.MainActivity
import com.ssafy.frogdetox.ui.todo.TodoViewModel

private const val TAG = "PersonalDialogFragment"
class PersonalDialogFragment : DialogFragment() {
    private lateinit var mainActivity: MainActivity

    private var _binding: FragmentPersonalDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private val viewModel: TodoViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
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
            val result = runCatching {
                user?.delete()
            }

            result.onSuccess {
                viewModel.deleteAllTodo()
                Toast.makeText(mainActivity, "회원 탈퇴 되었습니다.", Toast.LENGTH_SHORT).show()
                goLoginWithState()
            } .onFailure {
                Toast.makeText(mainActivity, "회원 탈퇴 실패", Toast.LENGTH_SHORT).show()
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
        val intent3 = Intent(mainActivity, LoginActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            .putExtra("state", 1)
        startActivity(intent3)
    }
}
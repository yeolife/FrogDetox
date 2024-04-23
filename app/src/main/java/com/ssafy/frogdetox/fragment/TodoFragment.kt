package com.ssafy.frogdetox.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.frogdetox.MainActivity
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.databinding.FragmentTodoBinding

class TodoFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!


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
    ): View? {
        _binding = FragmentTodoBinding.inflate(inflater, container, false)

        return binding.root
    }

}
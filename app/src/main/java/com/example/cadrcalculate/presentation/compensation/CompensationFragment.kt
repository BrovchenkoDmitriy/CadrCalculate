package com.example.cadrcalculate.presentation.compensation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cadrcalculate.databinding.FragmentCompensationBinding

class CompensationFragment : Fragment() {

    private var _binding: FragmentCompensationBinding? = null
    private val binding: FragmentCompensationBinding
        get() = _binding ?: throw RuntimeException("FragmentCompensationBinding is null")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCompensationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
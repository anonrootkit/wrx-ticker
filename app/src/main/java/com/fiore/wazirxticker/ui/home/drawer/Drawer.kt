package com.fiore.wazirxticker.ui.home.drawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fiore.wazirxticker.R
import com.fiore.wazirxticker.databinding.FragmentDrawerBinding
import com.fiore.wazirxticker.utils.safeNavigate
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class Drawer : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentDrawerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDrawerBinding.inflate(inflater)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.investmentHistory.setOnClickListener {
            safeNavigate(DrawerDirections.actionDrawerToHistory())
        }

        binding.settings.setOnClickListener {
            safeNavigate(DrawerDirections.actionDrawerToSettings())
        }

    }
}
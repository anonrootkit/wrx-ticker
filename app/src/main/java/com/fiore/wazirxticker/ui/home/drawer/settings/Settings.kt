package com.fiore.wazirxticker.ui.home.drawer.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.fiore.wazirxticker.databinding.FragmentSettingsBinding
import com.fiore.wazirxticker.ui.viewmodels.UtilsViewModel
import com.fiore.wazirxticker.utils.ThemeConstants
import com.fiore.wazirxticker.utils.changeAppTheme
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Settings : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val utilsViewModel: UtilsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        binding.darkModeToggleButton.setOnCheckedChangeListener { _, isChecked ->
            val newTheme = if (isChecked) ThemeConstants.DARK else ThemeConstants.LIGHT
            utilsViewModel.setCurrentAppTheme(newTheme)
            changeAppTheme(newTheme)
        }
    }

    private fun initView() {
        val currentTheme = utilsViewModel.getCurrentTheme()
        binding.darkModeToggleButton.isChecked = currentTheme == ThemeConstants.DARK
    }
}
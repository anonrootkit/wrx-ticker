package com.fiore.wazirxticker.ui.home.investments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.SimpleItemAnimator
import com.fiore.wazirxticker.R
import com.fiore.wazirxticker.databinding.FragmentInvestmentsBinding
import com.fiore.wazirxticker.ui.home.investments.adapter.InvestmentsAdapter
import com.fiore.wazirxticker.ui.viewmodels.PricesViewModel
import com.fiore.wazirxticker.ui.viewmodels.UtilsViewModel
import com.fiore.wazirxticker.utils.ThemeConstants
import com.fiore.wazirxticker.utils.applyThemeSettings
import com.fiore.wazirxticker.utils.changeAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Investments : Fragment(R.layout.fragment_investments) {
    private lateinit var binding : FragmentInvestmentsBinding
    private val pricesViewModel : PricesViewModel by viewModels()
    private val utilsViewModel : UtilsViewModel by activityViewModels()

    private val investmentsAdapter by lazy {
        InvestmentsAdapter(
            inflater = layoutInflater
        )
    }

    private val combinedInvestmentsAdapter by lazy {
        InvestmentsAdapter(
            inflater = layoutInflater
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentInvestmentsBinding.bind(view)
        binding.lifecycleOwner = this

        initToolbar()

        binding.investmentsList.apply {
            adapter = investmentsAdapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        binding.combinedInvestmentsList.apply {
            adapter = combinedInvestmentsAdapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        pricesViewModel.coins.observe(viewLifecycleOwner) {
            pricesViewModel.startUpdatingCoins()
        }

        pricesViewModel.investments.observe(viewLifecycleOwner) {
            investmentsAdapter.submitList(it)
            pricesViewModel.startUpdatingInvestments()
            binding.eachInvestmentLabel.visibility = if (it.isNullOrEmpty()) View.GONE else View.VISIBLE
            binding.emptyInvestmentText.visibility =
                if (it.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        pricesViewModel.combinedInvestments.observe(viewLifecycleOwner) {
            combinedInvestmentsAdapter.submitList(it)
            binding.combinedInvestmentLabel.visibility = if (it.isNullOrEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun initToolbar() {
        binding.toolbar.toggleTheme
            .applyThemeSettings(theme = utilsViewModel.getCurrentTheme()) { currentTheme ->
                when (currentTheme) {
                    ThemeConstants.LIGHT -> {
                        changeAppTheme(ThemeConstants.DARK)
                        utilsViewModel.setCurrentAppTheme(ThemeConstants.DARK)
                    }
                    ThemeConstants.DARK -> {
                        changeAppTheme(ThemeConstants.LIGHT)
                        utilsViewModel.setCurrentAppTheme(ThemeConstants.LIGHT)
                    }
                }

                initToolbar()
            }
    }
}
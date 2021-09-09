package com.fiore.wazirxticker.ui.home.coins

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.SimpleItemAnimator
import com.fiore.wazirxticker.R
import com.fiore.wazirxticker.databinding.FragmentCoinsBinding
import com.fiore.wazirxticker.ui.home.coins.adapter.CoinsAdapter
import com.fiore.wazirxticker.ui.home.investments.adapter.InvestmentsAdapter
import com.fiore.wazirxticker.ui.viewmodels.PricesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Coins : Fragment(R.layout.fragment_coins) {

    private lateinit var binding: FragmentCoinsBinding
    private val pricesViewModel: PricesViewModel by viewModels()

    private val coinsAdapter by lazy {
        CoinsAdapter(
            inflater = layoutInflater
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCoinsBinding.bind(view)
        binding.lifecycleOwner = this

        binding.refresh.isEnabled = false
        binding.coinsList.apply {
            adapter = coinsAdapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        pricesViewModel.coins.observe(viewLifecycleOwner) {
            coinsAdapter.submitList(it?.map { it.copy(name = it.name.uppercase()) })
            pricesViewModel.startUpdatingCoins()
            binding.emptyCoinsText.visibility = if (it.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
    }
}
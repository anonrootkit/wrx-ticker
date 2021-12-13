package com.fiore.wazirxticker.ui.home.coins

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.SimpleItemAnimator
import com.fiore.wazirxticker.R
import com.fiore.wazirxticker.databinding.FragmentCoinsBinding
import com.fiore.wazirxticker.ui.home.HomeActivity
import com.fiore.wazirxticker.ui.home.coins.adapter.CoinSwipeListener
import com.fiore.wazirxticker.ui.home.coins.adapter.CoinsAdapter
import com.fiore.wazirxticker.ui.viewmodels.PricesViewModel
import com.fiore.wazirxticker.ui.viewmodels.UtilsViewModel
import com.fiore.wazirxticker.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Coins : Fragment(R.layout.fragment_coins) {

    private lateinit var binding: FragmentCoinsBinding
    private val pricesViewModel: PricesViewModel by viewModels()
    private val utilsViewModel: UtilsViewModel by activityViewModels()

    private val coinsAdapter by lazy {
        CoinsAdapter(inflater = layoutInflater)
    }

    private fun showSnackBar() {
        (activity as? HomeActivity)?.showSnackbar(
            snackbarMsg = getString(R.string.undo_coin_delete),
            snackbarAction = SnackbarAction(
                actionTitle = R.string.undo,
                actionToPerform = {
                    pricesViewModel.undoCoinVisibilityStatus()
                }
            )
        )
    }

    private val coinSwipeListener by lazy {
        CoinSwipeListener(
            context = requireContext(),
            coinsAdapter = coinsAdapter,
            deleteCoin = { coinName ->
                pricesViewModel.hideCoinInDB(coinName.lowercase())
                showSnackBar()
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCoinsBinding.bind(view)
        binding.lifecycleOwner = this

        initToolbar()

        binding.refresh.isEnabled = false

        binding.coinsList.apply {
            adapter = coinsAdapter
            ItemTouchHelper(coinSwipeListener).attachToRecyclerView(this)
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        pricesViewModel.coins.observe(viewLifecycleOwner) {
            coinsAdapter.submitList(it?.map { it.copy(name = it.name.uppercase()) })
            pricesViewModel.startUpdatingCoins()
            binding.emptyCoinsText.visibility = if (it.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun initToolbar() {
        binding.toolbar.settings.setOnClickListener {
            safeNavigate(CoinsDirections.actionCoinsToSettings())
        }

        binding.toolbar.coffee.setOnClickListener {
            safeNavigate(CoinsDirections.actionCoinsToCoffee())
        }
    }
}
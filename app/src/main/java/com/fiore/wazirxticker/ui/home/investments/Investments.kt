package com.fiore.wazirxticker.ui.home.investments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.SimpleItemAnimator
import com.fiore.wazirxticker.R
import com.fiore.wazirxticker.databinding.FragmentInvestmentsBinding
import com.fiore.wazirxticker.ui.home.HomeActivity
import com.fiore.wazirxticker.ui.home.investments.adapter.InvestmentSwipeListener
import com.fiore.wazirxticker.ui.home.investments.adapter.InvestmentsAdapter
import com.fiore.wazirxticker.ui.viewmodels.HistoryViewModel
import com.fiore.wazirxticker.ui.viewmodels.PricesViewModel
import com.fiore.wazirxticker.utils.SnackbarAction
import com.fiore.wazirxticker.utils.safeNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Investments : Fragment(R.layout.fragment_investments) {
    private lateinit var binding: FragmentInvestmentsBinding
    private val pricesViewModel: PricesViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()

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

    private val investmentSwipeListener by lazy {
        InvestmentSwipeListener(
            context = requireContext(),
            investmentsAdapter = investmentsAdapter,
            deleteInvestment = { investment ->
                pricesViewModel.deleteInvestmentFromDB(investment)
                showSnackBar(investmentDeleted = true)
            },
            moveInvestmentToHistory = { investment ->
                historyViewModel.insertInvestmentToHistory(investment)
                pricesViewModel.deleteInvestmentFromDB(investment)
                showSnackBar()
            }
        )
    }

    private fun showSnackBar(investmentDeleted: Boolean = false) {
        if (investmentDeleted)
            (activity as? HomeActivity)?.showSnackbar(
                snackbarMsg = getString(R.string.undo_investment_delete),
                snackbarAction = SnackbarAction(
                    actionTitle = R.string.undo,
                    actionToPerform = {
                        pricesViewModel.undoDeleteInvestment()
                    }
                ),
                long = true
            )
        else
            (activity as? HomeActivity)?.showSnackbar(
                snackbarMsg = getString(R.string.investment_added_to_history),
                snackbarAction = SnackbarAction(
                    actionTitle =  R.string.undo,
                    actionToPerform = {
                        pricesViewModel.undoDeleteInvestment()
                        historyViewModel.undoHistoryInsertion()
                    }
                ),
                long = true
            )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentInvestmentsBinding.bind(view)
        binding.lifecycleOwner = this

        initToolbar()

        binding.investmentsList.apply {
            adapter = investmentsAdapter
            ItemTouchHelper(investmentSwipeListener).attachToRecyclerView(this)
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
            binding.eachInvestmentLabel.visibility =
                if (it.isNullOrEmpty()) View.GONE else View.VISIBLE
            binding.emptyInvestmentText.visibility =
                if (it.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        pricesViewModel.combinedInvestments.observe(viewLifecycleOwner) {
            combinedInvestmentsAdapter.submitList(it)
            binding.combinedInvestmentLabel.visibility =
                if (it.isNullOrEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun initToolbar() {
        binding.toolbar.drawer.setOnClickListener {
            safeNavigate(InvestmentsDirections.actionInvestmentsToDrawer())
        }

        binding.toolbar.coffee.setOnClickListener {
            safeNavigate(InvestmentsDirections.actionInvestmentsToCoffee())
        }
    }
}
package com.fiore.wazirxticker.ui.home.drawer.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.SimpleItemAnimator
import com.fiore.wazirxticker.R
import com.fiore.wazirxticker.databinding.FragmentHistoryBinding
import com.fiore.wazirxticker.ui.home.HomeActivity
import com.fiore.wazirxticker.ui.home.drawer.history.adapter.HistoryAdapter
import com.fiore.wazirxticker.ui.home.drawer.history.adapter.HistorySwipeListener
import com.fiore.wazirxticker.ui.viewmodels.HistoryViewModel
import com.fiore.wazirxticker.utils.SnackbarAction
import com.fiore.wazirxticker.utils.navigateUp
import com.fiore.wazirxticker.utils.safeNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class History : Fragment(R.layout.fragment_history) {
    private lateinit var binding: FragmentHistoryBinding
    private val historyViewModel: HistoryViewModel by viewModels()

    private val historyAdapter: HistoryAdapter by lazy {
        HistoryAdapter(
            inflater = layoutInflater,
            onEditClick = { history ->
                safeNavigate(HistoryDirections.actionHistoryToInvestment(history = history))
        })
    }

    private val historySwiperListener by lazy {
        HistorySwipeListener(
            context = requireContext(),
            historyAdapter = historyAdapter,
            deleteHistory = { history ->
                historyViewModel.deleteHistory(history = history)
                showSnackBar()
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHistoryBinding.bind(view)
        binding.lifecycleOwner = this

        initToolbar()

        binding.refresh.isEnabled = false

        binding.hisotryList.apply {
            adapter = historyAdapter
            ItemTouchHelper(historySwiperListener).attachToRecyclerView(this)
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        historyViewModel.history.observe(viewLifecycleOwner) {
            historyAdapter.submitList(it)

            binding.emptyHistoryText.visibility =
                if (it.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

    }

    private fun initToolbar() {
        binding.toolbar.coffee.setOnClickListener {
            navigateUp()
        }
    }

    private fun showSnackBar() {
        (activity as? HomeActivity)?.showSnackbar(
            context = requireContext(),
            snackbarMsg = getString(R.string.history_deleted),
            snackbarAction = SnackbarAction(
                actionTitle = R.string.undo,
                actionToPerform = {
                    historyViewModel.undoHistoryDeletion()
                }
            ),
            long = true,
            anchorView = null
        )
    }
}
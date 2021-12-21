package com.fiore.wazirxticker.ui.home.add.investment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.fiore.wazirxticker.R
import com.fiore.wazirxticker.data.database.entities.History
import com.fiore.wazirxticker.databinding.FragmentNewHistoryBinding
import com.fiore.wazirxticker.domain.core.ResponseStatus
import com.fiore.wazirxticker.ui.viewmodels.HistoryViewModel
import com.fiore.wazirxticker.ui.viewmodels.UtilsViewModel
import com.fiore.wazirxticker.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class NewHistory : Fragment(R.layout.fragment_new_history) {
    private lateinit var binding: FragmentNewHistoryBinding
    private val historyViewModel: HistoryViewModel by viewModels()
    private val utilsViewModel: UtilsViewModel by activityViewModels()

    private var buyDate: Long = 0
    private var sellDate: Long = 0
    private var updateHistory: History? = null

    private val buyDatePicker: DatePickerDialog by lazy {
        val dateComponents: Triple<Int, Int, Int> = updateHistory?.buyDate.getDateComponents()

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth)

                if (calendar.timeInMillis > sellDate) {
                    showToast(getString(R.string.bought_date_less))
                    return@DatePickerDialog
                }

                buyDate = calendar.timeInMillis
                updateDateView()
            }, dateComponents.third, dateComponents.second, dateComponents.first
        )
    }

    private val sellDatePicker: DatePickerDialog by lazy {
        val dateComponents: Triple<Int, Int, Int> = updateHistory?.sellDate.getDateComponents()

        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)

            if (calendar.timeInMillis < buyDate) {
                showToast(getString(R.string.selling_date_less))
                return@DatePickerDialog
            }

            sellDate = calendar.timeInMillis
            updateDateView()
        }, dateComponents.third, dateComponents.second, dateComponents.first)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentNewHistoryBinding.bind(view)
        binding.lifecycleOwner = this

        updateHistory = arguments?.let { NewHistoryArgs.fromBundle(it).history }

        initViews()

        binding.buyDatePicker.setOnClickListener {
            buyDatePicker.show()
        }

        binding.sellDatePicker.setOnClickListener {
            sellDatePicker.show()
        }

        binding.addHistory.setOnClickListener {
            val coinName: String = binding.coinName.text.toString().uppercase()

            if (coinName.isNotBlank()) {
                val buyPrice: String = binding.buyPrice.text.toString()
                val isBuyPriceValid = buyPrice.isValid()

                if (isBuyPriceValid) {

                    val sellPrice: String = binding.sellPrice.text.toString()
                    val isSellPriceValid = sellPrice.isValid()

                    if (isSellPriceValid) {
                        val coinsAmount: String = binding.coinsCount.text.toString()
                        val isCountValid = coinsAmount.isValid()

                        if (isCountValid) {

                            historyViewModel.insertHistory(
                                historyId = updateHistory?.id ?: System.currentTimeMillis(),
                                name = coinName,
                                buyPrice = buyPrice.trim().bd(),
                                sellPrice = sellPrice.trim().bd(),
                                buyDate = buyDate,
                                sellDate = sellDate,
                                coinsCount = coinsAmount.trim().bd()
                            )

                        } else {
                            showToast(getString(R.string.enter_valid_coins_count))
                        }
                    } else {
                        showToast(getString(R.string.enter_valid_sell_price))
                    }
                } else {
                    showToast(getString(R.string.enter_valid_buy_price))
                }
            } else {
                showToast(getString(R.string.enter_valid_coin))
            }
        }

        historyViewModel.addingHistoryStatus.observe(viewLifecycleOwner) {
            when (it) {
                ResponseStatus.SUCCESS -> {
                    showToast(getString(R.string.added_in_investment_history))
                    utilsViewModel.updateDismissInvestmentBottomSheet(dismiss = true)
                }

                else -> {}
            }
        }
    }

    private fun initViews() {
        initDateViews()

        updateHistory?.let {
            binding.coinName.setText(it.name)
            binding.buyPrice.setText(it.buyPrice)
            binding.sellPrice.setText(it.sellPrice)
            binding.coinsCount.setText(it.coinsCount)
            binding.addHistory.text = getString(R.string.update_history)
        }
    }

    private fun initDateViews() {
        buyDate = updateHistory?.buyDate ?: System.currentTimeMillis()
        sellDate = updateHistory?.sellDate ?: System.currentTimeMillis()
        updateDateView()
    }

    private fun updateDateView() {
        val dateFormat = "dd/MM/yyyy"
        val buyDateString = buyDate.getDate(format = dateFormat)
        val sellDateString = sellDate.getDate(format = dateFormat)

        binding.buyDatePicker.text = buyDateString
        binding.sellDatePicker.text = sellDateString
    }
}
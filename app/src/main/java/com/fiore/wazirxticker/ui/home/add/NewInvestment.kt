package com.fiore.wazirxticker.ui.home.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.fiore.wazirxticker.R
import com.fiore.wazirxticker.databinding.FragmentNewInvestmentBinding
import com.fiore.wazirxticker.domain.core.ResponseStatus
import com.fiore.wazirxticker.ui.viewmodels.PricesViewModel
import com.fiore.wazirxticker.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewInvestment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewInvestmentBinding
    private val pricesViewModel: PricesViewModel by viewModels()
    private lateinit var loader: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewInvestmentBinding.inflate(inflater)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loader = loader(getString(R.string.adding_investment))

        binding.addInvestment.setOnClickListener {
            val coinName: String = binding.coinName.text.toString().lowercase()

            if (coinName.isNotBlank()) {
                val buyPrice = binding.buyPrice.text.toString()
                val isPriceValid = buyPrice.isValid()

                if (isPriceValid) {
                    val buyAmount = binding.buyAmount.text.toString()
                    val isAmountValid = buyAmount.isValid()

                    if (isAmountValid) {

                        pricesViewModel.insertInvestment(
                            name = coinName,
                            buyPrice = buyPrice.trim().bd(),
                            buyAmount = buyAmount.trim().bd().toBigInteger()
                        )
                    } else {
                        showToast(getString(R.string.enter_valid_amount))
                    }
                } else {
                    showToast(getString(R.string.enter_valid_buy_price))
                }
            } else {
                showToast(getString(R.string.enter_valid_coin))
            }
        }

        pricesViewModel.addingInvestmentStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    ResponseStatus.LOADING -> {
                        loader.show()
                    }
                    ResponseStatus.SUCCESS -> {
                        loader.dismiss()
                        dismiss()
                    }

                    ResponseStatus.NETWORK_CONNECTION_ERROR -> {
                        loader.dismiss()
                        showToast(getString(R.string.no_internet_connection))
                        dismiss()
                    }

                    else -> {
                        loader.dismiss()
                        showToast("No coin found")
                        dismiss()
                    }
                }

                pricesViewModel.resetInvestmentStatus()
            }
        }
    }
}
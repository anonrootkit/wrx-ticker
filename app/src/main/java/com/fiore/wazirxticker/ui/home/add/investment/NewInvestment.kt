package com.fiore.wazirxticker.ui.home.add.investment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.fiore.wazirxticker.R
import com.fiore.wazirxticker.databinding.FragmentNewInvestmentBinding
import com.fiore.wazirxticker.domain.core.ResponseStatus
import com.fiore.wazirxticker.ui.viewmodels.PricesViewModel
import com.fiore.wazirxticker.ui.viewmodels.UtilsViewModel
import com.fiore.wazirxticker.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class NewInvestment : Fragment(R.layout.fragment_new_investment), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: FragmentNewInvestmentBinding
    private val pricesViewModel: PricesViewModel by viewModels()
    private val utilsViewModel : UtilsViewModel by activityViewModels()
    private lateinit var loader: AlertDialog

    private val datePicker : DatePickerDialog by lazy {
        DatePickerDialog(requireContext()).apply { setOnDateSetListener(this@NewInvestment) }
    }

    private var buyDate : Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentNewInvestmentBinding.bind(view)
        binding.lifecycleOwner = this

        loader = loader(getString(R.string.adding_investment))

        initBuyDate()

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
                            buyAmount = buyAmount.trim().bd().toBigInteger(),
                            date = buyDate
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

        binding.datePicker.setOnClickListener {
            datePicker.show()
        }

        pricesViewModel.addingInvestmentStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    ResponseStatus.LOADING -> {
                        loader.show()
                    }
                    ResponseStatus.SUCCESS -> {
                        loader.dismiss()
                        utilsViewModel.updateDismissInvestmentBottomSheet(dismiss = true)
                    }

                    ResponseStatus.NETWORK_CONNECTION_ERROR -> {
                        loader.dismiss()
                        showToast(getString(R.string.no_internet_connection))
                        utilsViewModel.updateDismissInvestmentBottomSheet(dismiss = true)
                    }

                    else -> {
                        loader.dismiss()
                        showToast("No coin found")
                        utilsViewModel.updateDismissInvestmentBottomSheet(dismiss = true)
                    }
                }

                pricesViewModel.resetInvestmentStatus()
            }
        }
    }

    private fun initBuyDate() {
        buyDate = System.currentTimeMillis()
        updateDateView()
    }

    private fun updateDateView() {
        val dateString = buyDate.getDate(format = "dd/MM/yyyy")
        binding.datePicker.text = dateString
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        buyDate = calendar.timeInMillis
        updateDateView()
    }
}
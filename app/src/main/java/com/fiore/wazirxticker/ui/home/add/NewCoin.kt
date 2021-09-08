package com.fiore.wazirxticker.ui.home.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.fiore.wazirxticker.R
import com.fiore.wazirxticker.databinding.FragmentNewCoinBinding
import com.fiore.wazirxticker.domain.core.ResponseStatus
import com.fiore.wazirxticker.ui.viewmodels.PricesViewModel
import com.fiore.wazirxticker.utils.loader
import com.fiore.wazirxticker.utils.showToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewCoin : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentNewCoinBinding
    private val pricesViewModel : PricesViewModel by viewModels()
    private lateinit var loader : AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewCoinBinding.inflate(inflater)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loader = loader(getString(R.string.fetching_coin_details))

        pricesViewModel.coinDetailsFetchStatus.observe(viewLifecycleOwner) {
            if (it != null){
                when(it) {
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

                pricesViewModel.resetCoinDetailsFetchStatus()
            }
        }

        binding.addCoin.setOnClickListener {
            val coinName : String = binding.coinName.text.toString()
            if (coinName.isNotBlank()){
                pricesViewModel.fetchCoinDetails(name = coinName.trim().lowercase())
            }else{
                showToast(getString(R.string.enter_valid_coin))
            }
        }

    }
}
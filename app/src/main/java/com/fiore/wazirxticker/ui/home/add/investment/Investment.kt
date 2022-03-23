package com.fiore.wazirxticker.ui.home.add.investment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.fiore.wazirxticker.data.database.entities.History
import com.fiore.wazirxticker.databinding.FragmentInvestmentBinding
import com.fiore.wazirxticker.ui.viewmodels.UtilsViewModel
import com.fiore.wazirxticker.utils.safeNavigate
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class Investment : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentInvestmentBinding
    private val utilsViewModel : UtilsViewModel by activityViewModels()

    private val navController by lazy {
        Navigation.findNavController(binding.investmentScreens)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInvestmentBinding.inflate(inflater)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val history : History? = arguments?.let { InvestmentArgs.fromBundle(it).history }

        binding.buyOrder.isSelected = true

        binding.buyOrder.apply {
            setOnClickListener {
                this.isSelected = true
                binding.sellOrder.isSelected = false

                navController.safeNavigate(NewHistoryDirections.actionNewHistoryToNewInvestment())
            }
        }

        binding.sellOrder.apply {
            setOnClickListener {
                this.isSelected = true
                binding.buyOrder.isSelected = false

                navController.safeNavigate(NewInvestmentDirections.actionNewInvestmentToNewHistory(history = history))
            }
        }

        history?.let { binding.sellOrder.post { binding.sellOrder.performClick() } }

        utilsViewModel.dismissInvestmentsBottomSheet.observe(viewLifecycleOwner) {
            if (it == true) {
                dismiss()
                utilsViewModel.updateDismissInvestmentBottomSheet(dismiss = false)
            }
        }

    }

}
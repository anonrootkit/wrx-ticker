package com.fiore.wazirxticker.ui.home.coffee

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.fiore.wazirxticker.BuildConfig
import com.fiore.wazirxticker.R
import com.fiore.wazirxticker.databinding.FragmentCoffeeBinding
import com.fiore.wazirxticker.utils.openUrl

class Coffee : DialogFragment(R.layout.fragment_coffee) {

    private lateinit var binding: FragmentCoffeeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCoffeeBinding.bind(view)
        binding.lifecycleOwner = this

        binding.buyCoffee.setOnClickListener { openUrl(BuildConfig.PATREON_URL) }
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = requireDialog().window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        requireDialog().window!!.attributes = params as WindowManager.LayoutParams
    }
}
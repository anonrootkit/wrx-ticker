package com.fiore.wazirxticker.ui.home

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.iterator
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.chuckerteam.chucker.databinding.ChuckerActivityMainBinding
import com.fiore.wazirxticker.R
import com.fiore.wazirxticker.databinding.ActivityMainBinding
import com.fiore.wazirxticker.ui.home.coins.CoinsDirections
import com.fiore.wazirxticker.ui.viewmodels.PricesViewModel
import com.fiore.wazirxticker.utils.safeNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val rootNavController: NavController by lazy {
        Navigation.findNavController(this, R.id.home_screens_container)
    }

    private val screenChangeListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->

            val currentScreenId = destination.id
            toggleFab(currentScreenId)
        }

    private fun toggleFab(currentScreenId: Int) {
        binding.bottomNavigationContainer.addCoinFab.visibility = when (currentScreenId) {
            R.id.coins -> View.VISIBLE
            R.id.newCoin -> View.VISIBLE
            R.id.investments -> View.VISIBLE
            else -> View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this

        setContentView(binding.root)

        rootNavController.addOnDestinationChangedListener(screenChangeListener)

        binding.bottomNavigationContainer.addCoinFab.setOnClickListener {
            when(rootNavController.currentDestination?.id){
                R.id.coins ->  rootNavController.navigate(R.id.newCoin)
                R.id.investments -> rootNavController.navigate(R.id.newCoin)
                else -> View.GONE
            }
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        NavigationUI.setupWithNavController(
            binding.bottomNavigationContainer.bottomNavigation,
            rootNavController
        )
    }
}
@file:Suppress("SpellCheckingInspection")

package com.fiore.wazirxticker.ui.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.fiore.wazirxticker.R
import com.fiore.wazirxticker.databinding.ActivityMainBinding
import com.fiore.wazirxticker.utils.SnackbarAction
import com.google.android.material.snackbar.Snackbar
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
                R.id.investments -> rootNavController.navigate(R.id.newInvestment)
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

    fun showSnackbar(
        snackbarMsg: String,
        snackbarAction: SnackbarAction? = null,
    ) {
        try {
            val snackbar = Snackbar.make(this, binding.bottomNavigationContainer.root, snackbarMsg, Snackbar.LENGTH_SHORT)
            snackbarAction?.let { action ->
                snackbar.setAction(action.actionTitle) {
                    action.actionToPerform()
                }
            }
            snackbar.anchorView = binding.bottomNavigationContainer.root
            snackbar.show()
        } catch (e: Exception) {

        }
    }
}
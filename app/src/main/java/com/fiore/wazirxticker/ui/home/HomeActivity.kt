@file:Suppress("SpellCheckingInspection")

package com.fiore.wazirxticker.ui.home

import android.content.Context
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
            toggleBottomNav(currentScreenId)
        }

    private fun toggleBottomNav(currentScreenId: Int) {
        binding.bottomNavigationContainer.root.visibility = when (currentScreenId) {
            R.id.history -> View.GONE
            else -> View.VISIBLE
        }
    }

    private fun toggleFab(currentScreenId: Int) {
        binding.bottomNavigationContainer.addCoinFab.visibility = when (currentScreenId) {
            R.id.coins,
            R.id.newCoin,
            R.id.investments,
            R.id.drawer,
            R.id.coffee,
            R.id.settings,
            R.id.investment -> View.VISIBLE
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
            when (rootNavController.currentDestination?.id) {
                R.id.coins -> rootNavController.navigate(R.id.newCoin)
                R.id.investments -> rootNavController.navigate(R.id.investment)
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
        context: Context = this,
        snackbarMsg: String,
        snackbarAction: SnackbarAction? = null,
        long: Boolean = false,
        anchorView: View? = binding.bottomNavigationContainer.root
    ) {
        try {
            val snackbar = Snackbar.make(
                context,
                binding.bottomNavigationContainer.root,
                snackbarMsg,
                if (long) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT
            )
            snackbarAction?.let { action ->
                snackbar.setAction(action.actionTitle) {
                    action.actionToPerform()
                }
            }
            snackbar.anchorView = anchorView
            snackbar.show()
        } catch (e: Exception) {

        }
    }
}
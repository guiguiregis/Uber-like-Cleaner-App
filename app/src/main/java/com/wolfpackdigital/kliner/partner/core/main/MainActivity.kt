package com.wolfpackdigital.kliner.partner.core.main

import android.animation.LayoutTransition
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import com.wolfpackdigital.kliner.partner.ActMainBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.data.notifications.NotificationModel
import com.wolfpackdigital.kliner.partner.data.notifications.PushNotificationAction
import com.wolfpackdigital.kliner.partner.shared.base.BaseActivity
import com.wolfpackdigital.kliner.partner.shared.customs.StatefulHideOnScrollBehaviour
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.setupWithNavController
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActMainBinding, MainActivityViewModel>(R.layout.activity_main) {
    override val viewModel by viewModel<MainActivityViewModel>()

    private val behaviour
        get() = (binding.mainBottomNav.layoutParams as? CoordinatorLayout.LayoutParams)
            ?.behavior as? StatefulHideOnScrollBehaviour

    private val onDestinationChangedListener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->
            val showBottomNav = when (destination.id) {
                R.id.dashboardFragment,
                R.id.planningFragment,
                R.id.offersFragment,
                R.id.moreFragment -> true
                else -> false
            }
            when (destination.id) {
                R.id.missionDetailsFragment -> loadColoredStatusBar(R.color.colorAccent)
                else -> loadColoredStatusBar()
            }
            viewModel.toggleBottomNav(showBottomNav)
        }

    override fun setupViews() {
        (binding.clRoot as? ViewGroup)?.layoutTransition?.enableTransitionType(LayoutTransition.CHANGING)
        setupObservers()
    }

    override fun parseNotificationFromIntent(notification: NotificationModel?) {
        Log.d("FBNOTIF", "main notification parsed: $notification")
        when (notification?.action) {
            PushNotificationAction.NEW_MISSION_OFFER -> navigateToNewMissionOffer(notification)
            else -> {
                // TODO TBD
            }
        }
    }

    private fun navigateToNewMissionOffer(notification: NotificationModel) {
        viewModel.selectedUnconfirmedMission.value = notification.resourceId
        viewModel.selectTab(R.id.nav_tab_offers)
    }

    private fun setupObservers() {
        viewModel.cmd.observe(this) {
            when (it) {
                is MainActivityViewModel.Command.SignOut -> {
                }
                is MainActivityViewModel.Command.BottomNavVisible -> {
                    if (it.visible) {
                        behaviour?.enabled = true
                        behaviour?.slideUp(binding.mainBottomNav)
                    } else {
                        behaviour?.slideDown(binding.mainBottomNav)
                        behaviour?.enabled = false
                    }
                }
                is MainActivityViewModel.Command.SelectTab -> {
                    binding.mainBottomNav.selectedItemId = it.pageId
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    private fun setupBottomNavigationBar() {
        val navGraphIds = listOf(
            R.navigation.nav_tab_dashboard, R.navigation.nav_tab_planning,
            R.navigation.nav_tab_offers, R.navigation.nav_tab_more
        )

        // Setup the bottom navigation view with a list of navigation graphs
        binding.mainBottomNav.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = binding.mainHostFragment.id,
            intent = intent
        ).observe(this) {
            it.addOnDestinationChangedListener(onDestinationChangedListener)
        }
    }

    companion object {
        fun getMainActivityExtras() = ActivityNavigator.Extras.Builder()
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .build()
    }
}

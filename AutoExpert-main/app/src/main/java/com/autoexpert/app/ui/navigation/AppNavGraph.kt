package com.autoexpert.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.autoexpert.app.ui.home.HomeScreen
import com.autoexpert.app.ui.login.LoginScreen
import com.autoexpert.app.ui.messaging.MessagingScreen
import com.autoexpert.app.ui.customers.CustomerListScreen
import com.autoexpert.app.ui.customers.NewCustomerScreen
import com.autoexpert.app.ui.wallet.WalletScreen
import com.autoexpert.app.ui.profile.ProfileScreen
import com.autoexpert.app.ui.notices.NoticesScreen
import com.autoexpert.app.ui.splash.SplashScreen

object Routes {
    const val SPLASH    = "splash"
    const val LOGIN     = "login"
    const val HOME      = "home"
    const val CUSTOMERS = "customers"
    const val NEW_CUSTOMER = "new_customer"
    const val MESSAGING = "messaging"
    const val WALLET    = "wallet"
    const val PROFILE   = "profile"
    const val NOTICES   = "notices"
}

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(
                onTimeout = { navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }}
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Routes.HOME) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }}
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onNewCustomer  = { navController.navigate(Routes.NEW_CUSTOMER) },
                onOpenChat     = { navController.navigate(Routes.MESSAGING) },
                onOpenNotices  = { navController.navigate(Routes.NOTICES) },
                onOpenCustomers= { navController.navigate(Routes.CUSTOMERS) },
                onOpenWallet   = { navController.navigate(Routes.WALLET) },
                onOpenProfile  = { navController.navigate(Routes.PROFILE) },
            )
        }

        composable(Routes.NEW_CUSTOMER) {
            NewCustomerScreen(onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() })
        }

        composable(Routes.CUSTOMERS) {
            CustomerListScreen(onBack = { navController.popBackStack() },
                onNewCustomer = { navController.navigate(Routes.NEW_CUSTOMER) })
        }

        composable(Routes.MESSAGING) {
            MessagingScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.WALLET) {
            WalletScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.PROFILE) {
            ProfileScreen(onBack = { navController.popBackStack() },
                onLogout = { navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.HOME) { inclusive = true }
                }})
        }

        composable(Routes.NOTICES) {
            NoticesScreen(onBack = { navController.popBackStack() })
        }
    }
}

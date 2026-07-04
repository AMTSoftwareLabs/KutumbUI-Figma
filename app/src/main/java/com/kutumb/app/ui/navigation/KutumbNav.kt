package com.kutumb.app.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kutumb.app.ui.screens.home.KutumbHomeScreen
import com.kutumb.app.ui.screens.karya.KaryaScreen
import com.kutumb.app.ui.screens.niyama.NiyamaScreen
import com.kutumb.app.ui.screens.parichay.ParichayScreen
import com.kutumb.app.ui.screens.rina.RinaScreen
import com.kutumb.app.ui.screens.samvaad.SamvaadScreen
import com.kutumb.app.ui.screens.smriti.SmritiScreen
import com.kutumb.app.ui.screens.soochi.SoochiScreen
import com.kutumb.app.ui.screens.vyaya.VyayaScreen
import com.kutumb.app.ui.theme.LocalSeedTheme
import com.kutumb.app.ui.viewmodel.MainViewModel

sealed class Screen(val route: String) {
    object Home     : Screen("home")
    object Karya    : Screen("karya")
    object Niyama   : Screen("niyama")
    object Vyaya    : Screen("vyaya")
    object Rina     : Screen("rina")
    object Samvaad  : Screen("samvaad")
    object Soochi   : Screen("soochi")
    object Smriti   : Screen("smriti")
    object Parichay : Screen("parichay")
}

data class NavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val activeIcon: ImageVector
)

private val NAV_ITEMS = listOf(
    NavItem(Screen.Home,     "Kutumb",  Icons.Outlined.Home,                Icons.Filled.Home),
    NavItem(Screen.Karya,    "Karya",   Icons.Outlined.Assignment,           Icons.Filled.Assignment),
    NavItem(Screen.Niyama,   "Niyama",  Icons.Outlined.CheckCircle,          Icons.Filled.CheckCircle),
    NavItem(Screen.Vyaya,    "Vyaya",   Icons.Outlined.ShoppingCart,         Icons.Filled.ShoppingCart),
    NavItem(Screen.Rina,     "Rina",    Icons.Outlined.AccountBalanceWallet, Icons.Filled.AccountBalanceWallet),
    NavItem(Screen.Samvaad,  "Samvaad", Icons.Outlined.ChatBubble,           Icons.Filled.ChatBubble),
    NavItem(Screen.Soochi,   "Soochi",  Icons.Outlined.ListAlt,              Icons.Filled.ListAlt),
    NavItem(Screen.Smriti,   "Smriti",  Icons.Outlined.PhotoAlbum,           Icons.Filled.PhotoAlbum),
    NavItem(Screen.Parichay, "Parichay",Icons.Outlined.Person,               Icons.Filled.Person),
)

@Composable
fun KutumbApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    LaunchedEffect(Unit) { viewModel.seedIfEmpty() }

    Scaffold(
        bottomBar = {
            KutumbBottomBar(
                currentRoute = currentRoute,
                onNavigate   = { screen ->
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { _ ->
        NavHost(navController = navController, startDestination = Screen.Home.route) {
            composable(Screen.Home.route)     { KutumbHomeScreen(viewModel) }
            composable(Screen.Karya.route)    { KaryaScreen(viewModel) }
            composable(Screen.Niyama.route)   { NiyamaScreen(viewModel) }
            composable(Screen.Vyaya.route)    { VyayaScreen(viewModel) }
            composable(Screen.Rina.route)     { RinaScreen(viewModel) }
            composable(Screen.Samvaad.route)  { SamvaadScreen(viewModel) }
            composable(Screen.Soochi.route)   { SoochiScreen(viewModel) }
            composable(Screen.Smriti.route)   { SmritiScreen(viewModel) }
            composable(Screen.Parichay.route) { ParichayScreen(viewModel) }
        }
    }
}

@Composable
fun KutumbBottomBar(currentRoute: String?, onNavigate: (Screen) -> Unit) {
    val seed = LocalSeedTheme.current
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        NAV_ITEMS.forEach { item ->
            val selected = currentRoute == item.screen.route
            NavigationBarItem(
                selected = selected,
                onClick  = { onNavigate(item.screen) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.activeIcon else item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(item.label, fontSize = 9.sp, fontWeight = if (selected)
                        androidx.compose.ui.text.font.FontWeight.ExtraBold
                    else androidx.compose.ui.text.font.FontWeight.Medium)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = seed.primary,
                    selectedTextColor   = seed.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor      = seed.primary.copy(alpha = 0.12f)
                )
            )
        }
    }
}

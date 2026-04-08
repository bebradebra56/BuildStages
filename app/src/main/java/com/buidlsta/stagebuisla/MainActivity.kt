package com.buidlsta.stagebuisla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.buidlsta.stagebuisla.data.preferences.AppPreferences
import com.buidlsta.stagebuisla.ui.navigation.AppNavHost
import com.buidlsta.stagebuisla.ui.navigation.BottomNavBar
import com.buidlsta.stagebuisla.ui.navigation.Screen
import com.buidlsta.stagebuisla.ui.navigation.isBottomNavRoute
import com.buidlsta.stagebuisla.ui.theme.BuildStagesTheme
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val prefs: AppPreferences = koinInject()
            val themeMode by prefs.themeMode.collectAsState(initial = "System")
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (themeMode) {
                "Dark" -> true
                "Light" -> false
                else -> systemDark
            }
            BuildStagesTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val showBottomBar = isBottomNavRoute(currentRoute)

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

package com.example.chatapp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.ui.theme.ChatAppTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true

            FirebaseApp.initializeApp(this)

            FirebaseDatabase.getInstance().setPersistenceEnabled(true)

            val navHostController = rememberNavController()

            AppNavigation(navHostController)
        }
    }
}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = "WelcomeScreen"
    ) {
        composable("MainChat") {
            AnimatedContent(
                targetState = "MainChat",
                transitionSpec = {
                    slideInHorizontally(initialOffsetX = { it }) with slideOutHorizontally(
                        targetOffsetX = { -it })
                }
            ) {
                MainChat(navHostController)
            }
        }
        composable("WelcomeScreen") { WelcomeScreen(navHostController) }
    }
}

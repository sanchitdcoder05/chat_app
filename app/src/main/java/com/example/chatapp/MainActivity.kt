package com.example.chatapp

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.chatapp.roomdb.AppDatabase
import com.example.chatapp.roomdb.User
import com.example.chatapp.roomdb.UserDao
import androidx.room.Room
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
            FirebaseApp.initializeApp(this)
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)

            val db = AppDatabase.getDatabase(this)
            val userDao = db.userDao()

            val navHostController = rememberNavController()
            AppNavigation(navHostController, userDao)
        }
    }
}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(navHostController: NavHostController, userDao: UserDao) {
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
                MainChat(navHostController, userDao)
            }
        }
        composable("WelcomeScreen") { WelcomeScreen(navHostController) }
    }
}


@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

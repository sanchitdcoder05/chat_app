package com.example.chatapp

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(navHostController: NavHostController) {
    var clicked by remember { mutableStateOf(false) }

    val welcomeText = "Welcome"
    val toText = "to"
    val chatAppText = "Chat App"

    val transitionState = remember { MutableTransitionState(false) }
    val slideInAnimation = updateTransition(transitionState, label = "LetterSlideIn")

    val welcomeOffsets = welcomeText.mapIndexed { index, _ ->
        slideInAnimation.animateDp(
            label = "Welcome Letter $index",
            transitionSpec = { tween(durationMillis = 500, easing = FastOutSlowInEasing) }
        ) { state -> if (state) 0.dp else if (index % 2 == 0) -200.dp else 200.dp }
    }

    val toOffsets = toText.mapIndexed { index, _ ->
        slideInAnimation.animateDp(
            label = "To Letter $index",
            transitionSpec = { tween(durationMillis = 500, easing = FastOutSlowInEasing) }
        ) { state -> if (state) 0.dp else if (index % 2 == 0) -200.dp else 200.dp }
    }

    val chatAppOffsets = chatAppText.mapIndexed { index, _ ->
        slideInAnimation.animateDp(
            label = "Chat App Letter $index",
            transitionSpec = { tween(durationMillis = 500, easing = FastOutSlowInEasing) }
        ) { state -> if (state) 0.dp else if (index % 2 == 0) -200.dp else 200.dp }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF73B1E2)),
        contentAlignment = Alignment.Center
    ) {
        if (!clicked) {
            LaunchedEffect(Unit) {
                transitionState.targetState = true
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row {
                    welcomeText.forEachIndexed { index, letter ->
                        Text(
                            text = letter.toString(),
                            fontSize = 95.sp,
                            color = Color.White,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.offset(x = welcomeOffsets[index].value)
                        )
                    }
                }

                Row {
                    toText.forEachIndexed { index, letter ->
                        Text(
                            text = letter.toString(),
                            fontSize = 20.sp,
                            color = Color.White,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.offset(x = toOffsets[index].value)
                        )
                    }
                }

                Row {
                    chatAppText.forEachIndexed { index, letter ->
                        Text(
                            text = letter.toString(),
                            fontSize = 50.sp,
                            color = Color.White,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.offset(x = chatAppOffsets[index].value)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(111.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Button(
                    onClick = { clicked = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier
                        .width(150.dp)
                        .height(50.dp)
                ) {
                    Text(text = "Get Started...", color = Color(0xFF62ACE9), fontSize = 16.sp)
                }
            }
        } else {
            WhiteBlockAnimation {
                navHostController.navigate("MainChat")
            }
        }
    }
}

@Composable
fun WhiteBlockAnimation(onAnimationEnd: () -> Unit) {
    var animationHeight by remember { mutableStateOf(0.dp) }

    val transition = updateTransition(targetState = animationHeight, label = "blockAnimation")
    val animatedHeight by transition.animateDp(
        transitionSpec = { tween(durationMillis = 2000, easing = FastOutSlowInEasing) },
        label = "HeightAnimation"
    ) { state -> state }

    LaunchedEffect(animatedHeight) {
        if (animatedHeight == 10000.dp) {
            onAnimationEnd()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF62ACE9))
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(animatedHeight)
                .background(Color.White, shape = RoundedCornerShape(30.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Text(
                    text = "Welcome to My Chat App",
                    fontSize = 32.sp,
                    color = Color.Black,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Loading...",
                    fontSize = 22.sp,
                    color = Color.Black,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(5.dp)
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(500)
        animationHeight = 10000.dp
    }
}

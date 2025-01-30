package com.example.chatapp

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainChat(
    navHostController: NavHostController
) {
    var message by remember { mutableStateOf("") }
    val database = Firebase.database
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(true) }
    var isAuthenticated by remember { mutableStateOf(false) }
    var key by remember { mutableStateOf("") }



    val messagesRef = database.reference.child("Message")

    var messageList by remember { mutableStateOf(listOf<Message>()) }


    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    var launchCount by remember {
        mutableStateOf(sharedPreferences.getInt("launchCount", 0))
    }

    launchCount++
    sharedPreferences.edit().putInt("launchCount", launchCount).apply()

    if(launchCount==1){
        navHostController.navigate("WelcomeScreen")
    }






    LaunchedEffect(Unit) {
        val listener = messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val messageListTemp = mutableListOf<Message>()
                for (messageSnapshot in dataSnapshot.children) {
                    val messageText = messageSnapshot.child("text").value.toString()
                    val messageAuthor = messageSnapshot.child("sender").value.toString()
                    val messageSentTime = messageSnapshot.child("sentTime").value.toString()
                    messageListTemp.add(Message(messageText, messageAuthor, messageSentTime))
                }
                messageList = messageListTemp
            }

            override fun onCancelled(databaseError: DatabaseError){
                println("Error: ${databaseError.message}")
            }
        })
    }


    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Chat App") },
                actions = {
                    IconButton(
                        onClick = {
                            isVisible = !isVisible
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF7FAFD),
                    titleContentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 1.dp)
                    .border(1.dp, Color.Black, shape = RectangleShape)
            )
        },
        content = { paddingValues ->

            Column (
                modifier = Modifier.padding(paddingValues)
            ){

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(messageList){message->
                        Column(
                            modifier = Modifier.padding(12.dp)
                                .background(color = Color(0xFF9BCCF4), RoundedCornerShape(1.dp))
                                .border(1.dp, Color.Black, shape = RoundedCornerShape(1.dp))
                        ){
                            Row(
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Text(message.text, color = Color.Black, fontSize = 15.sp,
                                    modifier = Modifier.widthIn(max = 298.dp)
                                    )

                                Spacer(modifier = Modifier.width(7.dp))

                                Column {
                                    Text(" ~"+message.sender, color = Color.DarkGray, fontSize = 10.sp)
                                    Text("  "+message.sentTime, color = Color.DarkGray, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFFF7FAFD),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.fillMaxWidth()
                .border(1.dp, Color.Black, shape = RectangleShape)
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        placeholder = { Text("Type your message...") },
                        shape = RoundedCornerShape(1.dp),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (message.isNotEmpty()) {
                                        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                                        val currentDateAndTime = sdf.format(Date())

                                        val newMessage = Message(text = message, sender = username, sentTime = currentDateAndTime)

                                        val messagesRef = database.reference.child("Message")
                                        val messageRef = messagesRef.push()
                                        messageRef.setValue(newMessage)

                                        messageList = messageList + newMessage

                                        Toast.makeText(context, "Message sent successfully", Toast.LENGTH_SHORT).show()

                                        message = ""
                                    } else {
                                        Toast.makeText(context, "Message cannot be empty", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send",
                                    tint = Color.Black
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    )
    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .align(Alignment.Center)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.border(2.dp, Color.Black, shape = RoundedCornerShape(15.dp))
                    ){
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Please Set Your Username !",
                                fontSize = 32.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 16.dp),
                                fontFamily = FontFamily.SansSerif
                            )
                            OutlinedTextField(
                                value = username,
                                onValueChange = { username = it },
                                label = { Text("Your Username") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = key,
                                onValueChange = { key = it },
                                label = { Text("Enter Key to Get Authenticated") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    username = username
                                    if (key=="123" &&username != ""){
                                        isAuthenticated = true
                                        Toast.makeText(context, "Access Granted", Toast.LENGTH_SHORT).show()
                                        isVisible = false
                                    }
                                    else if(username == ""){
                                        Toast.makeText(context, "username cannot be empty", Toast.LENGTH_SHORT).show()
                                    }
                                    else{
                                        Toast.makeText(context, "Access Denied", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                            ) {
                                Text("Set UserName")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}
data class Message(
    val text: String = "",
    val sender: String = "",
    val sentTime: String = ""
)
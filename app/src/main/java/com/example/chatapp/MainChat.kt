package com.example.chatapp

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.chatapp.roomdb.UserDao
import com.example.chatapp.roomdb.UserViewModel
import com.example.chatapp.roomdb.UserViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import androidx.compose.runtime.collectAsState
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainChat(navHostController: NavHostController, userDao: UserDao) {
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(userDao))

    var username by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        userViewModel.username.collect { newUsername ->
            username = newUsername ?: ""
        }
    }

    var message by remember { mutableStateOf("") }
    val database = Firebase.database
    val context = LocalContext.current
    var isVisible by remember { mutableStateOf(false) }
    var isAuthenticated by remember { mutableStateOf(false) }
    var key by remember { mutableStateOf("") }
    val messagesRef = database.reference.child("Message")
    var messageList by remember { mutableStateOf(listOf<Message>()) }
    val listState = rememberLazyListState()

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

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error: ${databaseError.message}")
            }
        })
    }

    LaunchedEffect(messageList.size) {
        if (messageList.isNotEmpty()) {
            listState.animateScrollToItem(messageList.size - 1)
        }
    }


    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    var launchCount by remember {
        mutableStateOf(sharedPreferences.getInt("launchCount", 0))
    }

    launchCount++
    sharedPreferences.edit().putInt("launchCount", launchCount).apply()

    if(launchCount==1){
        isVisible = true
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat App") },
                actions = {
                    IconButton(onClick = { isVisible = !isVisible }) {
                        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Account")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF7FAFD),
                    titleContentColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black)
            )
        },
        content = { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    state = listState,
                    reverseLayout = false
                ) {
                    items(messageList) { message ->
                        if(message.sender==username){
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .background(
                                            color = Color(0xFF9BCCF4),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
                                ) {
                                    Row(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            message.text,
                                            color = Color.Black,
                                            fontSize = 15.sp,
                                            modifier = Modifier.widthIn(max = 298.dp)
                                        )
                                        Spacer(modifier = Modifier.width(7.dp))
                                        Column {
                                            Text(" ~${message.sender}", color = Color.DarkGray, fontSize = 10.sp)
                                            Text("  ${message.sentTime}", color = Color.DarkGray, fontSize = 10.sp)
                                        }

                                    }
                                }
                            }
                        }
                        else{
                            Column(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .background(
                                        color = Color(0xFF9BCCF4),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
                            ) {
                                Row(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        message.text,
                                        color = Color.Black,
                                        fontSize = 15.sp,
                                        modifier = Modifier.widthIn(max = 298.dp)
                                    )
                                    Spacer(modifier = Modifier.width(7.dp))
                                    Column {
                                        Text(" ~${message.sender}", color = Color.DarkGray, fontSize = 10.sp)
                                        Text("  ${message.sentTime}", color = Color.DarkGray, fontSize = 10.sp)
                                    }

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
                modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black)
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        placeholder = { Text("Type your message...") },
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(onClick = {
                                if (message.isNotEmpty()) {
                                    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                                    val currentDateAndTime = sdf.format(Date())
                                    val newMessage = Message(
                                        text = message,
                                        sender = username,
                                        sentTime = currentDateAndTime
                                    )
                                    messagesRef.push().setValue(newMessage)
                                    messageList = messageList + newMessage
                                    message = ""
                                } else {
                                    Toast.makeText(context, "Message cannot be empty", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send",
                                    tint = Color.Black
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp)
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
                    ) {
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
                                value = username, // Use the manually collected username
                                onValueChange = { userViewModel.saveUsername(it) },
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
                                    if (key == "123" && username.isNotEmpty()) {
                                        isAuthenticated = true
                                        Toast.makeText(context, "Access Granted", Toast.LENGTH_SHORT).show()
                                        isVisible = false
                                    } else if (username.isEmpty()) {
                                        Toast.makeText(context, "Username cannot be empty", Toast.LENGTH_SHORT).show()
                                    } else {
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


data class Message(val text: String = "", val sender: String = "", val sentTime: String = "")
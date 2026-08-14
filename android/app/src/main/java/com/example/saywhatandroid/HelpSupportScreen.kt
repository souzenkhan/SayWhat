package com.example.saywhatandroid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HelpSupportScreen(
    onHomeClick: () -> Unit,
    onScanClick: () -> Unit,
    onAudioClick: () -> Unit,
    onAboutClick: () -> Unit,
    onConnectionErrorClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    var searchText by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            HelpBottomBar(
                onHomeClick = onHomeClick,
                onScanClick = onScanClick,
                onAudioClick = onAudioClick,
                onHelpClick = {}
            )
        },
        containerColor = Color(0xFFF8F8FF)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HelpTopHeader(onSettingsClick = onSettingsClick)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Help & Support",
                color = Color(0xFF17172A),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Find answers to common questions\nand troubleshooting tips for your\nhearing assistance experience.",
                color = Color(0xFF17172A),
                fontSize = 17.sp,
                lineHeight = 25.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = {
                    Text(
                        text = "Search help topics",
                        fontSize = 16.sp,
                        color = Color(0xFF777777)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF17172A),
                    unfocusedTextColor = Color(0xFF17172A),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color(0xFF3047E8),
                    unfocusedIndicatorColor = Color(0xFFD0D0D8),
                    cursorColor = Color(0xFF3047E8)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            HowToConnectCard()

            Spacer(modifier = Modifier.height(22.dp))

            TroubleshootingCard(
                onConnectionErrorClick = onConnectionErrorClick
            )

            Spacer(modifier = Modifier.height(22.dp))

            FAQSection()

            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
fun HelpTopHeader(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_hearing),
            contentDescription = "Say What Logo",
            tint = Color(0xFF3047E8),
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = "Say What?",
            color = Color(0xFF3047E8),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_settings),
            contentDescription = "Settings",
            tint = Color(0xFF3047E8),
            modifier = Modifier
                .size(22.dp)
                .clickable {
                    onSettingsClick()
                }
        )
    }
}

@Composable
fun HowToConnectCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE9EBFF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⌁",
                    color = Color(0xFF3047E8),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.size(10.dp))

                Text(
                    text = "How to Connect",
                    color = Color(0xFF3047E8),
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            ConnectStep(
                number = "1",
                title = "JOIN WI-FI",
                body = "Locate your phone's Settings app. Navigate to Wi-Fi and make sure it is turned on. Look for the network named Venue_Guest_WiFi or similar."
            )

            Spacer(modifier = Modifier.height(20.dp))

            ConnectStep(
                number = "2",
                title = "SCAN QR CODE",
                body = "Open the Say What? app and tap the Connect icon in the bottom navigation bar. Center the venue QR code on the poster or table sign inside the camera frame."
            )

            Spacer(modifier = Modifier.height(20.dp))

            ConnectStep(
                number = "3",
                title = "START LISTENING",
                body = "Plug in wired headphones or verify your Bluetooth device is connected. Navigate to the Audio screen to adjust volume and balance."
            )
        }
    }
}

@Composable
fun ConnectStep(
    number: String,
    title: String,
    body: String
) {
    Column {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFF3047E8), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = title,
            color = Color(0xFF17172A),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = body,
            color = Color(0xFF17172A),
            fontSize = 15.sp,
            lineHeight = 23.sp
        )
    }
}

@Composable
fun TroubleshootingCard(
    onConnectionErrorClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "🔧",
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Troubleshooting",
                color = Color(0xFF17172A),
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Quick fixes for common audio and connection issues.",
                color = Color(0xFF17172A),
                fontSize = 15.sp,
                lineHeight = 23.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onConnectionErrorClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3047E8)
                )
            ) {
                Text(
                    text = "View Fixes",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FAQSection() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "FAQs",
            color = Color(0xFF17172A),
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(14.dp))

        FAQDropdown(
            question = "Why is my audio delayed?",
            answer = "A small delay can happen while the stream buffers. Make sure you are on the venue Wi-Fi and close other apps using audio."
        )

        Spacer(modifier = Modifier.height(12.dp))

        FAQDropdown(
            question = "Can I use Bluetooth hearing aids?",
            answer = "Yes. Pair your hearing aids or earbuds with your phone before starting the audio stream."
        )

        Spacer(modifier = Modifier.height(12.dp))

        FAQDropdown(
            question = "What if the QR code does not work?",
            answer = "Check that the camera is focused and the full QR code is inside the frame. You can also ask venue staff for help."
        )
    }
}

@Composable
fun FAQDropdown(
    question: String,
    answer: String
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                expanded = !expanded
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    color = Color(0xFF17172A),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = if (expanded) "⌃" else "⌄",
                    color = Color(0xFF3047E8),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = answer,
                    color = Color(0xFF333333),
                    fontSize = 15.sp,
                    lineHeight = 23.sp
                )
            }
        }
    }
}

@Composable
fun HelpBottomBar(
    onHomeClick: () -> Unit,
    onScanClick: () -> Unit,
    onAudioClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Color.White)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HelpBottomNavIcon(
            iconRes = R.drawable.baseline_home_24,
            label = "Home",
            selected = false,
            onClick = onHomeClick
        )

        HelpBottomNavIcon(
            iconRes = R.drawable.baseline_qr_code_scanner_24,
            label = "Connect",
            selected = false,
            onClick = onScanClick
        )

        HelpBottomNavIcon(
            iconRes = R.drawable.ic_recent,
            label = "Recent",
            selected = false,
            onClick = onAudioClick
        )

        HelpBottomNavText(
            icon = "?",
            label = "Help",
            selected = true,
            onClick = onHelpClick
        )
    }
}

@Composable
fun HelpBottomNavIcon(
    iconRes: Int,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) Color.White else Color(0xFF777777)
    val backgroundColor = if (selected) Color(0xFF4B55E7) else Color.Transparent

    Column(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(28.dp))
            .clickable {
                onClick()
            }
            .padding(
                horizontal = if (selected) 16.dp else 6.dp,
                vertical = if (selected) 8.dp else 4.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun HelpBottomNavText(
    icon: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) Color.White else Color(0xFF777777)
    val backgroundColor = if (selected) Color(0xFF4B55E7) else Color.Transparent

    Column(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(28.dp))
            .clickable {
                onClick()
            }
            .padding(
                horizontal = if (selected) 16.dp else 6.dp,
                vertical = if (selected) 8.dp else 4.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            color = color,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

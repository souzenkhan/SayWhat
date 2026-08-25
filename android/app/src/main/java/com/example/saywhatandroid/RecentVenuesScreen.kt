package com.example.saywhatandroid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecentVenuesScreen(
    sessions: List<VenueSession>,
    onHomeClick: () -> Unit,
    onConnectClick: () -> Unit,
    onHelpClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Scaffold(
        topBar = { TopHeader(onSettingsClick = onSettingsClick, onAboutClick = {}) },
        bottomBar = {
            RecentBottomBar(onHomeClick, onConnectClick, onHelpClick)
        },
        containerColor = Color(0xFFF8F8FF)
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recent Venues", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color(0xFF17172A))
                Text("View all", fontSize = 14.sp, color = Color(0xFF3047E8))
            }
            Spacer(Modifier.height(14.dp))
            if (sessions.isEmpty()) {
                Text(
                    "Your connected venues will appear here.",
                    color = Color(0xFF5F5F6D),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 28.dp)
                )
            } else {
                sessions.forEachIndexed { index, session ->
                    RecentVenueCard("⌂", session.venueName, "Connected session")
                    if (index < sessions.lastIndex) Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun RecentVenueCard(icon: String, title: String, subtitle: String) {
    Card(
        modifier = Modifier.fillMaxWidth().height(92.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 28.sp, modifier = Modifier.size(58.dp).clip(RoundedCornerShape(7.dp)).background(Color(0xFFE9EBFF)).padding(12.dp))
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF17172A))
                Spacer(Modifier.height(5.dp))
                Text(subtitle, fontSize = 13.sp, color = Color(0xFF5F5F6D))
            }
            Text("›", fontSize = 24.sp, color = Color(0xFF3047E8))
        }
    }
}

@Composable
private fun RecentBottomBar(onHome: () -> Unit, onConnect: () -> Unit, onHelp: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(72.dp).background(Color.White).padding(horizontal = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(R.drawable.baseline_home_24, "Home", false, onHome)
        BottomNavItem(R.drawable.baseline_qr_code_scanner_24, "Connect", false, onConnect)
        BottomNavItem(R.drawable.ic_recent, "Recent", true, {})
        BottomNavTextItem("?", "Help", false, onHelp)
    }
}

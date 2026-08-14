package com.example.saywhatandroid

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
fun TranslateScreen(onBack: () -> Unit, onSettingsClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF8F8FF)).padding(horizontal = 20.dp)
    ) {
        AudioTopHeader(onSettingsClick)
        Spacer(Modifier.height(14.dp))
        CompactSessionCard()
        Spacer(Modifier.height(14.dp))
        TranslationCard("English (US)", "Out, damned spot! Out, I say!\nOne, two—why, then ‘tis time to do't.")
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(Color(0xFF3047E8)), contentAlignment = Alignment.Center) {
                Text("↓", color = Color.White, fontSize = 25.sp)
            }
        }
        TranslationCard("Spanish (Spain)", "¡Fuera, maldita mancha! ¡Fuera, te digo! Una, dos... bien, entonces es hora de hacerlo.")
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 26.dp).height(42.dp),
            shape = RoundedCornerShape(7.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5069C9))
        ) { Text("Back", fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun CompactSessionCard() {
    Card(
        modifier = Modifier.fillMaxWidth().height(126.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.fillMaxSize().padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("● LIVE", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(Color(0xFFB92D2D)).padding(horizontal = 10.dp, vertical = 6.dp))
                Text("Session Active", color = Color(0xFF17172A), fontSize = 14.sp, modifier = Modifier.padding(start = 10.dp))
                Spacer(Modifier.weight(1f))
                Text("♡", color = Color(0xFF3047E8), fontSize = 22.sp)
            }
            Spacer(Modifier.height(10.dp))
            Text("Grand Concert Hall ↗", color = Color(0xFF17172A), fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Text("◷  00:42:17", color = Color(0xFF44444F), fontSize = 13.sp)
        }
    }
}

@Composable
private fun TranslationCard(language: String, copy: String) {
    Card(
        modifier = Modifier.fillMaxWidth().height(170.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.fillMaxSize().padding(14.dp)) {
            Text(language, color = Color(0xFF3047E8), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(copy, color = Color(0xFF585865), fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

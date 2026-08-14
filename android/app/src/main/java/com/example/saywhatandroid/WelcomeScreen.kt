package com.example.saywhatandroid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(onTakeTour: () -> Unit, onSkip: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8FF))
            .padding(horizontal = 30.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_hearing),
                    contentDescription = null,
                    tint = Color(0xFF3047E8),
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(Modifier.height(34.dp))
            Text("Welcome to", color = Color(0xFF17172A), fontSize = 22.sp)
            Text(
                "Say What!",
                color = Color(0xFF3047E8),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(22.dp))
            Text(
                "Click here to take a brief\ntour of the app!",
                color = Color(0xFF333333),
                fontSize = 17.sp,
                lineHeight = 25.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(38.dp))
            Button(
                onClick = onTakeTour,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3047E8))
            ) {
                Text("Take the Tour  ›", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                "Skip for now",
                color = Color(0xFF6B6B7A),
                fontSize = 15.sp,
                modifier = Modifier.padding(20.dp).clickable(onClick = onSkip)
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(58.dp)
                .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                .background(Color(0xFFE2EBFF))
        )
    }
}

package com.example.saywhatandroid

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QRScanScreen(
    onUseScanClick: () -> Unit,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onScanClick: () -> Unit,
    onAudioClick: () -> Unit,
    onHelpClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Scaffold(
        topBar = {
            QRScanTopHeader(onSettingsClick = onSettingsClick)
        },
        bottomBar = {
            QRScanBottomBar(
                onHomeClick = onHomeClick,
                onScanClick = onScanClick,
                onAudioClick = onAudioClick,
                onHelpClick = onHelpClick
            )
        },
        containerColor = Color(0xFFF8F8FF)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Scan QR Code",
                color = Color(0xFF17172A),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(
                        color = Color(0xFF8E8E8E),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(185.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .border(
                            width = 3.dp,
                            color = Color(0xFF3047E8),
                            shape = RoundedCornerShape(6.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_qr_code_scanner_24),
                        contentDescription = "QR Code Scanner",
                        tint = Color(0xFF3047E8),
                        modifier = Modifier.size(66.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = onUseScanClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3047E8)
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_qr_code_scanner_24),
                    contentDescription = "Use Scan",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.size(10.dp))

                Text(
                    text = "Use Scan",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF3047E8)
                )
            ) {
                Text(
                    text = "← Back",
                    color = Color(0xFF3047E8),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun QRScanTopHeader(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
            .padding(horizontal = 20.dp),
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
            fontSize = 22.sp,
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
fun QRScanBottomBar(
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
            .padding(horizontal = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        QRScanBottomNavItem(
            iconRes = R.drawable.baseline_home_24,
            label = "Home",
            selected = false,
            onClick = onHomeClick
        )

        QRScanBottomNavItem(
            iconRes = R.drawable.baseline_qr_code_scanner_24,
            label = "Connect",
            selected = true,
            onClick = onScanClick
        )

        QRScanBottomNavItem(
            iconRes = R.drawable.ic_recent,
            label = "Recent",
            selected = false,
            onClick = onAudioClick
        )

        QRScanBottomNavTextItem(
            icon = "?",
            label = "Help",
            selected = false,
            onClick = onHelpClick
        )
    }
}

@Composable
fun QRScanBottomNavItem(
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
fun QRScanBottomNavTextItem(
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

package com.j4m1eb.averagespeedcolour.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(onGetStarted: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Color Speed",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A73E8),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Speed fields with live colour feedback",
                fontSize = 13.sp,
                color = Color(0xFF444444),
                textAlign = TextAlign.Center
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ColorKeyRow(
                sampleText = "30.4",
                bgColor = Color(0xFF1EFF00),
                textColor = Color.Black,
                label = "Above average speed"
            )
            ColorKeyRow(
                sampleText = "26.4",
                bgColor = Color(0xFFD60303),
                textColor = Color.White,
                label = "Below average speed"
            )
            ColorKeyRow(
                sampleText = "28.4",
                bgColor = Color(0xFF444444),
                textColor = Color.White,
                label = "At average / neutral"
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Add fields via the Karoo data field editor. Choose from ride average, lap average, or a custom target speed. Settings let you switch to teal and toggle direction icons.",
                fontSize = 12.sp,
                color = Color(0xFF444444),
                textAlign = TextAlign.Center,
                lineHeight = 17.sp
            )
        }

        FilledTonalButton(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Open settings", fontSize = 14.sp)
        }
    }
}

@Composable
private fun ColorKeyRow(sampleText: String, bgColor: Color, textColor: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = sampleText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(bgColor)
                .padding(horizontal = 10.dp, vertical = 4.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF222222)
        )
    }
}

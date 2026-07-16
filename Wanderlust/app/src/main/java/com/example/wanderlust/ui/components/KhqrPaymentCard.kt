package com.example.wanderlust.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wanderlust.R

/** ABA-inspired KHQR receive card (layout only — not an official ABA template). */
private val KhqrRed = Color(0xFFE11D2E)
private val KhqrCardBg = Color(0xFFFFFBF7)
private val KhqrInk = Color(0xFF1A1A1A)
private val KhqrMuted = Color(0xFF6B6B6B)

@Composable
fun KhqrPaymentCard(
    qrBitmap: Bitmap,
    merchantName: String,
    amountLabel: String,
    billNumber: String? = null,
    currencyCode: String = "USD",
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(10.dp, shape, clip = false)
            .clip(shape)
            .background(KhqrCardBg)
            .border(1.dp, Color(0xFFE8E0D8), shape),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(KhqrRed)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "KHQR",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                letterSpacing = 2.sp,
            )
        }

        Spacer(Modifier.height(14.dp))
        Text(
            text = merchantName.ifBlank { "Wanderlust" }.uppercase(),
            color = KhqrInk,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(10.dp))
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(0.72f)
                .padding(bottom = 12.dp),
            color = Color(0xFFD9D0C6),
            thickness = 1.dp,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.78f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                bitmap = qrBitmap.asImageBitmap(),
                contentDescription = "KHQR",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Fit,
            )
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_khqr_mark),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        Text(
            text = amountLabel,
            color = KhqrInk,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            text = currencyCode.uppercase(),
            color = KhqrMuted,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 2.dp),
        )
        billNumber?.takeIf { it.isNotBlank() }?.let { bill ->
            Text(
                text = bill,
                color = KhqrMuted,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Scan with any KHQR bank app",
            color = KhqrMuted,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF3EEE8))
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "BAKONG  ·  WANDERLUST",
                color = KhqrMuted,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                letterSpacing = 1.2.sp,
            )
        }
    }
}

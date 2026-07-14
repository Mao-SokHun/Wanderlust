package com.example.wanderlust.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.wanderlust.R

/**
 * [Kantumruy Pro](https://fonts.google.com/specimen/Kantumruy+Pro) —
 * designed for Khmer + Latin, used as the app typeface.
 */
val KantumruyProFamily = FontFamily(
    Font(R.font.kantumruy_pro, weight = FontWeight.Normal),
    Font(R.font.kantumruy_pro, weight = FontWeight.Medium),
    Font(R.font.kantumruy_pro, weight = FontWeight.SemiBold),
    Font(R.font.kantumruy_pro, weight = FontWeight.Bold),
)

val WanderlustTypography = Typography(
    displayLarge = TextStyle(fontFamily = KantumruyProFamily, fontWeight = FontWeight.Bold, fontSize = 32.sp),
    displayMedium = TextStyle(fontFamily = KantumruyProFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp),
    headlineLarge = TextStyle(fontFamily = KantumruyProFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp),
    headlineMedium = TextStyle(fontFamily = KantumruyProFamily, fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
    headlineSmall = TextStyle(fontFamily = KantumruyProFamily, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    titleLarge = TextStyle(fontFamily = KantumruyProFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp),
    titleMedium = TextStyle(fontFamily = KantumruyProFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    titleSmall = TextStyle(fontFamily = KantumruyProFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    bodyLarge = TextStyle(
        fontFamily = KantumruyProFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 26.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = KantumruyProFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 24.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = KantumruyProFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(fontFamily = KantumruyProFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = KantumruyProFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp),
    labelSmall = TextStyle(fontFamily = KantumruyProFamily, fontWeight = FontWeight.Medium, fontSize = 11.sp),
)

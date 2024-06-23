package com.example.eventtrackerkotlincompose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Double,
    numberOfReviews: Int,
    onRatingChanged: (Int) -> Unit
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..5) {
            val starRating = i - 0.5
            Icon(
                imageVector = when {
                    rating >= i -> Icons.Filled.Star
                    rating >= starRating -> Icons.Filled.StarHalf
                    else -> Icons.Filled.StarBorder
                },
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        onRatingChanged(i)
                    },
                tint = if (rating >= starRating) Color(0xFFFFD700) else Color.Gray
            )
        }
        Text("($numberOfReviews)")
    }
}
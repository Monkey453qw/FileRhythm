package com.filerhythm.app.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Material Design 3 Expressive Shape System
 * Directly inspired by Rhythm's design language — organic, rounded corners with
 * asymmetric variants for sheets.
 */
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

object ExpressiveShapeTokens {
    val Full = CircleShape
    val ExtraLarge = RoundedCornerShape(32.dp)
    val Large = RoundedCornerShape(28.dp)
    val Medium = RoundedCornerShape(20.dp)
    val Small = RoundedCornerShape(14.dp)
    val ExtraSmall = RoundedCornerShape(10.dp)

    // Asymmetric shapes for sheets (Rhythm-style)
    val TopSheet = RoundedCornerShape(
        topStart = 0.dp, topEnd = 0.dp,
        bottomStart = 28.dp, bottomEnd = 28.dp
    )
    val BottomSheet = RoundedCornerShape(
        topStart = 28.dp, topEnd = 28.dp,
        bottomStart = 0.dp, bottomEnd = 0.dp
    )
}

/**
 * Custom shapes for file manager specific components
 */
object FileShapes {
    val FileCard = RoundedCornerShape(16.dp)
    val FolderCard = RoundedCornerShape(20.dp)
    val CategoryCard = RoundedCornerShape(20.dp)
    val StatCard = RoundedCornerShape(20.dp)

    // Pill-shaped components (search bar, chips, FAB)
    val SearchBar = CircleShape
    val Chip = CircleShape
    val FAB = RoundedCornerShape(20.dp)

    // Bottom sheet (top-rounded, 28dp)
    val BottomSheet = RoundedCornerShape(
        topStart = 28.dp, topEnd = 28.dp,
        bottomStart = 0.dp, bottomEnd = 0.dp
    )
    val Dialog = RoundedCornerShape(28.dp)

    // Navigation
    val NavBarIndicator = CircleShape
    val NavBarContainer = RoundedCornerShape(
        topStart = 28.dp, topEnd = 28.dp,
        bottomStart = 0.dp, bottomEnd = 0.dp
    )

    // Progress
    val ProgressTrack = CircleShape
}

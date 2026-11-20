package com.kev.fdjapptest.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SportsBaseball
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SportsFootball
import androidx.compose.material.icons.filled.SportsGolf
import androidx.compose.material.icons.filled.SportsHandball
import androidx.compose.material.icons.filled.SportsHockey
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material.icons.filled.SportsMotorsports
import androidx.compose.material.icons.filled.SportsRugby
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.SportsVolleyball
import androidx.compose.ui.graphics.vector.ImageVector

/** Icon for a TheSportsDB sport label ("Soccer", "Basketball", "Ice Hockey"...); a trophy otherwise. */
fun sportIcon(sport: String?): ImageVector {
    val s = sport?.lowercase().orEmpty()
    return when {
        s == "soccer" -> Icons.Default.SportsSoccer
        "basketball" in s -> Icons.Default.SportsBasketball
        "hockey" in s -> Icons.Default.SportsHockey
        "american football" in s || "australian football" in s -> Icons.Default.SportsFootball
        "baseball" in s -> Icons.Default.SportsBaseball
        "rugby" in s -> Icons.Default.SportsRugby
        "tennis" in s -> Icons.Default.SportsTennis
        "volleyball" in s -> Icons.Default.SportsVolleyball
        "handball" in s -> Icons.Default.SportsHandball
        "cricket" in s -> Icons.Default.SportsCricket
        "golf" in s -> Icons.Default.SportsGolf
        "motorsport" in s -> Icons.Default.SportsMotorsports
        "fighting" in s || "mma" in s -> Icons.Default.SportsMma
        "esports" in s -> Icons.Default.SportsEsports
        else -> Icons.Default.EmojiEvents
    }
}

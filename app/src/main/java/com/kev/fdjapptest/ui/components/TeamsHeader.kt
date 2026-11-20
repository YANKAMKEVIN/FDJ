package com.kev.fdjapptest.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import com.kev.fdjapptest.R

/** "French Ligue 1 · 18 teams": what is shown and how many; the count is left out while loading. */
@Composable
fun TeamsHeader(
    leagueName: String,
    teamsCount: Int?,
    modifier: Modifier = Modifier
) {
    val countLabel = teamsCount?.let { pluralStringResource(R.plurals.teams_count, it, it) }
    val secondary = MaterialTheme.colorScheme.onSurfaceVariant
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append(leagueName) }
            if (countLabel != null) {
                withStyle(SpanStyle(color = secondary)) { append(" · $countLabel") }
            }
        },
        style = MaterialTheme.typography.titleSmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

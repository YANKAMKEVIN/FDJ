package com.kev.fdjapptest.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueTopBar(
    lastUpdatedMillis: Long?,
    title: String = "Leagues & Teams",
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val lastUpdatedFormatter = remember(Locale.getDefault()) {
        SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
    }

    TopAppBar(
        modifier = modifier,
        navigationIcon = { navigationIcon?.invoke() },
        title = {
            Column {
                Text(title, style = MaterialTheme.typography.titleLarge)
                lastUpdatedMillis?.let { ts ->
                    Text(
                        text = "Last updated: ${lastUpdatedFormatter.format(Date(ts))}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        actions = actions
    )
}

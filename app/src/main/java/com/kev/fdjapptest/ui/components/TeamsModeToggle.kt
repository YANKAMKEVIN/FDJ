package com.kev.fdjapptest.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kev.fdjapptest.R
import com.kev.fdjapptest.ui.home.TeamsDisplayMode

@Composable
fun TeamsModeToggle(
    mode: TeamsDisplayMode,
    onChange: (TeamsDisplayMode) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = mode == TeamsDisplayMode.Grid,
            onClick = { onChange(TeamsDisplayMode.Grid) },
            label = { Text(stringResource(R.string.team_mode_toggle_grid)) },
            leadingIcon = { Icon(Icons.Default.GridView, contentDescription = null) }
        )
        FilterChip(
            selected = mode == TeamsDisplayMode.List,
            onClick = { onChange(TeamsDisplayMode.List) },
            label = { Text(stringResource(R.string.team_mode_toggle_list)) },
            leadingIcon = { Icon(Icons.Default.ViewAgenda, contentDescription = null) }
        )
    }
}

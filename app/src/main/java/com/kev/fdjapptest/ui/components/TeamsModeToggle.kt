package com.kev.fdjapptest.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kev.fdjapptest.R
import com.kev.fdjapptest.ui.home.TeamsDisplayMode

/** Compact Grid / List segmented button; the labels are read by TalkBack. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsModeToggle(
    mode: TeamsDisplayMode,
    onChange: (TeamsDisplayMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        Triple(TeamsDisplayMode.Grid, Icons.Default.GridView, R.string.team_mode_toggle_grid),
        Triple(TeamsDisplayMode.List, Icons.Default.ViewAgenda, R.string.team_mode_toggle_list)
    )
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, (option, icon, label) ->
            SegmentedButton(
                selected = mode == option,
                onClick = { onChange(option) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                icon = {},
                label = { ModeIcon(icon, stringResource(label)) }
            )
        }
    }
}

@Composable
private fun ModeIcon(icon: ImageVector, description: String) {
    Icon(icon, contentDescription = description, modifier = Modifier.size(18.dp))
}

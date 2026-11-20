package com.kev.fdjapptest.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import com.kev.domain.model.League
import com.kev.fdjapptest.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutocompleteField(
    query: String,
    suggestions: List<League>,
    isOpen: Boolean,
    showNoMatch: Boolean = false,
    onQueryChange: (String) -> Unit,
    onSelect: (League) -> Unit,
    onDismiss: () -> Unit,
    onClear: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var textFieldValue by remember { mutableStateOf(TextFieldValue(query)) }

    LaunchedEffect(query) {
        if (textFieldValue.text != query) {
            textFieldValue = TextFieldValue(
                text = query,
                selection = TextRange(query.length)
            )
        }
    }

    val expanded = isOpen && (suggestions.isNotEmpty() || showNoMatch)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {}
    ) {
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
                onQueryChange(it.text)
            },
            label = { Text(stringResource(R.string.autocomplete_hint)) },
            modifier = Modifier
                .menuAnchor(
                    type = MenuAnchorType.PrimaryEditable,
                    enabled = true
                )
                .fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                AnimatedVisibility(visible = query.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            onClear()
                            textFieldValue = TextFieldValue("")
                            keyboardController?.hide()
                            onDismiss()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.autocomplete_clear_description)
                        )
                    }
                }
            },
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss
        ) {
            if (suggestions.isEmpty() && showNoMatch) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.autocomplete_no_match, query.trim()),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.SearchOff, contentDescription = null) },
                    onClick = {},
                    enabled = false
                )
            }
            suggestions.forEach { league ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = highlightMatch(league.name, query),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            league.sport?.takeIf { it.isNotBlank() }?.let { sport ->
                                Text(
                                    text = sport,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = sportIcon(league.sport),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    onClick = {
                        onSelect(league)
                        keyboardController?.hide()
                    }
                )
            }
        }
    }
}

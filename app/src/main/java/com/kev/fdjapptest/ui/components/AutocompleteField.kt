package com.kev.fdjapptest.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import com.kev.domain.model.League

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutocompleteField(
    query: String,
    suggestions: List<League>,
    isOpen: Boolean,
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

    ExposedDropdownMenuBox(
        expanded = isOpen && suggestions.isNotEmpty(),
        onExpandedChange = {}
    ) {
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
                onQueryChange(it.text)
            },
            label = { Text("Search a league") },
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
                            contentDescription = "Clear text"
                        )
                    }
                }
            },
        )

        ExposedDropdownMenu(
            expanded = isOpen && suggestions.isNotEmpty(),
            onDismissRequest = onDismiss
        ) {
            suggestions.forEach { league ->
                DropdownMenuItem(
                    text = { Text(league.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    onClick = {
                        onSelect(league)
                        keyboardController?.hide()
                    }
                )
            }
        }
    }
}

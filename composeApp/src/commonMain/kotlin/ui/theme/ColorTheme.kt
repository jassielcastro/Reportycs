package ui.theme

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable

@Composable
fun GithubTextOutlinedColor(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
    focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
    errorBorderColor = MaterialTheme.colorScheme.error,
    focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimary,
    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimary,
    focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
    cursorColor = MaterialTheme.colorScheme.onPrimary,
    selectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.onPrimary,
        backgroundColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
    )
)

@Composable
fun GithubButtonOutlinedColor(): ButtonColors = ButtonDefaults.buttonColors(
    containerColor = MaterialTheme.colorScheme.primary,
    contentColor = MaterialTheme.colorScheme.onPrimary,
    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
)

@Composable
fun InverseGithubButtonOutlinedColor(): ButtonColors = ButtonDefaults.buttonColors(
    containerColor = MaterialTheme.colorScheme.primary,
    contentColor = MaterialTheme.colorScheme.onPrimary,
)

@Composable
fun InverseDeleteButtonOutlinedColor(): ButtonColors = ButtonDefaults.buttonColors(
    containerColor = MaterialTheme.colorScheme.primary,
    contentColor = MaterialTheme.colorScheme.onPrimary,
)

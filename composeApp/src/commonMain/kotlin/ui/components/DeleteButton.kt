package ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import ui.theme.InverseDeleteButtonOutlinedColor

@Composable
fun DeleteButton(
    icon: DrawableResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        shape = MaterialTheme.shapes.small,
        onClick = onClick,
        colors = InverseDeleteButtonOutlinedColor(),
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = "Delete button"
        )
    }
}
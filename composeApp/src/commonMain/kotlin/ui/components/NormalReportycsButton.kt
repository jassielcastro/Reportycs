package ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.theme.InverseGithubButtonOutlinedColor

@Composable
fun NormalReportycsButton(
    text: String,
    onClick: () -> Unit,
    color: ButtonColors = InverseGithubButtonOutlinedColor(),
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        shape = MaterialTheme.shapes.small,
        onClick = onClick,
        colors = color,
        modifier = modifier
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 16.dp)
        )
    }
}

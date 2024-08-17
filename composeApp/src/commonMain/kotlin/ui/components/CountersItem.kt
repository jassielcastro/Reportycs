package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun CountersItem(
    title: String,
    count: () -> String,
    icon: DrawableResource,
    modifier: Modifier = Modifier
) {

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    ) {

        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .padding(8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = count(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 80.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .fillMaxWidth()
                )
            }

            Icon(
                painter = painterResource(icon),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(end = 8.dp)
                    .size(54.dp)
                    .padding(4.dp)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(MaterialTheme.colorScheme.onPrimary)
            )
        }
    }
}

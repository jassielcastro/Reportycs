package ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jirareports.composeapp.generated.resources.Res
import jirareports.composeapp.generated.resources.ic_pull_request
import org.jetbrains.compose.resources.painterResource
import repository.model.PullRequestData

@Composable
fun PullRequestItem(
    modifier: Modifier = Modifier,
    pullRequestData: PullRequestData
) {
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("#${pullRequestData.id}")
        }
        append(" by ${pullRequestData.author}")
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            painterResource(Res.drawable.ic_pull_request),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .requiredSize(32.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = pullRequestData.title,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 8.dp, start = 16.dp)
            )

            Text(
                text = annotatedString,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .padding(top = 4.dp, start = 16.dp)
            )

            Divider(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            )
        }
    }
}

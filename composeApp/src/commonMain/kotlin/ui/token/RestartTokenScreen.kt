package ui.token

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jirareports.composeapp.generated.resources.Res
import jirareports.composeapp.generated.resources.refresh_token_screen_button
import jirareports.composeapp.generated.resources.refresh_token_screen_helper
import jirareports.composeapp.generated.resources.refresh_token_screen_placeholder
import jirareports.composeapp.generated.resources.refresh_token_screen_subtitle
import jirareports.composeapp.generated.resources.refresh_token_screen_title
import jirareports.composeapp.generated.resources.repository_error_field
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.rememberKoinInject
import ui.TokenViewModel
import ui.components.NormalReportycsButton
import ui.repositories.TextErrorHelper
import ui.repositories.TextPlaceHolder
import ui.theme.GithubTextOutlinedColor

@Composable
fun RestartTokenScreen(
    onSaved: () -> Unit
) {
    val viewModel = rememberKoinInject<TokenViewModel>()
    var tokenText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(Res.string.refresh_token_screen_title),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 4.dp)
                .fillMaxWidth()
        )

        TextPlaceHolder(
            text = stringResource(Res.string.refresh_token_screen_subtitle),
            modifier = Modifier
                .padding(bottom = 24.dp)
                .fillMaxWidth()
        )

        Text(
            text = stringResource(Res.string.refresh_token_screen_helper),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth()
        )

        OutlinedTextField(
            value = tokenText,
            onValueChange = {
                tokenText = it
            },
            placeholder = { TextPlaceHolder(stringResource(Res.string.refresh_token_screen_placeholder)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            ),
            singleLine = true,
            isError = tokenText.isEmpty(),
            supportingText = {
                if (tokenText.isEmpty()) {
                    TextErrorHelper(stringResource(Res.string.repository_error_field))
                }
            },
            shape = MaterialTheme.shapes.small,
            colors = GithubTextOutlinedColor(),
            modifier = Modifier
                .padding(bottom = 24.dp)
                .fillMaxWidth()
        )

        NormalReportycsButton(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(0.4f)
                .height(48.dp),
            text = stringResource(Res.string.refresh_token_screen_button),
            onClick = {
                viewModel.updateProjectToken(tokenText)
                onSaved()
            }
        )
    }
}

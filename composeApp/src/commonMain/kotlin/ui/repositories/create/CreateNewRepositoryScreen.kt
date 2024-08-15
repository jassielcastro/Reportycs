package ui.repositories.create

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jirareports.composeapp.generated.resources.Res
import jirareports.composeapp.generated.resources.add_button
import jirareports.composeapp.generated.resources.add_new_repository_form
import jirareports.composeapp.generated.resources.ic_analytics_bro
import jirareports.composeapp.generated.resources.repository_name
import jirareports.composeapp.generated.resources.repository_name_placeholder
import jirareports.composeapp.generated.resources.repository_owner
import jirareports.composeapp.generated.resources.repository_owner_placeholder
import jirareports.composeapp.generated.resources.repository_owners
import jirareports.composeapp.generated.resources.repository_owners_helper
import jirareports.composeapp.generated.resources.repository_owners_placeholder
import jirareports.composeapp.generated.resources.repository_token
import jirareports.composeapp.generated.resources.repository_token_placeholder
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.rememberKoinInject
import repository.model.RepositoryData
import ui.components.ReportycsButton
import ui.model.UiState
import ui.theme.GithubTextOutlinedColor

@Composable
fun CreateNewRepositoryScreen(
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = rememberKoinInject<CreateNewRepositoryViewModel>()
    val saveRepositoryState by viewModel.saveRepoState.collectAsState()

    LaunchedEffect(saveRepositoryState) {
        when (saveRepositoryState) {
            is UiState.Success -> onSuccess()
            else -> Unit
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
    ) {
        Surface(
            shape = ShapeDefaults.Large,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(0.8f)
        ) {
            Row {
                AddRepositoryForm(
                    isLoading = saveRepositoryState is UiState.Loading,
                    onClick = { repository, owners ->
                        viewModel.saveRepository(repository, owners)
                    }
                )

                Surface(
                    shape = ShapeDefaults.Medium,
                    shadowElevation = 1.dp,
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    Image(
                        painterResource(Res.drawable.ic_analytics_bro),
                        contentDescription = "",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun AddRepositoryForm(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    onClick: (RepositoryData, owners: String) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(0.5f)
            .fillMaxHeight()
            .padding(24.dp)
    ) {
        var ownerText by remember { mutableStateOf("") }
        var repositoryText by remember { mutableStateOf("") }
        var tokenText by remember { mutableStateOf("") }
        var ownersText by remember { mutableStateOf("") }

        Text(
            text = stringResource(Res.string.add_new_repository_form),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )

        Column(
            horizontalAlignment = Alignment.End,
            modifier = modifier
                .padding(top = 48.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(24.dp)
        ) {

            Text(
                text = stringResource(Res.string.repository_owner),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
            )

            OutlinedTextField(
                value = ownerText,
                onValueChange = { ownerText = it },
                placeholder = { TextPlaceHolder(stringResource(Res.string.repository_owner_placeholder)) },
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                ),
                singleLine = true,
                shape = MaterialTheme.shapes.small,
                colors = GithubTextOutlinedColor(),
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth()
            )

            Text(
                text = stringResource(Res.string.repository_name),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
            )

            OutlinedTextField(
                value = repositoryText,
                onValueChange = { repositoryText = it },
                placeholder = { TextPlaceHolder(stringResource(Res.string.repository_name_placeholder)) },
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                ),
                singleLine = true,
                shape = MaterialTheme.shapes.small,
                colors = GithubTextOutlinedColor(),
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth()
            )

            Text(
                text = stringResource(Res.string.repository_token),
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
                onValueChange = { tokenText = it },
                placeholder = { TextPlaceHolder(stringResource(Res.string.repository_token_placeholder)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                ),
                singleLine = true,
                shape = MaterialTheme.shapes.small,
                colors = GithubTextOutlinedColor(),
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth()
            )

            Text(
                text = stringResource(Res.string.repository_owners),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
            )

            OutlinedTextField(
                value = ownersText,
                onValueChange = { ownersText = it },
                placeholder = { TextPlaceHolder(stringResource(Res.string.repository_owners_placeholder)) },
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                ),
                singleLine = true,
                shape = MaterialTheme.shapes.small,
                colors = GithubTextOutlinedColor(),
                supportingText = {
                    Text(
                        text = stringResource(Res.string.repository_owners_helper),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Light,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    )
                },
                modifier = Modifier
                    .padding(bottom = 44.dp)
                    .fillMaxWidth()
            )

            ReportycsButton(
                text = stringResource(Res.string.add_button),
                isLoading = isLoading,
                onClick = {
                    if (!isLoading) {
                        onClick(
                            RepositoryData(
                                owner = ownerText,
                                repository = repositoryText,
                                token = tokenText,
                            ),
                            ownersText
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun TextPlaceHolder(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
        fontSize = 16.sp,
        fontWeight = FontWeight.Light,
        modifier = modifier
    )
}

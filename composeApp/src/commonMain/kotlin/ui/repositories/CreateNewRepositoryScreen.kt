package ui.repositories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jirareports.composeapp.generated.resources.Res
import jirareports.composeapp.generated.resources.add_button
import jirareports.composeapp.generated.resources.add_new_repository_form
import jirareports.composeapp.generated.resources.add_new_repository_form_helper
import jirareports.composeapp.generated.resources.repository_error_field
import jirareports.composeapp.generated.resources.repository_name
import jirareports.composeapp.generated.resources.repository_name_placeholder
import jirareports.composeapp.generated.resources.repository_owner
import jirareports.composeapp.generated.resources.repository_owner_placeholder
import jirareports.composeapp.generated.resources.repository_owners
import jirareports.composeapp.generated.resources.repository_owners_helper
import jirareports.composeapp.generated.resources.repository_owners_placeholder
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.rememberKoinInject
import ui.components.ReportycsButton
import ui.model.UiState
import ui.theme.GithubTextOutlinedColor
import usecase.model.RepositoryData

@Composable
fun CreateNewRepositoryScreen(
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize(0.6f),
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
            shape = MaterialTheme.shapes.large
        ) {

            Row(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                AddRepositoryForm(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(0.55f)
                        .fillMaxHeight(),
                    onSuccess = onSuccess
                )

                // TODO: add image
                /*Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium),
                )*/
            }
        }
    }
}

@Composable
fun AddRepositoryForm(
    modifier: Modifier = Modifier,
    onSuccess: () -> Unit
) {
    val viewModel = rememberKoinInject<CreateNewRepositoryViewModel>()
    val saveRepositoryState by viewModel.saveRepoState.collectAsState()

    LaunchedEffect(saveRepositoryState) {
        when (saveRepositoryState) {
            is UiState.Success -> onSuccess()
            else -> Unit
        }
    }

    var ownerText by remember { mutableStateOf("") }
    var repositoryText by remember { mutableStateOf("") }
    var ownersText by remember { mutableStateOf("") }

    val createStateError by viewModel.createState.collectAsState()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = stringResource(Res.string.add_new_repository_form),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 4.dp)
                .fillMaxWidth()
        )

        TextPlaceHolder(
            text = stringResource(Res.string.add_new_repository_form_helper),
            modifier = Modifier
                .padding(bottom = 24.dp)
                .fillMaxWidth()
        )

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
            onValueChange = {
                ownerText = it
                viewModel.updateOwnerErrorState(ownerText.isEmpty())
            },
            placeholder = { TextPlaceHolder(stringResource(Res.string.repository_owner_placeholder)) },
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            ),
            singleLine = true,
            isError = createStateError.hasOwnerError,
            supportingText = {
                if (createStateError.hasOwnerError) {
                    TextErrorHelper(stringResource(Res.string.repository_error_field))
                }
            },
            shape = MaterialTheme.shapes.small,
            colors = GithubTextOutlinedColor(),
            modifier = Modifier
                .padding(bottom = 24.dp)
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
            onValueChange = {
                repositoryText = it
                viewModel.updateNameErrorState(repositoryText.isEmpty())
            },
            placeholder = { TextPlaceHolder(stringResource(Res.string.repository_name_placeholder)) },
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            ),
            singleLine = true,
            isError = createStateError.hasNameError,
            supportingText = {
                if (createStateError.hasNameError) {
                    TextErrorHelper(stringResource(Res.string.repository_error_field))
                }
            },
            shape = MaterialTheme.shapes.small,
            colors = GithubTextOutlinedColor(),
            modifier = Modifier
                .padding(bottom = 24.dp)
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
            onValueChange = {
                ownersText = it
                viewModel.updateCodeOwnerErrorState(ownersText.isEmpty())
            },
            placeholder = { TextPlaceHolder(stringResource(Res.string.repository_owners_placeholder)) },
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            ),
            singleLine = true,
            isError = createStateError.hasCodeOwnersError,
            shape = MaterialTheme.shapes.small,
            colors = GithubTextOutlinedColor(),
            supportingText = {
                if (createStateError.hasCodeOwnersError) {
                    TextErrorHelper(stringResource(Res.string.repository_error_field))
                } else {
                    Text(
                        text = stringResource(Res.string.repository_owners_helper),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth()
                    )
                }
            },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        )

        ReportycsButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            text = stringResource(Res.string.add_button),
            isLoading = saveRepositoryState is UiState.Loading,
            onClick = {
                viewModel.saveRepository(
                    RepositoryData(
                        owner = ownerText,
                        repository = repositoryText,
                    ),
                    ownersText
                )
            }
        )
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

@Composable
fun TextErrorHelper(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.error,
        fontSize = 12.sp,
        fontWeight = FontWeight.Light,
        modifier = modifier
    )
}

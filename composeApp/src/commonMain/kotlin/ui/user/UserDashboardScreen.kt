package ui.user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import org.koin.compose.rememberKoinInject
import repository.UserRepository

@Composable
fun UserDashboardScreen() {
    val viewModel = rememberKoinInject<UserRepository>()

    LaunchedEffect(Unit) {
        viewModel.loadUserRepositories(
            token = "test",
            userName = "jassielcastro"
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    )
}
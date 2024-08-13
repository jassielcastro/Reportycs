package ui.repositories

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import repository.model.RepositoryData
import ui.components.FailureScreen

@Composable
fun RepositoriesScreen(
    modifier: Modifier,
    repositories: List<RepositoryData>
) {

    var repositorySelected by remember { mutableStateOf(repositories.firstOrNull()) }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {

        Row {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.2f)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                item {
                    Text(
                        text = "Dashboard",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(16.dp)
                    )
                }

                repositories.forEach { repo ->
                    item {
                        RepositoryItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            repository = repo,
                            isSelected = repo.id == repositorySelected?.id
                        ) { repo ->
                            if (repositorySelected?.id != repo.id) {
                                repositorySelected = repo
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                AnimatedContent(
                    targetState = repositorySelected,
                    transitionSpec = {
                        fadeIn() + slideInVertically(animationSpec = spring(
                            dampingRatio = 0.8f,
                            stiffness = Spring.StiffnessLow
                        ), initialOffsetY = { fullHeight -> fullHeight }) togetherWith
                                fadeOut(animationSpec = tween(200))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) { repo ->
                    if (repo != null) {
                        RepositoryScreen(
                            modifier = Modifier.fillMaxSize(),
                            repository = repo
                        )
                    } else {
                        FailureScreen(Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}

@Composable
fun RepositoryItem(
    modifier: Modifier,
    repository: RepositoryData,
    isSelected: Boolean,
    onRepositorySelected: (repo: RepositoryData) -> Unit
) {
    Surface(
        modifier = modifier
            .clickable { onRepositorySelected(repository) }
            .padding(16.dp),
        shape = ShapeDefaults.Medium,
        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(
                text = repository.owner,
                color = MaterialTheme.colorScheme.onTertiary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = repository.repository,
                color = MaterialTheme.colorScheme.onTertiary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
            )
        }
    }
}

package repository.model

import androidx.compose.runtime.Immutable

@Immutable
data class RepositoryData(
    val id: Int = 0,
    val owner: String,
    val repository: String,
    val token: String
)

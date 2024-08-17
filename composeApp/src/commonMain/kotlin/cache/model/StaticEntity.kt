package cache.model

data class StaticEntity(
    val id: Int,
    val repositoryId: Int,
    val title: String,
    val author: String,
    val avatar: String,
    val reviewCommentsCount: Int,
    val approves: List<String>,
)

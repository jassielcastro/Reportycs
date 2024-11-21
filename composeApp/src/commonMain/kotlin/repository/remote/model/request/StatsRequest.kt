package repository.remote.model.request

data class StatsRequest(
    val page: Int = 1,
    val perPage: Int = 25,
    val baseBranch: String = "develop",
    val state: String = "closed",
    val sort: String = "created",
    val direction: String = "desc",
)

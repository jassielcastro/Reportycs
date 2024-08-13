package ui.model

data class GithubStats(
    val prsCount: Int,
    val prsByContributor: Map<String, Int>,
    val commentsByPr: Int,
    val approvalsAndCommentsByContributor: Map<String, Pair<Int, Int>>,
    val statsByType: Map<String, Int>,
)

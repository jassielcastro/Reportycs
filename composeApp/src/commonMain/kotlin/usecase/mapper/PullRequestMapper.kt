package usecase.mapper

import repository.remote.model.response.PullRequestData
import repository.remote.model.response.ReviewedBy
import usecase.model.ApprovedBy
import usecase.model.CommentsBy
import usecase.model.PullRequestStaticsData

fun PullRequestData.toPullRequestStaticsData(): PullRequestStaticsData {
    return PullRequestStaticsData(
        title = this.title,
        number = this.number,
        author = this.author.login,
        createdAt = this.createdAt,
        mergedAt = this.mergedAt.orEmpty(),
        comments = this.reviews.nodes.toCommentsBy(),
        approves = this.reviews.nodes.toApprovedBy(),
    )
}

fun List<ReviewedBy>.toCommentsBy(): List<CommentsBy> {
    return this.filter {
        it.author.login != null
    }.filter {
        it.state == "CHANGES_REQUESTED"
    }.map { comment ->
        CommentsBy(
            author = comment.author.login.orEmpty()
        )
    }
}

fun List<ReviewedBy>.toApprovedBy(): List<ApprovedBy> {
    return this.filter {
        it.author.login != null
    }.filter {
        it.state == "APPROVED"
    }.map { comment ->
        ApprovedBy(
            author = comment.author.login.orEmpty()
        )
    }
}

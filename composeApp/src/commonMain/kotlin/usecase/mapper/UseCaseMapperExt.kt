package usecase.mapper

import repository.local.model.RepositoryRequest
import repository.model.PullRequestDto
import repository.model.RepositoryDto
import repository.model.TokenContributionDto
import repository.remote.model.response.PullRequestRemoteInfo
import usecase.model.PullRequestData
import usecase.model.RepositoryData
import usecase.model.TokenForContributionData

fun RepositoryDto.toRepositoryData(): RepositoryData {
    return RepositoryData(
        id = this.id,
        owner = this.owner,
        repository = this.repository,
    )
}

fun RepositoryData.toRepositoryDto(): RepositoryDto {
    return RepositoryDto(
        id = this.id,
        owner = this.owner,
        repository = this.repository,
    )
}

fun RepositoryData.toRepositoryRequest(): RepositoryRequest {
    return RepositoryRequest(
        id = this.id,
        owner = this.owner,
        repo = this.repository,
    )
}

fun PullRequestRemoteInfo.toPullRequestDto(repositoryId: Int): PullRequestDto {
    return PullRequestDto(
        id = this.number,
        repositoryId = repositoryId,
        title = this.title,
        author = this.author.login,
        avatar = this.author.avatarUrl,
    )
}

fun PullRequestDto.toPullRequestData(): PullRequestData {
    return PullRequestData(
        id = this.id,
        repositoryId = this.repositoryId,
        title = this.title,
        author = this.author,
        avatar = this.avatar,
    )
}

fun TokenContributionDto.toTokenData(): TokenForContributionData {
    return TokenForContributionData(
        id = this.id,
        name = this.name,
        token = this.token
    )
}

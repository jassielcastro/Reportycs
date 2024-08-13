package repository.mapper

import repository.model.PullRequestData
import repository.model.RepositoryData
import usecase.model.PullRequestDto
import usecase.model.RepositoryDto
import usecase.remote.model.request.RepositoryRequest
import usecase.remote.model.response.PullRequestResponse

fun RepositoryDto.toRepositoryData(): RepositoryData {
    return RepositoryData(
        id = this.id,
        owner = this.owner,
        repository = this.repository,
        token = this.token,
    )
}

fun RepositoryData.toRepositoryDto(): RepositoryDto {
    return RepositoryDto(
        id = this.id,
        owner = this.owner,
        repository = this.repository,
        token = this.token,
    )
}

fun RepositoryData.toRepositoryRequest(): RepositoryRequest {
    return RepositoryRequest(
        id = this.id,
        owner = this.owner,
        repo = this.repository,
        token = this.token,
    )
}

fun PullRequestResponse.toPullRequestDto(repositoryId: Int): PullRequestDto {
    return PullRequestDto(
        id = this.number,
        repositoryId = repositoryId,
        title = this.title,
        author = this.user.name,
        avatar = this.user.avatar,
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

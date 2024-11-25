package repository.mapper

import cache.model.OwnerEntity
import cache.model.PullRequestEntity
import cache.model.RepositoryEntity
import cache.model.StaticEntity
import repository.model.OwnerDto
import repository.model.PullRequestDto
import repository.model.RepositoryDto
import repository.model.StaticDto

fun RepositoryDto.toRepositoryEntity(): RepositoryEntity {
    return RepositoryEntity(
        id = this.id,
        owner = this.owner,
        repository = this.repository,
    )
}

fun RepositoryEntity.toRepositoryDto(): RepositoryDto {
    return RepositoryDto(
        id = this.id,
        owner = this.owner,
        repository = this.repository,
    )
}

fun OwnerEntity.toOwnerDto(): OwnerDto {
    return OwnerDto(
        idOwner = this.idOwner,
        user = this.user,
        repositoryId = this.repositoryId,
    )
}

fun OwnerDto.toOwnerEntity(): OwnerEntity {
    return OwnerEntity(
        idOwner = this.idOwner,
        user = this.user,
        repositoryId = this.repositoryId,
    )
}

fun PullRequestDto.toPullRequestEntity(): PullRequestEntity {
    return PullRequestEntity(
        id = this.id,
        repositoryId = this.repositoryId,
        title = this.title,
        author = this.author,
        avatar = this.avatar,
    )
}

fun PullRequestEntity.toPullRequestDto(): PullRequestDto {
    return PullRequestDto(
        id = this.id,
        repositoryId = this.repositoryId,
        title = this.title,
        author = this.author,
        avatar = this.avatar,
    )
}

fun StaticEntity.toStaticDto(): StaticDto {
    return StaticDto(
        id = this.id,
        repositoryId = this.repositoryId,
        title = this.title,
        author = this.author,
        avatar = this.avatar,
        reviewCommentsCount = this.reviewCommentsCount,
        approves = this.approves,
    )
}

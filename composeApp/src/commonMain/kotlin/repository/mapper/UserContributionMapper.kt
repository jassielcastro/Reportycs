package repository.mapper

import cache.model.TokenContributionEntity
import repository.model.TokenContributionDto

fun TokenContributionEntity.toDto(): TokenContributionDto {
    return TokenContributionDto(
        this.id,
        this.name,
        this.token
    )
}

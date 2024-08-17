package usecase.local

import cache.DataBase
import cache.model.ApproveEntity
import cache.model.PrCommentsEntity
import ext.now
import usecase.mapper.toOwnerDto
import usecase.mapper.toOwnerEntity
import usecase.mapper.toPullRequestDto
import usecase.mapper.toPullRequestEntity
import usecase.mapper.toRepositoryDto
import usecase.mapper.toRepositoryEntity
import usecase.mapper.toStaticDto
import usecase.model.OwnerDto
import usecase.model.PullRequestDto
import usecase.model.RepositoryDto
import usecase.model.StaticDto

class LocalPullRequestUseCase(
    private val dataBase: DataBase
) {

    /**
     * Repository CRUD
     */
    fun addNewRepository(repository: RepositoryDto) {
        dataBase.insertNewRepository(
            repository = repository.toRepositoryEntity()
        )
    }

    fun getRepositories(): List<RepositoryDto> {
        return dataBase.getAllRepositories().map { entity ->
            entity.toRepositoryDto()
        }
    }

    fun selectRepositoryBy(repositoryName: String): RepositoryDto? {
        return dataBase.selectRepositoryBy(repositoryName)?.toRepositoryDto()
    }

    fun removeRepository(repositoryId: Int) {
        dataBase.removeRepository(repositoryId)
    }

    /**
     * Owners CRUD
     */

    fun addRepositoryOwners(owners: List<OwnerDto>) {
        dataBase.addRepositoryOwners(
            owners = owners.map { owner -> owner.toOwnerEntity() }
        )
    }

    fun getRepositoryOwners(repositoryId: Int): List<OwnerDto> {
        return dataBase.getOwnersBy(repositoryId).map { owner ->
            owner.toOwnerDto()
        }
    }

    /**
     * Metrics CRUD
     */

    fun getPRSizeToAnalyse(repositoryId: Int): Int {
        return dataBase.getPullRequestSize(repositoryId)
    }

    fun updatePRSizeToAnalyze(repositoryId: Int, size: Int) {
        val actualSize = getPRSizeToAnalyse(repositoryId)
        if (actualSize == -1) {
            dataBase.insertMetrics(repositoryId, size)
        } else {
            dataBase.updateMetrics(repositoryId, size)
        }
    }

    /**
     * PullRequest CRUD
     */

    fun getPullRequest(repositoryId: Int): List<PullRequestDto> {
        return dataBase.getPullRequestList(repositoryId).map { pullRequest ->
            pullRequest.toPullRequestDto()
        }
    }

    fun addPullRequest(pullRequest: List<PullRequestDto>) {
        dataBase.insertPullRequest(
            pullRequest = pullRequest.map { pr -> pr.toPullRequestEntity() }
        )
    }

    /**
     * Approves CRUD
     */

    fun addApprovesByPR(pullRequestId: Int, approves: List<String>) {
        dataBase.insertApproves(
            approves = approves.map { approve ->
                ApproveEntity(
                    prId = pullRequestId,
                    user = approve
                )
            }
        )
    }

    fun hasApproves(pullRequestId: Int): Boolean = dataBase.hasApproves(pullRequestId)

    /**
     * Comments CRUD
     */
    fun addCommentsByPR(pullRequestId: Int, commentsCount: Int) {
        dataBase.insertPrComments(
            comment = PrCommentsEntity(
                prId = pullRequestId,
                reviewCommentsCount = commentsCount
            )
        )
    }

    fun hasComments(pullRequestId: Int): Boolean = dataBase.hasComments(pullRequestId)

    /**
     * Date CRUD
     */

    fun updateLastDateOfInsertions() {
        dataBase.updateLastDateOfInsertions()
    }

    fun hasPullRequestUpdated(): Boolean {
        val insertionDate = dataBase.getLastDateInsertion()
        return now() == insertionDate
    }

    /**
     * Get Pull Request information
     */

    fun getPullRequestInformation(repositoryId: Int): List<StaticDto> {
        return dataBase.getPullRequestInformation(repositoryId).map { entity ->
            entity.toStaticDto()
        }
    }
}

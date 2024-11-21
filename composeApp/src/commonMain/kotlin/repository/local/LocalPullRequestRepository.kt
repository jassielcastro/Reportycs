package repository.local

import cache.DataBase
import cache.model.ApproveEntity
import cache.model.PrCommentsEntity
import crypt.CryptoHandler
import ext.now
import repository.mapper.toOwnerDto
import repository.mapper.toOwnerEntity
import repository.mapper.toPullRequestDto
import repository.mapper.toPullRequestEntity
import repository.mapper.toRepositoryDto
import repository.mapper.toRepositoryEntity
import repository.mapper.toStaticDto
import repository.model.OwnerDto
import repository.model.PullRequestDto
import repository.model.RepositoryDto
import repository.model.StaticDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class LocalPullRequestRepository(
    private val dataBase: DataBase,
    private val cryptoHandler: CryptoHandler
) {

    /**
     * Repository CRUD
     */
    fun addNewRepository(repository: RepositoryDto) {
        val encryptedRepo = repository.copy(
            token = cryptoHandler.encrypt(repository.token)
        )
        dataBase.insertNewRepository(
            repository = encryptedRepo.toRepositoryEntity()
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

    fun updateRepositoryToken(repositoryId: Int, newToken: String) {
        dataBase.updateRepositoryToken(repositoryId, cryptoHandler.encrypt(newToken))
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

    fun clearPullRequest(repositoryId: Int) {
        dataBase.clearPullRequest(repositoryId)
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
        return now().toString() == insertionDate
    }

    fun needResetPullRequest(): Boolean {
        val insertionDate = dataBase.getLastDateInsertion() ?: return false
        val now = now().toString()
        val diff = daysBetweenDates(insertionDate, now) > 2
        return diff
    }

    private fun daysBetweenDates(date1: String, date2: String): Long {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val firstDate = LocalDate.parse(date1, formatter)
        val secondDate = LocalDate.parse(date2, formatter)
        return ChronoUnit.DAYS.between(firstDate, secondDate)
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

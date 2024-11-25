package cache

import app.cash.sqldelight.db.SqlDriver
import cache.model.ApproveEntity
import cache.model.OwnerEntity
import cache.model.PrCommentsEntity
import cache.model.PullRequestEntity
import cache.model.RepositoryEntity
import cache.model.StaticEntity
import cache.model.TokenContributionEntity
import com.ajcm.jira.cache.AppDatabaseQueries
import ext.now

class DataBase(
    driver: SqlDriver
) {
    private val dbQuery by lazy {
        AppDatabaseQueries(driver)
    }

    /**
     * Repository CRUD
     */
    fun insertNewRepository(repository: RepositoryEntity) {
        dbQuery.insertRepository(
            owner = repository.owner,
            repository = repository.repository,
        )
    }

    fun getAllRepositories(): List<RepositoryEntity> {
        return dbQuery.selectAllRepositories().executeAsList().map { repo ->
            RepositoryEntity(
                repo.id.toInt(),
                repo.owner,
                repo.repository,
            )
        }
    }

    fun selectRepositoryBy(repositoryName: String): RepositoryEntity? {
        val repository = dbQuery.selectRepository(repositoryName)
            .executeAsOneOrNull()

        return repository?.let { repo ->
            RepositoryEntity(
                repo.id.toInt(),
                repo.owner,
                repo.repository,
            )
        }
    }

    fun removeRepository(repositoryId: Int) {
        dbQuery.deleteRepository(repositoryId.toLong())
    }

    /**
     * Owners CRUD
     */

    fun addRepositoryOwners(owners: List<OwnerEntity>) {
        owners.forEach { owner ->
            dbQuery.insertOwners(
                repositoryId = owner.repositoryId.toLong(),
                user = owner.user
            )
        }
    }

    fun getOwnersBy(repositoryId: Int): List<OwnerEntity> {
        return dbQuery.selectAllOwners(
            repositoryId = repositoryId.toLong()
        ).executeAsList().map { owner ->
            OwnerEntity(
                idOwner = owner.idOwner.toInt(),
                user = owner.user,
                repositoryId = owner.repositoryId.toInt(),
            )
        }
    }

    /**
     * Metrics CRUD
     */

    fun insertMetrics(repositoryId: Int, pullRequestSize: Int) {
        dbQuery.insertMetrics(
            repositoryId = repositoryId.toLong(),
            pullRequestSize = pullRequestSize.toLong(),
        )
    }

    fun updateMetrics(repositoryId: Int, pullRequestSize: Int) {
        dbQuery.updateMetrics(
            repositoryId = repositoryId.toLong(),
            pullRequestSize = pullRequestSize.toLong(),
        )
    }

    fun getPullRequestSize(repositoryId: Int): Int {
        return dbQuery.selectAllMetrics(repositoryId.toLong())
            .executeAsOneOrNull()
            ?.pullRequestSize?.toInt()
            ?: -1
    }

    /**
     * PullRequest CRUD
     */

    fun insertPullRequest(pullRequest: List<PullRequestEntity>) {
        pullRequest.forEach { pr ->
            dbQuery.insertPullRequest(
                id = pr.id.toLong(),
                repositoryId = pr.repositoryId.toLong(),
                title = pr.title,
                author = pr.author,
                avatar = pr.avatar
            )
        }
    }

    fun getPullRequestList(repositoryId: Int): List<PullRequestEntity> {
        val limit = getPullRequestSize(repositoryId)
        return dbQuery.selectAllPullRequest(
            repositoryId = repositoryId.toLong(),
            limit.toLong()
        ).executeAsList().map { pullRequest ->
            PullRequestEntity(
                id = pullRequest.id.toInt(),
                repositoryId = pullRequest.repositoryId.toInt(),
                title = pullRequest.title,
                author = pullRequest.author,
                avatar = pullRequest.avatar,
            )
        }
    }

    fun clearPullRequest() {
        dbQuery.setDateOfInsertion(null)
        dbQuery.clearPullRequest()
    }

    /**
     * Approves CRUD
     */

    fun insertApproves(approves: List<ApproveEntity>) {
        approves.forEach { approver ->
            dbQuery.insertApproves(
                pr_id = approver.prId.toLong(),
                user = approver.user
            )
        }
    }

    fun hasApproves(pullRequestId: Int): Boolean {
        val count = dbQuery.selectApprovesCount(pullRequestId.toLong())
            .executeAsList()
        return count.isNotEmpty()
    }

    /**
     * Comments CRUD
     */

    fun insertPrComments(comment: PrCommentsEntity) {
        dbQuery.insertComments(
            pr_id = comment.prId.toLong(),
            reviewCommentsCount = comment.reviewCommentsCount.toLong()
        )
    }

    fun hasComments(pullRequestId: Int): Boolean {
        val count = dbQuery.selectCommentsCount(pullRequestId.toLong())
            .executeAsList()
        return count.isNotEmpty()
    }

    /**
     * Date CRUD
     */

    fun getLastDateInsertion(): String? {
        return dbQuery.selectLastInsertion { date ->
            date.orEmpty()
        }.executeAsList().lastOrNull()?.takeIf { it.isNotEmpty() }
    }

    fun updateLastDateOfInsertions() {
        dbQuery.setDateOfInsertion(now().toString())
    }

    /**
     * Statistics CRUD
     */

    fun getPullRequestInformation(repositoryId: Int): List<StaticEntity> {
        val limit = getPullRequestSize(repositoryId)
        return dbQuery.selectPullRequestStatistics(
            repositoryId.toLong(),
            limit.toLong()
        ).executeAsList()
            .map { pullRequest ->
                StaticEntity(
                    id = pullRequest.id.toInt(),
                    repositoryId = pullRequest.repositoryId.toInt(),
                    title = pullRequest.title,
                    author = pullRequest.author,
                    avatar = pullRequest.avatar,
                    reviewCommentsCount = pullRequest.comments?.toInt() ?: 0,
                    approves = pullRequest.approvers.split(",").map { it.trim() },
                )
            }
    }

    /**
     * Token contributions CRUD
     */

    fun addNewTokenForContributions(
        tokenName: String,
        token: String
    ) {
        dbQuery.insertTokenForContributions(
            name = tokenName,
            token = token
        )
    }

    fun getTokenContributionList(): List<TokenContributionEntity> {
        return dbQuery.selectAllTokenForContributions()
            .executeAsList()
            .map { token ->
                TokenContributionEntity(
                    id = token.id.toInt(),
                    name = token.name,
                    token = token.token
                )
            }
    }

    fun deleteTokenForContributions() {
        dbQuery.deleteTokenForContributions()
    }
}

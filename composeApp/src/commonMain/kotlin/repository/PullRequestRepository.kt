package repository

import ext.asyncMap
import repository.mapper.toPullRequestData
import repository.mapper.toPullRequestDto
import repository.mapper.toRepositoryData
import repository.mapper.toRepositoryDto
import repository.mapper.toRepositoryRequest
import repository.mapper.toStaticData
import repository.model.CodeOwnerData
import repository.model.PullRequestData
import repository.model.RepositoryData
import repository.model.StaticData
import usecase.local.LocalPullRequestUseCase
import usecase.model.OwnerDto
import usecase.remote.RemotePullRequestUseCase
import usecase.remote.model.request.StatsRequest

class PullRequestRepository(
    private val remoteUseCase: RemotePullRequestUseCase,
    private val localUseCase: LocalPullRequestUseCase
) {

    fun saveNewRepository(repository: RepositoryData) {
        localUseCase.addNewRepository(repository.toRepositoryDto())
    }

    fun getAllRepositories(): List<RepositoryData> {
        return localUseCase.getRepositories().map { repository ->
            repository.toRepositoryData()
        }
    }

    fun selectRepositoryBy(repositoryName: String): RepositoryData? {
        return localUseCase.selectRepositoryBy(repositoryName)?.toRepositoryData()
    }

    fun deleteRepository(repositoryId: Int) {
        localUseCase.removeRepository(repositoryId)
    }

    fun setRepositoryMetrics(repositoryId: Int, prsToAnalyze: Int) {
        localUseCase.updatePRSizeToAnalyze(
            repositoryId = repositoryId,
            size = prsToAnalyze
        )
    }

    fun addCodeOwners(codeOwners: List<String>, repository: RepositoryData) {
        localUseCase.addRepositoryOwners(
            owners = codeOwners.map { owner ->
                OwnerDto(
                    user = owner,
                    repositoryId = repository.id
                )
            }
        )
    }

    fun getCodeOwners(repository: RepositoryData): List<CodeOwnerData> {
        return localUseCase.getRepositoryOwners(repository.id).map { owner ->
            CodeOwnerData(owner.user)
        }
    }

    fun getPRsSizeToAnalyse(repositoryId: Int): Int {
        return localUseCase.getPRSizeToAnalyse(repositoryId = repositoryId)
    }

    suspend fun getPullRequest(
        repositoryData: RepositoryData,
        statRequest: StatsRequest,
        reload: Boolean = false
    ): List<PullRequestData> {
        val repositoryRequest = repositoryData.toRepositoryRequest()
        val minCount = getPRsSizeToAnalyse(repositoryId = repositoryRequest.id)
        val owners = getCodeOwners(repositoryData).map { it.name }
        if (!localUseCase.hasPullRequestUpdated() || reload) {
            val pullRequest = remoteUseCase.getPullRequest(repositoryRequest, statRequest).filter {
                owners.contains(it.user.name)
            }
            localUseCase.addPullRequest(
                pullRequest = pullRequest.map { pr ->
                    pr.toPullRequestDto(repositoryRequest.id)
                }
            )
        }
        val localPrs = localUseCase.getPullRequest(repositoryId = repositoryRequest.id).map { pr ->
            pr.toPullRequestData()
        }

        if (localPrs.size >= minCount) {
            localUseCase.updateLastDateOfInsertions()
        } else {
            getPullRequest(
                repositoryData = repositoryData,
                statRequest = statRequest.copy(page = statRequest.page + 1),
                reload = true
            )
        }

        return localPrs
    }

    suspend fun fetchPullRequestApproves(repositoryData: RepositoryData) {
        val repositoryRequest = repositoryData.toRepositoryRequest()
        val pullRequest = localUseCase.getPullRequest(repositoryRequest.id)
        pullRequest.asyncMap { pr ->
            val hasApproves = localUseCase.hasApproves(pr.id)
            if (!hasApproves) {
                val approves = remoteUseCase.getPullRequestApproves(repositoryRequest, pr.id)
                localUseCase.addApprovesByPR(
                    pullRequestId = pr.id,
                    approves = approves.map { approve -> approve.user.name }
                )
            }
        }
    }

    suspend fun fetchPullRequestComments(repositoryData: RepositoryData) {
        val repositoryRequest = repositoryData.toRepositoryRequest()
        val pullRequest = localUseCase.getPullRequest(repositoryRequest.id)
        pullRequest.asyncMap { pr ->
            val hasComments = localUseCase.hasComments(pr.id)
            if (!hasComments) {
                val info = remoteUseCase.getPullRequestInfo(repositoryRequest, pr.id)
                if (info.merged) {
                    localUseCase.addCommentsByPR(
                        pullRequestId = pr.id,
                        commentsCount = info.reviewComments
                    )
                }
            }
        }
    }

    fun getPullRequestInformation(repositoryId: Int): List<StaticData> {
        return localUseCase.getPullRequestInformation(repositoryId).map { dto ->
            dto.toStaticData()
        }
    }
}

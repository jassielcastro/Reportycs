package repository

import ext.asyncMap
import repository.mapper.toPullRequestData
import repository.mapper.toPullRequestDto
import repository.mapper.toRepositoryData
import repository.mapper.toRepositoryDto
import repository.mapper.toRepositoryRequest
import repository.model.CodeOwnerData
import repository.model.PullRequestData
import repository.model.RepositoryData
import usecase.local.LocalPullRequestUseCase
import usecase.model.OwnerDto
import usecase.remote.RemotePullRequestUseCase
import usecase.remote.model.request.RepositoryRequest
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

    suspend fun getCodeOwners(repository: RepositoryData): List<CodeOwnerData> {
        val codeOwners = localUseCase.getRepositoryOwners(repository.id)
        if (codeOwners.isEmpty()) {
            val remoteOwners = remoteUseCase.getCodeOwners(
                repository.toRepositoryRequest()
            )
            localUseCase.addRepositoryOwners(
                owners = remoteOwners.map { owner ->
                    OwnerDto(
                        user = owner.name,
                        repositoryId = repository.id
                    )
                }
            )
        }

        return localUseCase.getRepositoryOwners(repository.id).map { owner ->
            CodeOwnerData(owner.user)
        }
    }

    suspend fun getPullRequest(
        repositoryRequest: RepositoryRequest,
        statRequest: StatsRequest,
        reload: Boolean = false
    ): List<PullRequestData> {
        val minCount = localUseCase.getPRSizeToAnalyse(repositoryId = repositoryRequest.id)
        if (!localUseCase.hasPullRequestUpdated() || reload) {
            val pullRequest = remoteUseCase.getPullRequest(repositoryRequest, statRequest)
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
                repositoryRequest = repositoryRequest,
                statRequest = statRequest.copy(page = statRequest.page + 1),
                reload = true
            )
        }

        return localPrs
    }

    suspend fun fetchPullRequestApproves(repositoryRequest: RepositoryRequest) {
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

    suspend fun fetchPullRequestComments(repositoryRequest: RepositoryRequest) {
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
}

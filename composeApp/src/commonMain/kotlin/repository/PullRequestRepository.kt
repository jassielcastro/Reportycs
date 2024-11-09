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
import usecase.model.ErrorStatus
import usecase.model.OwnerDto
import usecase.model.ResponseStatus
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

    fun updateRepositoryToken(repositoryId: Int, newToken: String) {
        localUseCase.updateRepositoryToken(repositoryId, newToken)
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
    ): ResponseStatus<List<PullRequestData>> {
        val repositoryRequest = repositoryData.toRepositoryRequest()
        val minCount = getPRsSizeToAnalyse(repositoryId = repositoryRequest.id)
        val owners = getCodeOwners(repositoryData).map { it.name }

        var forceReload = true

        fun loadSavedPullRequest() =
            localUseCase.getPullRequest(repositoryId = repositoryRequest.id).map { pr ->
                pr.toPullRequestData()
            }

        if (!localUseCase.hasPullRequestUpdated() || reload) {
            val remotePullRequest = remoteUseCase.getPullRequest(repositoryRequest, statRequest)

            when (remotePullRequest) {
                is ResponseStatus.Error -> {
                    when (remotePullRequest.status) {
                        ErrorStatus.EMPTY -> forceReload = false
                        else -> {
                            return ResponseStatus.Error(remotePullRequest.status)
                        }
                    }
                }

                is ResponseStatus.Success -> {
                    val pullRequest = remotePullRequest.response.filter {
                        owners.contains(it.user.name)
                    }

                    if (pullRequest.isNotEmpty()) {
                        localUseCase.addPullRequest(
                            pullRequest = pullRequest.map { pr ->
                                pr.toPullRequestDto(repositoryRequest.id)
                            }
                        )
                    } else {
                        forceReload = false
                    }
                }
            }
        }

        val localPrs = loadSavedPullRequest()

        if (localPrs.size >= minCount || !forceReload) {
            localUseCase.updateLastDateOfInsertions()
        } else {
            getPullRequest(
                repositoryData = repositoryData,
                statRequest = statRequest.copy(page = statRequest.page + 1),
                reload = true
            )
        }

        if (localPrs.isEmpty()) {
            return ResponseStatus.Error(ErrorStatus.EMPTY)
        }

        return ResponseStatus.Success(localPrs)
    }

    suspend fun fetchPullRequestApproves(repositoryData: RepositoryData) {
        val repositoryRequest = repositoryData.toRepositoryRequest()
        val pullRequest = localUseCase.getPullRequest(repositoryRequest.id)
        pullRequest.asyncMap { pr ->
            val hasApproves = localUseCase.hasApproves(pr.id)
            if (!hasApproves) {
                val approves = remoteUseCase.getPullRequestApproves(repositoryRequest, pr.id)
                when (approves) {
                    is ResponseStatus.Success -> {
                        localUseCase.addApprovesByPR(
                            pullRequestId = pr.id,
                            approves = approves.response.map { approve -> approve.user.name }
                        )
                    }

                    else -> Unit
                }
            }
        }
    }

    suspend fun fetchPullRequestComments(repositoryData: RepositoryData) {
        val repositoryRequest = repositoryData.toRepositoryRequest()
        val pullRequest = localUseCase.getPullRequest(repositoryRequest.id)
        pullRequest.asyncMap { pr ->
            val hasComments = localUseCase.hasComments(pr.id)
            if (!hasComments) {
                when (val info = remoteUseCase.getPullRequestInfo(repositoryRequest, pr.id)) {
                    is ResponseStatus.Success -> {
                        if (info.response.merged) {
                            localUseCase.addCommentsByPR(
                                pullRequestId = pr.id,
                                commentsCount = info.response.reviewComments
                            )
                        }
                    }

                    else -> Unit
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

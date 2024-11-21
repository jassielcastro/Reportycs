package usecase

import ext.asyncMap
import usecase.mapper.toPullRequestData
import usecase.mapper.toPullRequestDto
import usecase.mapper.toRepositoryData
import usecase.mapper.toRepositoryDto
import usecase.mapper.toRepositoryRequest
import usecase.mapper.toStaticData
import usecase.model.CodeOwnerData
import usecase.model.PullRequestData
import usecase.model.RepositoryData
import usecase.model.StaticData
import repository.local.LocalPullRequestRepository
import repository.model.ErrorStatus
import repository.model.OwnerDto
import repository.model.ResponseStatus
import repository.remote.RemotePullRequestRepository
import repository.remote.model.request.StatsRequest

class PullRequestUseCase(
    private val remoteUseCase: RemotePullRequestRepository,
    private val localUseCase: LocalPullRequestRepository
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

    fun clearRepositoryPullRequest(repositoryId: Int) {
        localUseCase.clearPullRequest(repositoryId)
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
        if (localUseCase.needResetPullRequest()) {
            localUseCase.clearPullRequest(repositoryData.id)
        }

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

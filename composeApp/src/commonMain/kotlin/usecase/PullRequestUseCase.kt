package usecase

import repository.local.LocalPullRequestRepository
import repository.model.ErrorStatus
import repository.model.OwnerDto
import repository.model.ResponseStatus
import repository.remote.RemotePullRequestRepository
import usecase.mapper.toPullRequestData
import usecase.mapper.toPullRequestDto
import usecase.mapper.toPullRequestStaticsData
import usecase.mapper.toRepositoryData
import usecase.mapper.toRepositoryDto
import usecase.mapper.toRepositoryRequest
import usecase.model.CodeOwnerData
import usecase.model.PullRequestData
import usecase.model.PullRequestStaticsData
import usecase.model.RepositoryData

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

    fun clearRepositoryPullRequest() {
        localUseCase.clearPullRequest()
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
    ): ResponseStatus<List<PullRequestData>> {
        if (localUseCase.needResetPullRequest()) {
            localUseCase.clearPullRequest()
        }

        val repositoryRequest = repositoryData.toRepositoryRequest()
        val owners = getCodeOwners(repositoryData).map { it.name }

        if (!localUseCase.hasPullRequestUpdated()) {
            when (val remotePullRequest = remoteUseCase.getPullRequest(repositoryRequest)) {
                is ResponseStatus.Error -> {
                    when (remotePullRequest.status) {
                        ErrorStatus.EMPTY -> Unit
                        else -> {
                            return ResponseStatus.Error(remotePullRequest.status)
                        }
                    }
                }

                is ResponseStatus.Success -> {
                    val pullRequest = remotePullRequest
                        .response
                        .data
                        .repository
                        .pullRequests
                        .nodes
                        .filter { pr ->
                            pr.mergedAt != null
                        }
                        .filter { pr ->
                            owners.contains(pr.author.login)
                        }

                    if (pullRequest.isNotEmpty()) {
                        localUseCase.updateLastDateOfInsertions()
                        localUseCase.addPullRequest(
                            pullRequest = pullRequest.map { pr ->
                                pr.toPullRequestDto(repositoryRequest.id)
                            }
                        )
                    }
                }
            }
        }

        val localPrs = loadSavedPullRequest(repositoryRequest.id)

        if (localPrs.isEmpty()) {
            return ResponseStatus.Error(ErrorStatus.EMPTY)
        }

        return ResponseStatus.Success(localPrs)
    }

    private fun loadSavedPullRequest(repositoryId: Int) =
        localUseCase.getPullRequest(repositoryId = repositoryId).map { pr ->
            pr.toPullRequestData()
        }

    suspend fun getPullRequestInfo(repositoryData: RepositoryData): List<PullRequestStaticsData> {
        val repositoryRequest = repositoryData.toRepositoryRequest()
        return when (val response = remoteUseCase.getPullRequestInfo(repositoryRequest)) {
            is ResponseStatus.Success -> {
                response
                    .response
                    .data
                    .repository
                    .pullRequests
                    .nodes
                    .filter { pr ->
                        pr.mergedAt != null
                    }.map {
                        it.toPullRequestStaticsData()
                    }
            }

            else -> emptyList()
        }
    }
}

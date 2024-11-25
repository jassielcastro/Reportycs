package ui

import androidx.lifecycle.ViewModel
import usecase.PullRequestUseCase
import usecase.TokenUseCase
import usecase.model.TokenForContributionData
import java.util.UUID

class TokenViewModel(
    private val pullRequestUseCase: PullRequestUseCase,
    private val userContributionUseCase: TokenUseCase
) : ViewModel() {
    fun getProjectToken(): TokenForContributionData? {
        return userContributionUseCase.getAllTokensForContributions().firstOrNull()
    }

    fun updateProjectToken(newToken: String) {
        pullRequestUseCase.clearRepositoryPullRequest()
        userContributionUseCase.saveTokenToHandleContributions(
            token = newToken,
            tokenName = UUID.randomUUID().toString()
        )
    }
}

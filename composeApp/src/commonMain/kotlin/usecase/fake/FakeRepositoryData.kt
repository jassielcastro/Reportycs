package usecase.fake

import usecase.model.CodeOwnerData
import usecase.model.PullRequestData
import usecase.model.RepositoryData
import usecase.model.StaticData
import kotlin.random.Random

object FakeRepositoryData {

    fun buildFakeRepositories(): List<RepositoryData> = listOf(
        RepositoryData(
            id = 1,
            owner = "jassielcastro",
            repository = "Reportycs",
            token = "",
        ),
        RepositoryData(
            id = 2,
            owner = "fake-repository",
            repository = "other-repo",
            token = "",
        )
    )

    fun buildFakeCodeOwners(): List<CodeOwnerData> = listOf(
        CodeOwnerData(
            name = "jassielcastro"
        ),
        CodeOwnerData(
            name = "git_trailblazer"
        ),
        CodeOwnerData(
            name = "terminal_wizard"
        ),
        CodeOwnerData(
            name = "mark_zucaritas"
        ),
        CodeOwnerData(
            name = "patch_pilot"
        ),
        CodeOwnerData(
            name = "code_voyager"
        ),
        CodeOwnerData(
            name = "debug_guru"
        ),
        CodeOwnerData(
            name = "pixel_crafter"
        ),
        CodeOwnerData(
            name = "bit_ninja"
        ),
        CodeOwnerData(
            name = "syntax_sorcerer"
        ),
        CodeOwnerData(
            name = "quantum_coder"
        ),
        CodeOwnerData(
            name = "repo_rider"
        ),
        CodeOwnerData(
            name = "script_savvy"
        ),
        CodeOwnerData(
            name = "byte_hacker"
        ),
        CodeOwnerData(
            name = "dev_forge_master"
        ),
        CodeOwnerData(
            name = "commit_crusader"
        ),
        CodeOwnerData(
            name = "build_bot_maker"
        ),
    )

    fun buildFakePullRequest(): List<PullRequestData> {
        val types = listOf("FEAT", "BUG", "DOCS", "STYLE", "REFACTOR", "PERF", "TEST", "CHORE")

        val descriptions = listOf(
            "Add new feature",
            "Fix critical bug",
            "Update documentation",
            "Refactor codebase",
            "Improve performance",
            "Write unit tests",
            "Change Theming colors",
            "Adjust styles",
            "Clean up tasks",
            "Add Api documentation",
            "Miscellaneous updates",
            "Simplify request permission location",
            "Remove unused code or imports",
            "Add CustomCardsViewModel testing",
            "Update changelog",
            "Add user authentication",
            "Fix login bug",
            "Performance improvements",
            "Correcting tests",
            "Improve CI/CD",
            "Add more logs to db",
            "Fix color for Universal View",
            "Change typo in Login screen",
            "Remove unused code",
            "Remove unused Resources",
            "Remove unused imports",
            "Remove unused strings",
        )

        return (1..100).map { id ->
            val type = types.random()
            val rtNumber = Random.nextInt(100, 1000)
            val description = descriptions.random()
            val title = "[$type][RT-$rtNumber]: $description"
            val author = buildFakeCodeOwners().random()
            val repositoryId = Random.nextInt(1, 2)

            PullRequestData(
                id = id,
                repositoryId = repositoryId,
                title = title,
                author = author.name,
                avatar = ""
            )
        }
    }

    fun buildFakeStatics(): List<StaticData> {
        return buildFakePullRequest().map { pr ->
            val reviewers = buildFakeCodeOwners()
                .shuffled()
                .take(Random.nextInt(0, 5))
                .map { it.name }

            StaticData(
                id = pr.id,
                repositoryId = pr.repositoryId,
                title = pr.title,
                author = pr.author,
                avatar = pr.avatar,
                reviewCommentsCount = Random.nextInt(0, 5),
                approves = reviewers,
            )
        }
    }
}

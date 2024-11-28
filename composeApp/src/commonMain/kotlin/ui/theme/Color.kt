package ui.theme

import androidx.compose.ui.graphics.Color

val primaryLight = Color(0xFF24292E)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFD6E3FF)
val onPrimaryContainerLight = Color(0xFF001B3E)
val secondaryLight = Color(0xFF31363b)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFDAE2F9)
val onSecondaryContainerLight = Color(0xFF131C2B)
val tertiaryLight = Color(0x0366D6)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFFAD8FD)
val onTertiaryContainerLight = Color(0xFF28132E)
val errorLight = Color(0xFFdf6251)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF410002)
val backgroundLight = Color(0xFF24292E)
val onBackgroundLight = Color(0xFF191C20)
val surfaceLight = Color(0x161B22)
val onSurfaceLight = Color(0xFF191C20)
val surfaceVariantLight = Color(0xFFE0E2EC)
val onSurfaceVariantLight = Color(0xFF44474E)
val outlineLight = Color(0xFF74777F)
val outlineVariantLight = Color(0xFFC4C6D0)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF2E3036)
val inverseOnSurfaceLight = Color(0xFFF0F0F7)
val inversePrimaryLight = Color(0xFFAAC7FF)
val surfaceDimLight = Color(0xFFD9D9E0)
val surfaceBrightLight = Color(0xFFF9F9FF)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFF3F3FA)
val surfaceContainerLight = Color(0xFFEDEDF4)
val surfaceContainerHighLight = Color(0xFFE7E8EE)
val surfaceContainerHighestLight = Color(0xFFE2E2E9)

val dashboardColor = Color(0xff9395D3)
val userSearchDotsColor = Color(0xff9395D3)
val userSearchDotsColor2 = Color(0xff455561)

val chartBarsColor = Color(0xff9395D3)
val chartBarsColor2 = Color(0xfff56565)
val chartBarsColor3 = Color(0xffBA7BA1)

val githubContributionColor1 = Color(0xff9395D3)
val githubContributionColor2 = githubContributionColor1.copy(alpha = 0.75f)
val githubContributionColor3 = githubContributionColor1.copy(alpha = 0.45f)
val githubContributionColor4 = githubContributionColor1.copy(alpha = 0.25f)
val githubContributionColor5 = githubContributionColor1.copy(alpha = 0.05f)

val githubContributionColorList = listOf(
    githubContributionColor5,
    githubContributionColor4,
    githubContributionColor3,
    githubContributionColor2,
    githubContributionColor1,
)

val pieChartColors: List<Color> by lazy {
    listOf(
        Color(0xFFdf6251),
        Color(0xff313E50),
        Color(0xff3A435E),
        Color(0xff455561),
        Color(0xFF5C6672),
        Color(0xFF6C6F7F),
        Color(0xff9395D3),
        Color(0xfff56565),
        Color(0xffBA7BA1),
    )
}

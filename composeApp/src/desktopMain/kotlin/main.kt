import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ui.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "reportycs",
        state = rememberWindowState(WindowPlacement.Maximized),
    ) {
        App()
    }
}

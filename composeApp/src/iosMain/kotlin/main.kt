import androidx.compose.ui.window.ComposeUIViewController
import org.adhan.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }

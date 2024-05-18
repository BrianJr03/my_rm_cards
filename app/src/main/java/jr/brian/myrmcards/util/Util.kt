package jr.brian.myrmcards.util

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import jr.brian.myrmcards.ui.theme.customBlue
import jr.brian.myrmcards.ui.theme.customGreen
import jr.brian.myrmcards.ui.theme.customPink
import jr.brian.myrmcards.ui.theme.customRed
import jr.brian.myrmcards.ui.theme.customYellow
import java.text.SimpleDateFormat
import java.util.Locale

private val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
private val outputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)

@Composable
fun getCharacterScreenColors(): Pair<Color, Color> {
    return if (isSystemInDarkTheme())
        Pair(customGreen, customYellow)
    else
        Pair(customBlue, customRed)
}

fun String.formatDate(): String {
    return inputFormat.parse(this)?.let {
        outputFormat.format(it)
    } ?: this
}

fun String.getStatusColor(): Color {
    return when (lowercase()) {
        "alive" -> Color.Green
        "dead" -> Color.Red
        else -> Color.LightGray
    }
}

fun Context.showShortToast(msg: String) {
    Toast.makeText(
        this,
        msg,
        Toast.LENGTH_SHORT
    ).show()
}

@Suppress("Unused")
fun Context.showLongToast(msg: String) {
    Toast.makeText(
        this,
        msg,
        Toast.LENGTH_LONG
    ).show()
}

@Composable
fun annotatedString(
    coloredText: String,
    regularText: String,
    coloredTextColor: Color = customPink,
    regularTextColor: Color = MaterialTheme.colorScheme.onSurface
): AnnotatedString {
    return buildAnnotatedString {
        withStyle(style = SpanStyle(color = coloredTextColor)) {
            append(coloredText)
        }
        withStyle(style = SpanStyle(color = regularTextColor)) {
            append(" $regularText")
        }
    }
}
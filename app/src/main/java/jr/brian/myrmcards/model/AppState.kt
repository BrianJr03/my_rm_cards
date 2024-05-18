package jr.brian.myrmcards.model

sealed class AppState<out T> {
    data object Loading : AppState<Nothing>()
    data object Idle : AppState<Nothing>()
    data class Error<out T>(val data: String) : AppState<T>()
    data class Success<out T>(val data: T) : AppState<T>()
}
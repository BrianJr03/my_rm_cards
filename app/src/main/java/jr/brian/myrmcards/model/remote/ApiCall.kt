package jr.brian.myrmcards.model.remote

import jr.brian.myrmcards.model.AppState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

fun <T> callApi(
    call: suspend () -> Response<T>
): Flow<AppState<T?>> = flow {
    emit(AppState.Loading)
    try {
        val response = call()
        response.let {
            if (it.isSuccessful) {
                if (it.body() == null) {
                    emit(AppState.Error(it.errorBody()?.string().toString()))
                } else {
                    emit(AppState.Success(it.body()))
                }
            } else {
                emit(AppState.Error(it.errorBody()?.string().toString()))
            }
        }
    } catch (e: Exception) {
        emit(AppState.Error("${e.message}"))
    }
}
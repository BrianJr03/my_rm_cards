package jr.brian.myrmcards.view.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import jr.brian.myrmcards.model.AppState
import jr.brian.myrmcards.model.local.Character
import jr.brian.myrmcards.model.local.database.CharacterDao
import jr.brian.myrmcards.util.showShortToast
import jr.brian.myrmcards.viewmodel.MainViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    dao: CharacterDao,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onFinish: () -> Unit
) {
    val characters = dao.getCharacters().collectAsState(initial = emptyList())
    val charactersFromSearch = remember { mutableStateOf<List<Character>>(emptyList()) }
    val selectedCharacter = remember { mutableStateOf(Character.EMPTY) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    val text = remember { mutableStateOf("") }
    val errorMsg = remember { mutableStateOf("") }

    val isLoading = remember { mutableStateOf(false) }
    val isError = remember { mutableStateOf(false) }
    val isShowingCharacterDialog = remember { mutableStateOf(false) }
    val isSavedCharacter = remember { mutableStateOf(false) }
    val isConfirmationRowShowing = remember { mutableStateOf(false) }
    val canShowConfirmationROw = isConfirmationRowShowing.value && characters.value.isNotEmpty()

    val appState = viewModel.state.collectAsState()
    val gridState= rememberLazyStaggeredGridState()

    val backPressTime = remember { mutableLongStateOf(0L) }
    val backPressJob = remember { mutableStateOf<Job?>(null) }

    val handleBackPress = {
        focusManager.clearFocus()
        isConfirmationRowShowing.value = false
        scope.launch {
            val currentTime = System.currentTimeMillis()
            if (currentTime - backPressTime.longValue <= 2000) {
                onFinish()
            } else {
                gridState.animateScrollToItem(0)
                backPressTime.longValue = currentTime
                context.showShortToast("One more time to exit")
                backPressJob.value?.cancel()
                backPressJob.value = launch {
                    delay(3000)
                }
            }
        }
    }

    val searchOnClick = {
        focusManager.clearFocus()
        if (!isLoading.value) {
            charactersFromSearch.value = emptyList()
            if (text.value.isNotBlank()) {
                scope.launch {
                    viewModel.getCharactersByName(text.value)
                }
            }
        }
    }

    BackHandler {
        handleBackPress()
    }

    when (val currentState = appState.value) {
        is AppState.Success -> {
            isLoading.value = false
            isError.value = false
            currentState.data?.let {
                it.results.onEach { character ->
                    dao.insertCharacter(character)
                }
                charactersFromSearch.value = it.results
            }
        }

        is AppState.Error -> {
            isLoading.value = false
            isError.value = true
            errorMsg.value = currentState.data
        }

        is AppState.Loading -> {
            isError.value = false
            isLoading.value = true
        }

        is AppState.Idle -> {
            isLoading.value = false
        }
    }
}
package jr.brian.myrmcards.util

import androidx.compose.animation.core.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

private enum class State { PLACING, PLACED }

private data class ScaleAndAlphaArgs(
    val fromScale: Float,
    val toScale: Float,
    val fromAlpha: Float,
    val toAlpha: Float
)

@Composable
private fun scaleAndAlpha(
    args: ScaleAndAlphaArgs,
    animation: FiniteAnimationSpec<Float>
): Pair<Float, Float> {
    val transitionState =
        remember { MutableTransitionState(State.PLACING).apply { targetState = State.PLACED } }
    val transition = rememberTransition(transitionState, label = "transition")
    val alpha by transition.animateFloat(transitionSpec = { animation }, label = "alpha") { state ->
        when (state) {
            State.PLACING -> args.fromAlpha
            State.PLACED -> args.toAlpha
        }
    }
    val scale by transition.animateFloat(transitionSpec = { animation }, label = "scale") { state ->
        when (state) {
            State.PLACING -> args.fromScale
            State.PLACED -> args.toScale
        }
    }
    return alpha to scale
}

@Composable
private fun LazyStaggeredGridState.calculateDelayAndEasing(
    index: Int,
    columnCount: Int
): Pair<Int, Easing> {
    val row = index / columnCount
    val column = index % columnCount
    val firstVisibleRow = remember { derivedStateOf { firstVisibleItemIndex } }
    val visibleRows = layoutInfo.visibleItemsInfo.count()
    val scrollingToBottom = firstVisibleRow.value < row
    val isFirstLoad = visibleRows == 0
    val rowDelay = 150 * when {
        isFirstLoad -> row // initial load
        scrollingToBottom -> visibleRows + firstVisibleRow.value - row // scrolling to bottom
        else -> 1 // scrolling to top
    }
    val scrollDirectionMultiplier = if (scrollingToBottom || isFirstLoad) 1 else -1
    val columnDelay = column * 100 * scrollDirectionMultiplier
    val easing = if (scrollingToBottom || isFirstLoad)
        LinearOutSlowInEasing else FastOutSlowInEasing
    return rowDelay + columnDelay to easing
}

@Composable
fun getScaleAndAlpha(
    index: Int,
    columnCount: Int = 2,
    gridState: LazyStaggeredGridState
): Pair<Float, Float> {
    val (delay, easing) = gridState.calculateDelayAndEasing(index, columnCount)
    val animation =
        tween<Float>(durationMillis = 100, delayMillis = delay, easing = easing)
    val args =
        ScaleAndAlphaArgs(fromScale = 2f, toScale = 1f, fromAlpha = 0f, toAlpha = 1f)
    return scaleAndAlpha(args = args, animation = animation)
}
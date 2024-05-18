package jr.brian.myrmcards.model.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import jr.brian.myrmcards.model.AppState
import jr.brian.myrmcards.model.Repository
import jr.brian.myrmcards.model.local.database.CharacterDao
import jr.brian.myrmcards.view.MainActivity
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.random.Random

@HiltWorker
class ApiWorker @AssistedInject constructor(
    private val repository: Repository,
    @Assisted val context: Context,
    @Assisted workerParameters: WorkerParameters
) :
    CoroutineWorker(context, workerParameters) {
    var dao: CharacterDao? = null
        @Inject set

    private val pageNumber = Random.nextInt(1, 43).toString()

    private fun isInternetConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override suspend fun doWork(): Result {
        if (isInternetConnected()) {
            repository.getAllCharacters(pageNumber = pageNumber).collect {
                when (val currentState = it) {
                    is AppState.Success -> {
                        currentState.data?.let { characterResult ->
                            dao?.let { dao ->
                                characterResult.results.onEach { character ->
                                    dao.insertCharacter(character)
                                }
                            }
                        }
                    }

                    is AppState.Error -> {
                        currentState.data.let {
                            Log.i("myTag", "Failed")
                        }
                    }

                    is AppState.Loading -> {
                        Log.i("myTag", "Loading")
                    }

                    is AppState.Idle -> {
                        Log.i("myTag", "Idle")
                    }
                }
            }
            return Result.Success.success()
        } else {
            return Result.retry()
        }
    }

    companion object {
        private const val TAG = "API Worker Tag"
        private lateinit var workRequest: PeriodicWorkRequest

        fun fetchDataInBackground(
            context: Context,
            onStateChange: (msg: String) -> Unit
        ) {
            if (context !is MainActivity) return

            workRequest = PeriodicWorkRequest.Builder(
                ApiWorker::class.java,
                15,
                TimeUnit.MINUTES
            )
                .setConstraints(
                    Constraints.Builder().apply {
                        setRequiredNetworkType(NetworkType.CONNECTED)
                        setRequiresBatteryNotLow(true)
                    }.build()
                )
                .build()

            val workManager = WorkManager.getInstance(context)

            workManager.enqueue(workRequest)

            workManager
                .getWorkInfoByIdLiveData(workRequest.id)
                .observe(context) { workInfo: WorkInfo ->
                    when (workInfo.state) {
                        WorkInfo.State.ENQUEUED -> {
                            val msg = "Operation Enqueued"
                            Log.d(TAG, msg)
                        }

                        WorkInfo.State.RUNNING -> {
                            val msg = "Operation Running"
                            Log.d(TAG, msg)
                        }

                        WorkInfo.State.SUCCEEDED -> {
                            val msg =
                                "Room DB has been updated"
                            onStateChange(msg)
                            Log.d(TAG, msg)
                        }

                        WorkInfo.State.FAILED -> {
                            val msg = "Operation Failed"
                            onStateChange(msg)
                            Log.d(TAG, msg)
                        }

                        WorkInfo.State.BLOCKED -> {
                            val msg = "Operation Blocked"
                            Log.d(TAG, msg)
                        }

                        WorkInfo.State.CANCELLED -> {
                            val msg = "Operation Cancelled"
                            Log.d(TAG, msg)
                        }
                    }
                }
        }
    }
}
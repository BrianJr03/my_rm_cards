package jr.brian.myrmcards.model

import jr.brian.myrmcards.model.remote.ApiService
import jr.brian.myrmcards.model.remote.callApi
import javax.inject.Inject

class Repository @Inject constructor(private val apiService: ApiService) {
    fun getCharactersByName(name: String) = callApi {
        apiService.getCharacterByName(name)
    }

    fun getAllCharacters(pageNumber: String) = callApi {
        apiService.getAllCharacters(pageNumber)
    }
}
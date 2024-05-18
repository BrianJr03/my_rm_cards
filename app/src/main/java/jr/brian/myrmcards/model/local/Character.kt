package jr.brian.myrmcards.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import jr.brian.myrmcards.model.local.rmcharacter.Location
import jr.brian.myrmcards.model.local.rmcharacter.Origin

@Entity(tableName = "characters")
data class Character(
    @PrimaryKey val id: Int,
    val created: String,
    val episode: List<String>,
    val gender: String,
    val image: String,
    val location: Location,
    val name: String,
    val origin: Origin,
    val species: String,
    val status: String,
    val type: String,
    val url: String
) {
    companion object {
        private const val DEFAULT_EMPTY_VALUE = "N/A"

        private const val DEFAULT_IMAGE_URL =
            "https://rickandmortyapi.com/api/character/avatar/19.jpeg"

        val EMPTY = Character(
            created = DEFAULT_EMPTY_VALUE,
            episode = listOf(),
            gender = DEFAULT_EMPTY_VALUE,
            id = -1,
            image = DEFAULT_IMAGE_URL,
            location = Location(
                name = DEFAULT_EMPTY_VALUE,
                url = DEFAULT_EMPTY_VALUE
            ),
            name = DEFAULT_EMPTY_VALUE,
            origin = Origin(
                name = DEFAULT_EMPTY_VALUE,
                url = DEFAULT_EMPTY_VALUE
            ),
            species = DEFAULT_EMPTY_VALUE,
            status = DEFAULT_EMPTY_VALUE,
            type = DEFAULT_EMPTY_VALUE,
            url = DEFAULT_EMPTY_VALUE
        )
    }
}

class Converters {
    @TypeConverter
    fun fromLocation(location: Location): String {
        return Gson().toJson(location)
    }

    @TypeConverter
    fun toLocation(value: String): Location {
        return Gson().fromJson(value, Location::class.java)
    }

    @TypeConverter
    fun fromOrigin(origin: Origin): String {
        return Gson().toJson(origin)
    }

    @TypeConverter
    fun toOrigin(value: String): Origin {
        return Gson().fromJson(value, Origin::class.java)
    }

    @TypeConverter
    fun fromEpisode(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromEpisode(list: List<String>): String {
        return Gson().toJson(list)
    }
}
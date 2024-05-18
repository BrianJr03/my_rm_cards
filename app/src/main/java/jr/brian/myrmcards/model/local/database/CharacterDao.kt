package jr.brian.myrmcards.model.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import jr.brian.myrmcards.model.local.Character
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCharacter(character: Character)

    @Update
    fun updateCharacter(character: Character)

    @Query("SELECT * FROM characters")
    fun getCharacters(): Flow<List<Character>>

    @Query("SELECT * FROM characters WHERE id = :id")
    fun getCharacterById(id: Int): Flow<Character?>

    @Delete
    fun removeCharacter(character: Character)

    @Query("DELETE FROM characters")
    fun removeAllCharacters()
}
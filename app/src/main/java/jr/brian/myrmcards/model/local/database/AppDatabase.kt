package jr.brian.myrmcards.model.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import jr.brian.myrmcards.model.local.Character
import jr.brian.myrmcards.model.local.Converters

@Database(
    entities = [Character::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): CharacterDao
}
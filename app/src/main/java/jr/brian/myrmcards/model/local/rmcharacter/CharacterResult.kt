package jr.brian.myrmcards.model.local.rmcharacter

import jr.brian.myrmcards.model.local.Character

data class CharacterResult(
    val info: Info,
    val results: List<Character>
)
package com.imposter.game.model

data class Player(
    val id: Int,
    val name: String,
    val score: Int = 0,
    val avatarPath: String? = null,
)

sealed class Role {
    data class Crew(val word: String) : Role()
    data object Imposter : Role()
}

data class RoundPlayer(
    val player: Player,
    val role: Role,
    var revealed: Boolean = false,
)

enum class ImposterCountMode {
    FIXED,
    RANDOM,
    AUTO,
}

data class GameSettings(
    val imposterMode: ImposterCountMode = ImposterCountMode.FIXED,
    val fixedImposters: Int = 1,
    val randomMin: Int = 1,
    val randomMax: Int = 2,
    val selectedCategoryIds: Set<String> = setOf("tiere", "essen", "orte", "gegenstaende", "sport", "lustiges"),
    val imposterKnowsCategory: Boolean = true,
    val showClueOrder: Boolean = true,
    val timeLimitEnabled: Boolean = false,
    val timeLimitSeconds: Int = 60,
)

enum class GamePhase {
    LOBBY,
    REVEAL,
    CLUES,
    VOTING,
    REVEAL_RESULT,
    ROUND_END,
}

data class WinResult(
    val crewWon: Boolean,
    val accusedPlayer: Player?,
    val imposters: List<Player>,
    val secretWord: String,
    val category: String,
)

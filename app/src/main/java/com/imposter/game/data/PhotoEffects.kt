package com.imposter.game.data

data class PhotoEffect(
    val id: String,
    val name: String,
    val emoji: String,
    val matrix: FloatArray,
) {
    override fun equals(other: Any?): Boolean = other is PhotoEffect && other.id == id
    override fun hashCode(): Int = id.hashCode()
}

object PhotoEffects {

    val all: List<PhotoEffect> = listOf(
        PhotoEffect("none", "Original", "✨", identity()),
        PhotoEffect("bw", "S/W", "🎞", grayscale()),
        PhotoEffect("noir", "Noir", "🕴", noir()),
        PhotoEffect("sepia", "Sepia", "📜", sepia()),
        PhotoEffect("vintage", "Vintage", "📷", vintage()),
        PhotoEffect("pop", "Pop", "💥", saturation(1.6f)),
        PhotoEffect("acid", "Acid", "🌈", saturation(2.5f)),
        PhotoEffect("cool", "Kühl", "❄", cool()),
        PhotoEffect("warm", "Warm", "🔥", warm()),
        PhotoEffect("frost", "Eis", "🧊", frost()),
        PhotoEffect("heat", "Hitze", "🌶", heat()),
        PhotoEffect("neon", "Neon", "💫", neon()),
        PhotoEffect("cyber", "Cyber", "🤖", cyber()),
        PhotoEffect("invert", "Negativ", "🌒", invert()),
        PhotoEffect("galaxy", "Galaxie", "🌌", galaxy()),
        PhotoEffect("rage", "Hulk", "💚", rage()),
        PhotoEffect("alien", "Alien", "👽", alien()),
        PhotoEffect("zombie", "Zombie", "🧟", zombie()),
    )

    val identity = all.first()

    private fun identity() = floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 1f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f,
    )

    private fun grayscale() = floatArrayOf(
        0.299f, 0.587f, 0.114f, 0f, 0f,
        0.299f, 0.587f, 0.114f, 0f, 0f,
        0.299f, 0.587f, 0.114f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f,
    )

    private fun noir() = floatArrayOf(
        0.42f, 0.82f, 0.16f, 0f, -55f,
        0.42f, 0.82f, 0.16f, 0f, -55f,
        0.42f, 0.82f, 0.16f, 0f, -55f,
        0f, 0f, 0f, 1f, 0f,
    )

    private fun sepia() = floatArrayOf(
        0.393f, 0.769f, 0.189f, 0f, 0f,
        0.349f, 0.686f, 0.168f, 0f, 0f,
        0.272f, 0.534f, 0.131f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f,
    )

    private fun vintage() = floatArrayOf(
        0.55f, 0.45f, 0.20f, 0f, 10f,
        0.40f, 0.60f, 0.20f, 0f, 5f,
        0.30f, 0.40f, 0.30f, 0f, -10f,
        0f, 0f, 0f, 1f, 0f,
    )

    fun saturation(s: Float): FloatArray {
        val inv = 1f - s
        val r = 0.213f * inv
        val g = 0.715f * inv
        val b = 0.072f * inv
        return floatArrayOf(
            r + s, g, b, 0f, 0f,
            r, g + s, b, 0f, 0f,
            r, g, b + s, 0f, 0f,
            0f, 0f, 0f, 1f, 0f,
        )
    }

    private fun cool() = floatArrayOf(
        0.85f, 0f, 0f, 0f, 0f,
        0f, 1.00f, 0f, 0f, 5f,
        0f, 0f, 1.25f, 0f, 25f,
        0f, 0f, 0f, 1f, 0f,
    )

    private fun warm() = floatArrayOf(
        1.20f, 0f, 0f, 0f, 25f,
        0f, 1.05f, 0f, 0f, 5f,
        0f, 0f, 0.85f, 0f, -10f,
        0f, 0f, 0f, 1f, 0f,
    )

    private fun frost() = floatArrayOf(
        0.70f, 0f, 0f, 0f, 0f,
        0f, 0.95f, 0.10f, 0f, 15f,
        0f, 0f, 1.35f, 0f, 40f,
        0f, 0f, 0f, 1f, 0f,
    )

    private fun heat() = floatArrayOf(
        1.55f, 0.10f, 0f, 0f, 20f,
        0f, 0.85f, 0f, 0f, 0f,
        0f, 0f, 0.55f, 0f, -25f,
        0f, 0f, 0f, 1f, 0f,
    )

    private fun neon() = floatArrayOf(
        1.45f, -0.20f, -0.10f, 0f, 10f,
        -0.10f, 1.30f, 0.10f, 0f, 0f,
        0.10f, -0.10f, 1.55f, 0f, 15f,
        0f, 0f, 0f, 1f, 0f,
    )

    private fun cyber() = floatArrayOf(
        1.20f, 0f, 0.40f, 0f, 0f,
        0f, 0.80f, 0.20f, 0f, 0f,
        0.30f, 0f, 1.30f, 0f, 20f,
        0f, 0f, 0f, 1f, 0f,
    )

    private fun invert() = floatArrayOf(
        -1f, 0f, 0f, 0f, 255f,
        0f, -1f, 0f, 0f, 255f,
        0f, 0f, -1f, 0f, 255f,
        0f, 0f, 0f, 1f, 0f,
    )

    private fun galaxy() = floatArrayOf(
        0.55f, 0f, 0.45f, 0f, 10f,
        0f, 0.45f, 0.35f, 0f, 0f,
        0.25f, 0f, 1.15f, 0f, 35f,
        0f, 0f, 0f, 1f, 0f,
    )

    private fun rage() = floatArrayOf(
        0.30f, 0.40f, 0f, 0f, 0f,
        0.10f, 1.50f, 0.10f, 0f, 10f,
        0f, 0.30f, 0.30f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f,
    )

    private fun alien() = floatArrayOf(
        0f, 0.50f, 0.50f, 0f, 0f,
        0.20f, 1.30f, 0.10f, 0f, 0f,
        0.40f, 0.30f, 0.40f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f,
    )

    private fun zombie() = floatArrayOf(
        0.40f, 0.50f, 0.10f, 0f, -20f,
        0.20f, 0.90f, 0.10f, 0f, 0f,
        0.10f, 0.30f, 0.20f, 0f, -30f,
        0f, 0f, 0f, 1f, 0f,
    )
}

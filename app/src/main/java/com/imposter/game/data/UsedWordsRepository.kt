package com.imposter.game.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UsedWordsRepository(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(
        "imposter_state",
        Context.MODE_PRIVATE,
    )

    private val _used = MutableStateFlow(load())
    val used: StateFlow<Set<String>> = _used.asStateFlow()

    private fun load(): Set<String> =
        prefs.getStringSet(KEY_USED, emptySet())?.toSet() ?: emptySet()

    fun markUsed(word: String) {
        val next = _used.value + word
        _used.value = next
        prefs.edit().putStringSet(KEY_USED, next).apply()
    }

    fun clear() {
        _used.value = emptySet()
        prefs.edit().remove(KEY_USED).apply()
    }

    companion object {
        private const val KEY_USED = "used_words"
    }
}

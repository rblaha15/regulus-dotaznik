package com.regulus.dotaznik

import android.content.Context
import com.google.gson.Gson


class Saver(private val context: Context) {

    fun save(stranky: Stranky) {
        val prefs = context.getSharedPreferences("PREFS_DOTAZNIK", Context.MODE_PRIVATE)
        val gson = Gson()

        prefs.edit().apply {
            putString("udaje", gson.toJson(stranky))

            apply()
        }
    }

    fun get(): Stranky {
        val prefs = context.getSharedPreferences("PREFS_DOTAZNIK", Context.MODE_PRIVATE)
        val gson = Gson()

        val json = prefs.getString("udaje", "")

        return if (json != "")
            gson.fromJson(json, Stranky::class.java)
        else
            Stranky()
    }

    fun delete() {
        val prefs = context.getSharedPreferences("PREFS_DOTAZNIK", Context.MODE_PRIVATE)

        prefs.edit().apply {
            putString("udaje", "")

            apply()
        }
    }
}
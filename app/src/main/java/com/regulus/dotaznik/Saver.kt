package com.regulus.dotaznik

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson

class Saver(private val context: Context) {

    fun save(stranky: Stranky) {

        Thread {

            val prefs = context.prefs
            val gson = Gson()

            prefs.edit {
                putString("udaje", gson.toJson(stranky))

            }
        }.start()
    }

    fun get(): Stranky {
        val prefs = context.prefs
        val gson = Gson()

        val json = prefs.getString("udaje", "")

        return if (json != "")
            gson.fromJson(json, Stranky::class.java)
        else
            Stranky()
    }

    fun delete() {

        Thread {

            val prefs = context.prefs

            prefs.edit {
                putString("udaje", "")

            }
        }
    }
}

val Context.saver: Saver
    get() = Saver(this)

val Context.prefs: SharedPreferences
    get() = getSharedPreferences("PREFS_DOTAZNIK", Context.MODE_PRIVATE)
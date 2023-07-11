package com.regulus.dotaznik

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.gson.Gson


class Saver(private val context: Context) {

    fun save(stranky: Stranky) {

        val prefs = context.prefs
        val gson = Gson()

        prefs.edit {

            putString("udaje", gson.toJson(stranky))
        }
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

    fun delete(activity: AppCompatActivity) {

        val prefs = context.prefs

        activity.finish()

        prefs.edit {

            clear()
        }

        activity.intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.intent.removeExtra("delete")
        context.startActivity(activity.intent)
    }
}

val Context?.saver: Saver
    get() = Saver(this!!)

val Context?.prefs: SharedPreferences
    get() = this!!.getSharedPreferences("PREFS_DOTAZNIK", Context.MODE_PRIVATE)

val Context?.prefsPrihlaseni: SharedPreferences
    get() = this!!.getSharedPreferences("PREFS_PRIHLASENI", Context.MODE_PRIVATE)

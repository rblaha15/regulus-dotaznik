package cz.regulus.dotaznik

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class Saver(private val context: Context) {

    fun save(stranky: cz.regulus.dotaznik.Stranky) {

        val prefs = context.prefs

        prefs.edit {

            putString("udaje", Json.encodeToString(stranky))
        }
    }

    fun get(): cz.regulus.dotaznik.Stranky {
        val prefs = context.prefs

        val json = prefs.getString("udaje", "") ?: ""

        return if (json.isNotBlank())
            Json.decodeFromString(json)
        else
            cz.regulus.dotaznik.Stranky()
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

val Context?.saver: cz.regulus.dotaznik.Saver
    get() = cz.regulus.dotaznik.Saver(this!!)

val Context?.prefs: SharedPreferences
    get() = this!!.getSharedPreferences("PREFS_DOTAZNIK", Context.MODE_PRIVATE)

val Context?.prefsPrihlaseni: SharedPreferences
    get() = this!!.getSharedPreferences("PREFS_PRIHLASENI", Context.MODE_PRIVATE)

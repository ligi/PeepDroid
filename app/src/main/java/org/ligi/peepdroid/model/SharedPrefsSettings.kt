package org.ligi.peepdroid.model

import android.content.Context
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatDelegate
import org.ligi.peepdroid.R

class SharedPrefsSettings(private val context: Context) : Settings {

    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    override fun isTimeWanted() = sharedPreferences.getBoolean(context.getString(R.string.key_prefs_time), true)
    override fun isAvatarsWanted() = sharedPreferences.getBoolean(context.getString(R.string.key_prefs_avatars), true)


    override fun getNightMode() = when (sharedPreferences.getString(context.getString(R.string.key_prefs_day_night), context.getString(R.string.default_day_night))) {
        "day" -> AppCompatDelegate.MODE_NIGHT_NO
        "night" -> AppCompatDelegate.MODE_NIGHT_YES
        "auto" -> AppCompatDelegate.MODE_NIGHT_AUTO
        else -> AppCompatDelegate.MODE_NIGHT_AUTO
    }

}

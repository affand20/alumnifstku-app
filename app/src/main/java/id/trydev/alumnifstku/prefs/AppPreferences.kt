package id.trydev.alumnifstku.prefs

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {

    private val PREFS_NAME = "id.trydev.pendekar.prefs"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val TOKEN = "TOKEN"
    private val USER_ID = "USER_ID"
    private val USER_NAME = "USER_NAME"
    private val FIRST_OPEN = "FIRST_OPEN"
    private val HAS_FILL_BIODATA = "HAS_FILL_BIODATA"

    var token: String?
        get() = prefs.getString(TOKEN, null)
        set(value) = prefs.edit().putString(TOKEN, value).apply()

    var userId: String?
        get() = prefs.getString(USER_ID, null)
        set(value) = prefs.edit().putString(USER_ID, value).apply()

    var userName: String?
        get() = prefs.getString(USER_NAME, null)
        set(value) = prefs.edit().putString(USER_NAME, value).apply()

    var firstOpen: Boolean
        get() = prefs.getBoolean(FIRST_OPEN, true)
        set(value) = prefs.edit().putBoolean(FIRST_OPEN, value).apply()

    var hasFillBio: Boolean
        get() = prefs.getBoolean(HAS_FILL_BIODATA, false)
        set(value) = prefs.edit().putBoolean(HAS_FILL_BIODATA, value).apply()

    fun resetPreference() {
        prefs.edit().clear().apply()
    }

}
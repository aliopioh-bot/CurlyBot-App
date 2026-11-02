package ir.nimacurly.curlybot

import android.content.Context

object Prefs {
    private fun sp(c: Context) = c.getSharedPreferences("curlybot", Context.MODE_PRIVATE)

    fun room(c: Context): String = sp(c).getString("room", "") ?: ""
    fun token(c: Context): String = sp(c).getString("token", "") ?: ""
    fun owner(c: Context): String = sp(c).getString("owner", "") ?: ""
    fun admins(c: Context): String = sp(c).getString("admins", "") ?: ""

    fun save(c: Context, room: String, token: String, owner: String, admins: String) {
        sp(c).edit()
            .putString("room", room.trim())
            .putString("token", token.trim())
            .putString("owner", owner.trim().removePrefix("@"))
            .putString("admins", admins.trim())
            .apply()
    }
}

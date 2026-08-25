package com.example.saywhatandroid

import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject

data class VenueSession(
    val id: String,
    val venueName: String,
    val streamUrl: String
)

class VenueSessionStore(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun load(): List<VenueSession> = runCatching {
        val savedJson = preferences.getString(RECENT_SESSIONS_KEY, "[]") ?: "[]"
        val saved = JSONArray(savedJson)
        buildList {
            for (index in 0 until saved.length()) {
                val item = saved.optJSONObject(index) ?: continue
                val id = item.optString("id")
                val venueName = item.optString("venueName")
                val streamUrl = item.optString("streamUrl")
                if (id.isNotBlank() && venueName.isNotBlank() && streamUrl.isNotBlank()) {
                    add(VenueSession(id, venueName, streamUrl))
                }
            }
        }.take(MAXIMUM_RECENT_SESSIONS)
    }.getOrDefault(emptyList())

    fun record(session: VenueSession, existing: List<VenueSession>): List<VenueSession> {
        val updated = (listOf(session) + existing)
            .distinctBy { it.id }
            .take(MAXIMUM_RECENT_SESSIONS)
        val saved = JSONArray()
        updated.forEach { venueSession ->
            saved.put(
                JSONObject()
                    .put("id", venueSession.id)
                    .put("venueName", venueSession.venueName)
                    .put("streamUrl", venueSession.streamUrl)
            )
        }
        preferences.edit().putString(RECENT_SESSIONS_KEY, saved.toString()).apply()
        return updated
    }

    private companion object {
        const val PREFERENCES_NAME = "say_what_venue_sessions"
        const val RECENT_SESSIONS_KEY = "recent_sessions"
        const val MAXIMUM_RECENT_SESSIONS = 50
    }
}

object VenueSessionParser {
    fun parse(payload: String): VenueSession? {
        val value = payload.trim()
        if (value.isEmpty()) return null

        return runCatching {
            if (value.startsWith("{")) fromJson(JSONObject(value)) else fromUri(Uri.parse(value))
        }.getOrNull()
    }

    private fun fromJson(json: JSONObject): VenueSession? {
        val streamValue = json.optString("streamUrl").ifBlank { json.optString("stream_url") }
        val streamUrl = normalizedStreamUrl(streamValue) ?: return null

        val name = json.optString("venueName")
            .ifBlank { json.optString("venue_name") }
            .ifBlank { json.optString("venue") }
            .ifBlank { displayNameFromUrl(streamUrl) }

        return VenueSession(
            id = json.optString("sessionId").ifBlank { json.optString("session_id") }
                .ifBlank { streamUrl },
            venueName = name,
            streamUrl = streamUrl
        )
    }

    private fun fromUri(uri: Uri): VenueSession? {
        val embeddedStream = uri.getQueryParameter("streamUrl")
            ?: uri.getQueryParameter("stream_url")
            ?: uri.getQueryParameter("stream")
        val streamUrl = normalizedStreamUrl(embeddedStream ?: uri.toString()) ?: return null

        val suppliedName = uri.getQueryParameter("venueName")
            ?: uri.getQueryParameter("venue_name")
            ?: uri.getQueryParameter("venue")
            ?: uri.getQueryParameter("name")
        val name = suppliedName?.takeIf { it.isNotBlank() } ?: displayNameFromUrl(streamUrl)

        return VenueSession(
            id = uri.getQueryParameter("sessionId")
                ?: uri.getQueryParameter("session_id")
                ?: streamUrl,
            venueName = name,
            streamUrl = streamUrl
        )
    }

    private fun displayNameFromUrl(value: String): String {
        val host = Uri.parse(value).host.orEmpty().removePrefix("www.")
        return host.takeIf { it.isNotBlank() } ?: "Venue audio session"
    }

    private fun normalizedStreamUrl(value: String): String? {
        val streamUrl = value.trim()
        if (streamUrl.isBlank()) return null
        return if (Uri.parse(streamUrl).scheme.isNullOrBlank()) "https://$streamUrl" else streamUrl
    }
}

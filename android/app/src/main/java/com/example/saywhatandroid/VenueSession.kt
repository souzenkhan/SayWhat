package com.example.saywhatandroid

import android.net.Uri
import org.json.JSONObject

data class VenueSession(
    val id: String,
    val venueName: String,
    val streamUrl: String
)

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

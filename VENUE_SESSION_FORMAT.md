# Venue session data

The app no longer contains sample venue names or a fixed audio stream URL. A venue connection must provide the audio stream URL and may provide its display name and stable session ID.

QR codes can contain JSON:

```json
{
  "sessionId": "venue-session-id",
  "venueName": "Venue display name",
  "streamUrl": "https://audio.example.com/live"
}
```

They can also contain a URL whose query parameters carry the same metadata:

```text
https://audio.example.com/live?sessionId=venue-session-id&venueName=Venue%20display%20name
```

Supported aliases are `session_id`, `venue_name`, `venue`, `name`, `stream_url`, and `stream`. If the name is omitted, the app derives a neutral label from the stream host. Recent venues are populated only from sessions the user actually connects to.

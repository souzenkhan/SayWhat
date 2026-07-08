console.log("STARTING...");

const express = require("express");
const fs = require("fs");
const path = require("path");
const { spawn } = require("child_process");
const http = require("http");
const WebSocket = require("ws");
const wrtc = require("wrtc"); // npm install wrtc  (use @roamhq/wrtc if on Apple Silicon)

const app = express();
const server = http.createServer(app); // needed so WebSocket can share the same port

// =========================
// TEST ROUTE
// =========================
app.get("/", (req, res) => {
  res.send("Server is working");
});

// =========================
// FILE STREAM (stable fallback)
// =========================
app.get("/audio", (req, res) => {
  const filePath = path.join(__dirname, "sample.mp3");

  if (!fs.existsSync(filePath)) {
    return res.status(404).send("No audio file found");
  }

  console.log("📁 Serving audio file to:", req.ip);

  res.sendFile(filePath);
});

// =========================
// LIVE MP3 STREAM (HTTP) — replaced by WebRTC below
// =========================
/*
let clients = [];

app.get("/audio-live", (req, res) => {
  console.log("🎧 Client connected:", req.ip);

  res.writeHead(200, {
    "Content-Type": "audio/mpeg",
    "Cache-Control": "no-cache, no-store, must-revalidate",
    Pragma: "no-cache",
    Expires: "0",
    Connection: "keep-alive",
  });

  clients.push(res);

  console.log("📱 Active clients:", clients.length);

  req.on("close", () => {
    console.log("🔌 Client disconnected");

    clients = clients.filter((client) => client !== res);

    console.log("📱 Active clients:", clients.length);
  });

  res.on("error", () => {
    clients = clients.filter((client) => client !== res);

    console.log("⚠️ Removed broken client");
  });
});
*/

// =========================
// WebRTC audio source (fed by ffmpeg below)
// =========================

const SAMPLE_RATE = 48000;
const CHANNELS = 1;
const BITS_PER_SAMPLE = 16;
const SAMPLES_PER_FRAME = SAMPLE_RATE / 100; // 10ms frame, required by wrtc

const audioSource = new wrtc.nonstandard.RTCAudioSource();

// =========================
// WebRTC signaling (WebSocket, replaces the old HTTP client list)
// =========================

const wss = new WebSocket.Server({ server, path: "/ws" });

const peers = new Set();

wss.on("connection", (ws) => {
  console.log("🎧 Client connected (signaling)");

  const pc = new wrtc.RTCPeerConnection({
    iceServers: [{ urls: "stun:stun.l.google.com:19302" }],
  });

  const track = audioSource.createTrack();
  pc.addTrack(track);
  peers.add(pc);

  console.log("📱 Active clients:", peers.size);

  pc.onicecandidate = (event) => {
    if (event.candidate) {
      ws.send(JSON.stringify({ type: "candidate", candidate: event.candidate }));
    }
  };

  pc.onconnectionstatechange = () => {
    console.log("📡 Peer connection state:", pc.connectionState);

    if (["disconnected", "failed", "closed"].includes(pc.connectionState)) {
      peers.delete(pc);
      track.stop();
      console.log("📱 Active clients:", peers.size);
    }
  };

  ws.on("message", async (raw) => {
    const msg = JSON.parse(raw);

    try {
      if (msg.type === "offer") {
        await pc.setRemoteDescription(new wrtc.RTCSessionDescription(msg.sdp));
        const answer = await pc.createAnswer();
        await pc.setLocalDescription(answer);
        ws.send(JSON.stringify({ type: "answer", sdp: pc.localDescription }));
      } else if (msg.type === "candidate") {
        await pc.addIceCandidate(new wrtc.RTCIceCandidate(msg.candidate));
      }
    } catch (err) {
      console.log("⚠️ Signaling error:", err.message);
    }
  });

  ws.on("close", () => {
    console.log("🔌 Client disconnected");

    peers.delete(pc);
    track.stop();
    pc.close();

    console.log("📱 Active clients:", peers.size);
  });

  ws.on("error", () => {
    peers.delete(pc);
    track.stop();
    pc.close();

    console.log("⚠️ Removed broken client");
  });
});

// =========================
// FFmpeg microphone capture -> raw PCM -> WebRTC audio source
// =========================

/*
// Old ffmpeg command: encoded straight to MP3 for HTTP streaming
const ffmpeg = spawn("ffmpeg", [
  "-f",
  "avfoundation",

  "-i",
  "none:1",

  "-ac",
  "1",

  "-ar",
  "8000",

  "-b:a",
  "32k",

  "-fflags",
  "nobuffer",

  "-flags",
  "low_delay",

  "-flush_packets",
  "1",

  "-f",
  "mp3",

  "-"
]);
*/

// New ffmpeg command: raw PCM (s16le) — WebRTC needs uncompressed samples,
// not mp3, so it can feed them straight into RTCAudioSource.
const ffmpeg = spawn("ffmpeg", [
  "-f",
  "avfoundation",

  "-i",
  "none:1",

  "-ac",
  String(CHANNELS),

  "-ar",
  String(SAMPLE_RATE),

  "-fflags",
  "nobuffer",

  "-flags",
  "low_delay",

  "-flush_packets",
  "1",

  "-f",
  "s16le",

  "-acodec",
  "pcm_s16le",

  "-"
]);

let pcmBuffer = Buffer.alloc(0);
const bytesPerFrame = SAMPLES_PER_FRAME * CHANNELS * (BITS_PER_SAMPLE / 8);

ffmpeg.stdout.on("data", (chunk) => {
  pcmBuffer = Buffer.concat([pcmBuffer, chunk]);

  while (pcmBuffer.length >= bytesPerFrame) {
    const frame = pcmBuffer.subarray(0, bytesPerFrame);
    pcmBuffer = pcmBuffer.subarray(bytesPerFrame);

    audioSource.onData({
      samples: new Int16Array(frame.buffer, frame.byteOffset, frame.length / 2),
      sampleRate: SAMPLE_RATE,
      bitsPerSample: BITS_PER_SAMPLE,
      channelCount: CHANNELS,
      numberOfFrames: SAMPLES_PER_FRAME,
    });
  }
});

ffmpeg.stderr.on("data", (data) => {
  // Optional debug:
  // console.log(data.toString());
});

ffmpeg.on("close", () => {
  console.log("FFmpeg process closed");
});

console.log("🎤 FFmpeg live PCM capture started (feeding WebRTC audio source)");

// =========================
// START SERVER
// =========================
server.listen(3000, "0.0.0.0", () => {
  console.log("✅ Server running on port 3000");
});
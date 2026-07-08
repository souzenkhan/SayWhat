## Say What

### System Overview

Say What is an accessibility-focused mobile application designed to improve the listening experience at live events. Audio from a presenter’s microphone is streamed over a local network and delivered to mobile devices, allowing users to listen through Bluetooth hearing aids, headphones, or phone speakers.

### The system consists of:

Node.js streaming server
Native iOS application
Native Android application

### Supported Platforms

Backend:
macOS
Windows
iOS:
macOS
Xcode
Android:
Windows or macOS
Android Studio

### Legacy React Prototype

An early React Native prototype was developed during initial project exploration. The final architecture uses separate native iOS and Android applications connected to a shared backend streaming server. The React prototype is no longer maintained and is not required to run the current system.

### Backend Setup

Prerequisites
Install:
Node.js
npm
FFmpeg
Verify:
node -v
npm -v
ffmpeg -version

Install FFmpeg
macOS:
brew install ffmpeg

Windows:
Download FFmpeg from:
https://ffmpeg.org/download.html
Running the Backend
Install dependencies:
cd server
npm install

Find your local IP address:
macOS:
ifconfig

Windows:
ipconfig

Start the server:
cd server
node server.js

The server will run on:
http://<YOUR_IP>:3000

Microphone Configuration
To identify microphone devices:
macOS:
ffmpeg -f avfoundation -list_devices true -i ""

Windows:
ffmpeg -list_devices true -f dshow -i dummy

Update the microphone device in:
server/server.js

### Running the iOS Application

Requirements:
Xcode
Apple ID
iPhone or iOS Simulator
Steps:
Open the iOS project in Xcode.
Select a simulator or connected iPhone.
Update the backend IP address if necessary.
Run the application.
If running on a physical device, trust the developer profile under:
Settings → General → VPN & Device Management

### Running the Android Application

Requirements:
Android Studio
Android SDK

Steps:
Open the Android project in Android Studio.
Allow Gradle Sync to complete.
Launch an Android Virtual Device using Device Manager.
Update the backend IP address if necessary.
Click Run.

Network Requirements
The server and mobile device must be connected to the same WiFi network.

Avoid:
Mobile data
VPNs
Guest networks

Testing
Static audio:
http://<YOUR_IP>:3000/audio

Live microphone stream:
http://<YOUR_IP>:3000/audio-live

### Known Limitations

Live streaming currently uses HTTP-based audio streaming.
Audio latency varies by platform.
Mobile devices may buffer several seconds of audio before playback.
The backend currently runs locally for demonstration purposes.

### Future Work

WebRTC-based low-latency streaming
Cloud-hosted backend deployment
Improved Bluetooth hearing aid support
Audio compression and latency optimization

### Team

Informatics 117 : Say What
Souzen Khan - Network & Streaming Developer
Fatima - Android Developer
Maryam - iOS Developer
Avni - Design & React Prototype

# OWNER

Souzen — Networking & Streaming

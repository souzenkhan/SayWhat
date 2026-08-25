package com.example.saywhatandroid

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat


data class DeviceItem(
    val name: String,
    val address: String,
    val isConnected: Boolean
)

enum class AppScreen {
    WELCOME,
    HOME,
    QR_SCAN,
    SETUP,
    AUDIO,
    HELP,
    ABOUT,
    CONNECTION_ERROR,
    RECENT,
    TRANSLATE
}

class MainActivity : ComponentActivity() {

    private var bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var audioManager: AudioManager
    private var mediaPlayer: MediaPlayer? = null
    private var audioDeviceCallback: AudioDeviceCallback? = null
    private var wasPlayingBeforeDeviceChange = false
    private var updatePlaybackStatus: ((String) -> Unit)? = null
    private var activeStreamUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val venueSessionStore = VenueSessionStore(applicationContext)

        setContent {
            var devices by remember { mutableStateOf(emptyList<DeviceItem>()) }
            var statusText by remember { mutableStateOf("Not checked yet") }
            var playbackStatus by remember { mutableStateOf("Stopped") }
            updatePlaybackStatus = { newStatus ->
                playbackStatus = newStatus
            }
            var currentScreen by remember { mutableStateOf(AppScreen.WELCOME) }
            var showEndConfirmation by remember { mutableStateOf(false) }
            var currentSession by remember { mutableStateOf<VenueSession?>(null) }
            var recentSessions by remember { mutableStateOf(venueSessionStore.load()) }
            var bluetoothDeviceName by remember { mutableStateOf("No Bluetooth device connected") }
            var shouldStartAudio by remember { mutableStateOf(false) }
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) {
                devices = loadBluetoothDevices()
                statusText = getAudioConnectionStatus()
            }

            LaunchedEffect(Unit) {
                requestNeededPermissions { permissions ->
                    permissionLauncher.launch(permissions)
                }

                statusText = getAudioConnectionStatus()
                bluetoothDeviceName = getConnectedBluetoothDeviceName()
                logAudioDevices()
            }

            DisposableEffect(Unit) {
                setupAudioDeviceCallback { newStatus ->
                    statusText = newStatus
                    bluetoothDeviceName = getConnectedBluetoothDeviceName()
                }

                onDispose {
                    audioDeviceCallback?.let {
                        audioManager.unregisterAudioDeviceCallback(it)
                    }
                    releasePlayer()
                }
            }

            MaterialTheme {
                when (currentScreen) {
                    AppScreen.WELCOME -> {
                        WelcomeScreen(
                            onTakeTour = { currentScreen = AppScreen.HOME },
                            onSkip = { currentScreen = AppScreen.HOME }
                        )
                    }

                    AppScreen.HOME -> {
                        SayWhatHomeScreen(
                            onScanClick = {
                                currentScreen = AppScreen.SETUP
                            },
                            onAudioClick = {
                                currentScreen = AppScreen.RECENT
                            },
                            onHelpClick = {
                                currentScreen = AppScreen.HELP
                            },
                            onBluetoothSettingsClick = {
                                startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                            },
                            onAboutClick = {
                                currentScreen = AppScreen.ABOUT
                            }
                        )
                    }

                    AppScreen.QR_SCAN -> {
                        QRScanScreen(
                            onVenueScanned = { payload ->
                                VenueSessionParser.parse(payload)?.let { session ->
                                    currentSession = session
                                    recentSessions = venueSessionStore.record(session, recentSessions)
                                    shouldStartAudio = true
                                    currentScreen = AppScreen.AUDIO
                                }
                            },
                            onBackClick = {
                                currentScreen = AppScreen.SETUP
                            },
                            onHomeClick = {
                                currentScreen = AppScreen.HOME
                            },
                            onScanClick = {
                                currentScreen = AppScreen.SETUP
                            },
                            onAudioClick = {
                                currentScreen = AppScreen.RECENT
                            },
                            onHelpClick = {
                                currentScreen = AppScreen.HELP
                            },
                            onSettingsClick = {
                                startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                            }
                        )
                    }

                    AppScreen.SETUP -> {
                        SetupScreen(
                            onScanVenueClick = {
                                currentScreen = AppScreen.QR_SCAN
                            },
                            onConnectUsingUrlClick = { url ->
                                VenueSessionParser.parse(url)?.let { session ->
                                    currentSession = session
                                    recentSessions = venueSessionStore.record(session, recentSessions)
                                    shouldStartAudio = true
                                    currentScreen = AppScreen.AUDIO
                                }
                            },
                            onHelpClick = {
                                currentScreen = AppScreen.HELP
                            },
                            onHomeClick = {
                                currentScreen = AppScreen.HOME
                            },
                            onScanClick = {
                                currentScreen = AppScreen.SETUP
                            },
                            onAudioClick = {
                                currentScreen = AppScreen.RECENT
                            },
                            onSettingsClick = {
                                startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                            }
                        )
                    }

                    AppScreen.AUDIO -> {
                        LaunchedEffect(shouldStartAudio) {
                            if (shouldStartAudio) {
                                currentSession?.streamUrl?.let(::playAudio)
                                shouldStartAudio = false
                            }
                        }

                        AudioScreen(
                            bluetoothDeviceName = bluetoothDeviceName,
                            bluetoothStatus = statusText,
                            playbackStatus = playbackStatus,
                            venueName = currentSession?.venueName ?: "Venue audio session",
                            onHomeClick = {
                                currentScreen = AppScreen.HOME
                            },
                            onScanClick = {
                                currentScreen = AppScreen.SETUP
                            },
                            onRecentClick = {
                                currentScreen = AppScreen.RECENT
                            },
                            onHelpClick = {
                                currentScreen = AppScreen.HELP
                            },
                            onSettingsClick = {
                                startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                            },
                            onTranslateClick = {
                                currentScreen = AppScreen.TRANSLATE
                            },
                            onEndSessionClick = {
                                showEndConfirmation = true
                            },
                            onBackClick = {
                                currentScreen = AppScreen.SETUP
                            }
                        )
                    }

                    AppScreen.HELP -> {
                        HelpSupportScreen(
                            onHomeClick = {
                                currentScreen = AppScreen.HOME
                            },
                            onScanClick = {
                                currentScreen = AppScreen.SETUP
                            },
                            onAudioClick = {
                                currentScreen = AppScreen.RECENT
                            },
                            onAboutClick = {
                                currentScreen = AppScreen.ABOUT
                            },
                            onConnectionErrorClick = {
                                currentScreen = AppScreen.CONNECTION_ERROR
                            },
                            onSettingsClick = {
                                startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                            }
                        )
                    }

                    AppScreen.ABOUT -> {
                        AboutScreen(
                            onHomeClick = {
                                currentScreen = AppScreen.HOME
                            },
                            onScanClick = {
                                currentScreen = AppScreen.SETUP
                            },
                            onAudioClick = {
                                currentScreen = AppScreen.RECENT
                            },
                            onHelpClick = {
                                currentScreen = AppScreen.HELP
                            },
                            onSettingsClick = {
                                startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                            }
                        )
                    }

                    AppScreen.CONNECTION_ERROR -> {
                        ConnectionErrorScreen(
                            onTryAgainClick = {
                                currentScreen = AppScreen.SETUP
                            },
                            onGoToHelpClick = {
                                currentScreen = AppScreen.HELP
                            },
                            onHomeClick = {
                                currentScreen = AppScreen.HOME
                            },
                            onScanClick = {
                                currentScreen = AppScreen.SETUP
                            },
                            onAudioClick = {
                                currentScreen = AppScreen.RECENT
                            },
                            onHelpClick = {
                                currentScreen = AppScreen.HELP
                            },
                            onSettingsClick = {
                                startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                            }
                        )
                    }

                    AppScreen.RECENT -> {
                        RecentVenuesScreen(
                            sessions = recentSessions,
                            onHomeClick = { currentScreen = AppScreen.HOME },
                            onConnectClick = { currentScreen = AppScreen.SETUP },
                            onHelpClick = { currentScreen = AppScreen.HELP },
                            onSettingsClick = {
                                startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                            }
                        )
                    }

                    AppScreen.TRANSLATE -> {
                        TranslateScreen(
                            venueName = currentSession?.venueName ?: "Venue audio session",
                            onBack = { currentScreen = AppScreen.AUDIO },
                            onSettingsClick = {
                                startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                            }
                        )
                    }
                }

                if (showEndConfirmation) {
                    AlertDialog(
                        onDismissRequest = { showEndConfirmation = false },
                        containerColor = Color(0xFF3154C8),
                        title = {
                            Text(
                                "Are you sure you want to end the session?",
                                color = Color.White
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                showEndConfirmation = false
                                stopPlayback()
                                currentScreen = AppScreen.HOME
                            }) { Text("Yes", color = Color(0xFF17172A)) }
                        },
                        dismissButton = {
                            TextButton(onClick = { showEndConfirmation = false }) {
                                Text("Cancel", color = Color(0xFF17172A))
                            }
                        }
                    )
                }
            }
        }
    }
    private fun playAudio(source: String) {
        try {
            activeStreamUrl = source
            updatePlaybackStatus?.invoke("Connecting")
            Log.d("AUDIO", "playAudio called with source: $source")

            if (mediaPlayer != null) {
                Log.d("AUDIO", "Existing MediaPlayer found, releasing before starting new stream")
                releasePlayer()
            }

            if (source.startsWith("http://") || source.startsWith("https://")) {
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )

                    // Max software volume for MediaPlayer
                    setVolume(1.0f, 1.0f)

                    Log.d("AUDIO", "Setting data source")
                    setDataSource(source)

                    setOnPreparedListener { player ->
                        Log.d("AUDIO", "MediaPlayer prepared, starting playback")
                        player.start()
                        updatePlaybackStatus?.invoke("Live")
                    }

                    setOnBufferingUpdateListener { _, percent ->
                        Log.d("AUDIO", "Buffering: $percent%")
                    }

                    setOnErrorListener { _, what, extra ->
                        Log.e("AUDIO", "Playback error: what=$what extra=$extra")
                        releasePlayer()
                        updatePlaybackStatus?.invoke("Stopped")
                        true
                    }

                    Log.d("AUDIO", "Calling prepareAsync")
                    prepareAsync()
                }
            } else {
                Log.d("AUDIO", "Playing local sample audio")
                mediaPlayer = MediaPlayer.create(this, R.raw.sample_audio)
                mediaPlayer?.setVolume(1.0f, 1.0f)
                mediaPlayer?.start()
            }

            mediaPlayer?.setOnCompletionListener {
                Log.d("AUDIO", "Playback completed")
                stopPlayback()
                updatePlaybackStatus?.invoke("Stopped")
            }

        } catch (e: Exception) {
            Log.e("AUDIO", "Exception in playAudio", e)
            releasePlayer()
            updatePlaybackStatus?.invoke("Stopped")
        }
    }

    private fun pausePlayback() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                updatePlaybackStatus?.invoke("Paused")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopPlayback() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            updatePlaybackStatus?.invoke("Stopped")
        } catch (e: Exception) {
            e.printStackTrace()
            mediaPlayer = null
            updatePlaybackStatus?.invoke("Stopped")
        }
    }

    private fun releasePlayer() {
        try {
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupAudioDeviceCallback(onStatusChange: (String) -> Unit) {
        audioDeviceCallback = object : AudioDeviceCallback() {
            override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>) {
                logAudioDevices()
                onStatusChange(getAudioConnectionStatus())
            }

            override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>) {
                logAudioDevices()
                onStatusChange(getAudioConnectionStatus())

                val removedBluetoothDevice = removedDevices.any { isBluetoothAudioDevice(it) }

                if (removedBluetoothDevice && wasPlayingBeforeDeviceChange) {
                    try {
                        mediaPlayer?.let { player ->
                            if (!player.isPlaying) {
                                player.start()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("AUDIO", "Restarting playback after Bluetooth disconnect", e)
                        releasePlayer()
                        activeStreamUrl?.let(::playAudio)
                    }
                }
            }
        }

        audioManager.registerAudioDeviceCallback(audioDeviceCallback, null)
    }
    private fun logAudioDevices() {
        Log.d("AUDIO", getAudioConnectionStatus())

        audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).forEach {
            Log.d("AUDIO_DEVICE", "type=${it.type}, name=${it.productName}")
        }
    }

    private fun requestNeededPermissions(permissionLauncher: (Array<String>) -> Unit) {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            }

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        if (permissions.isNotEmpty()) {
            permissionLauncher(permissions.toTypedArray())
        }
    }

    private fun loadBluetoothDevices(): List<DeviceItem> {
        val connectedAudioNames = getConnectedAudioDeviceNames()
        val result = mutableListOf<DeviceItem>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return emptyList()
        }

        val bondedDevices: Set<BluetoothDevice> = bluetoothAdapter?.bondedDevices ?: emptySet()
        for (device in bondedDevices) {
            val name = device.name ?: "Unknown Device"
            result.add(
                DeviceItem(
                    name = name,
                    address = device.address ?: "No address",
                    isConnected = connectedAudioNames.contains(name)
                )
            )
        }

        return result.sortedBy { it.name }
    }

    private fun getConnectedAudioDeviceNames(): Set<String> {
        val outputs = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        val connectedNames = mutableSetOf<String>()

        for (device in outputs) {
            if (isBluetoothAudioDevice(device)) {
                connectedNames.add(device.productName?.toString() ?: "Bluetooth Audio")
            }
        }

        return connectedNames
    }

    private fun getConnectedBluetoothDeviceName(): String {
        val outputs = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)

        for (device in outputs) {
            if (isBluetoothAudioDevice(device)) {
                return device.productName?.toString() ?: "Bluetooth Audio Device"
            }
        }

        return "No Bluetooth device connected"
    }

    private fun isBluetoothAudioDevice(device: AudioDeviceInfo): Boolean {
        return device.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
                device.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P &&
                        device.type == AudioDeviceInfo.TYPE_HEARING_AID) ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                        device.type == AudioDeviceInfo.TYPE_BLE_HEADSET)
    }

    private fun getAudioConnectionStatus(): String {
        val outputs = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        val bluetoothConnected = outputs.any { isBluetoothAudioDevice(it) }

        return if (bluetoothConnected) {
            "Bluetooth audio device connected"
        } else {
            "No Bluetooth audio device connected"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }
}

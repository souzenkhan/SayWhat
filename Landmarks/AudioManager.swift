import Foundation
import AVFoundation
import Combine

final class AudioManager: NSObject, ObservableObject {
    @Published var isPlaying: Bool = false
    @Published var isBuffering: Bool = false
    @Published var volume: Float = 1.0
    @Published var errorMessage: String = ""
    @Published var currentOutput: String = "Unknown"
    @Published var playbackStatus: String = "Not Playing"

    private var player: AVPlayer?

    override init() {
        super.init()

        configureAudioSession()

        NotificationCenter.default.addObserver(
            self,
            selector: #selector(handleRouteChange),
            name: AVAudioSession.routeChangeNotification,
            object: nil
        )

        updateCurrentRoute()
    }

    func configureAudioSession() {
        do {
            let session = AVAudioSession.sharedInstance()

            try session.setCategory(
                .playback,
                mode: .default,
                options: [.allowBluetoothA2DP, .allowAirPlay]
            )

            try session.setActive(true)
            updateCurrentRoute()

        } catch {
            errorMessage = "Failed to configure audio session: \(error.localizedDescription)"
            playbackStatus = "Audio Session Error"
            print(errorMessage)
        }
    }

    func loadStream(from urlString: String) {
        guard let url = URL(string: urlString) else {
            errorMessage = "Invalid stream URL"
            playbackStatus = "Invalid Stream URL"
            return
        }

        configureAudioSession()

        let playerItem = AVPlayerItem(url: url)

        playerItem.addObserver(
            self,
            forKeyPath: "playbackBufferEmpty",
            options: .new,
            context: nil
        )

        playerItem.addObserver(
            self,
            forKeyPath: "playbackLikelyToKeepUp",
            options: .new,
            context: nil
        )

        NotificationCenter.default.addObserver(
            self,
            selector: #selector(playerItemDidReachEnd),
            name: .AVPlayerItemDidPlayToEndTime,
            object: playerItem
        )

        player = AVPlayer(playerItem: playerItem)
        player?.automaticallyWaitsToMinimizeStalling = true
        player?.volume = volume

        isPlaying = false
        isBuffering = false
        playbackStatus = "Stream Loaded"
        errorMessage = ""

        play()
    }

    func play() {
        guard let player = player else {
            errorMessage = "No audio stream loaded"
            playbackStatus = "No Stream Loaded"
            return
        }

        player.play()
        isPlaying = true
        playbackStatus = "Playing"
        updateCurrentRoute()
    }

    func pause() {
        player?.pause()
        isPlaying = false
        playbackStatus = "Paused"
    }

    func stop() {
        player?.pause()
        player?.seek(to: .zero)
        isPlaying = false
        playbackStatus = "Stopped"
    }

    func setVolume(_ newVolume: Float) {
        volume = newVolume
        player?.volume = newVolume
    }

    @objc func playerItemDidReachEnd(notification: Notification) {
        playbackStatus = "Playback Finished"
        isPlaying = false
    }

    override func observeValue(
        forKeyPath keyPath: String?,
        of object: Any?,
        change: [NSKeyValueChangeKey : Any]?,
        context: UnsafeMutableRawPointer?
    ) {
        if keyPath == "playbackBufferEmpty" {
            DispatchQueue.main.async {
                self.isBuffering = true
                self.playbackStatus = "Buffering..."
            }
        }

        if keyPath == "playbackLikelyToKeepUp" {
            DispatchQueue.main.async {
                self.isBuffering = false

                if self.isPlaying {
                    self.playbackStatus = "Playing"
                }
            }
        }
    }

    @objc func handleRouteChange(notification: Notification) {
        updateCurrentRoute()
    }

    func updateCurrentRoute() {
        let session = AVAudioSession.sharedInstance()
        let outputs = session.currentRoute.outputs

        if let output = outputs.first {
            currentOutput = output.portName
        } else {
            currentOutput = "No Device Connected"
        }
    }
}

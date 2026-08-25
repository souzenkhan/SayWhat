import Foundation
import Combine

struct VenueSession: Identifiable, Equatable, Codable {
    let id: String
    let venueName: String
    let streamURL: URL
}

final class AppState: ObservableObject {
    private static let recentSessionsKey = "sayWhat.recentVenueSessions"
    private static let maximumRecentSessions = 50
    private let defaults: UserDefaults

    @Published var isConnected: Bool = false
    @Published var streamURLString: String = ""
    @Published var venueName: String = ""
    @Published var connectionStatus: String = "Not Connected To Venue's Audio Stream"
    @Published var currentSession: VenueSession?
    @Published var recentSessions: [VenueSession] = []

    init(defaults: UserDefaults = .standard) {
        self.defaults = defaults

        guard let data = defaults.data(forKey: Self.recentSessionsKey),
              let savedSessions = try? JSONDecoder().decode([VenueSession].self, from: data) else {
            return
        }
        recentSessions = Array(savedSessions.prefix(Self.maximumRecentSessions))
    }

    @discardableResult
    func connect(using payload: String) -> VenueSession? {
        guard let session = Self.parseSession(payload) else { return nil }

        currentSession = session
        venueName = session.venueName
        streamURLString = session.streamURL.absoluteString
        isConnected = true
        connectionStatus = "Connected To Venue's Audio Stream"
        recentSessions.removeAll { $0.id == session.id }
        recentSessions.insert(session, at: 0)
        recentSessions = Array(recentSessions.prefix(Self.maximumRecentSessions))
        persistRecentSessions()
        return session
    }

    private func persistRecentSessions() {
        guard let data = try? JSONEncoder().encode(recentSessions) else { return }
        defaults.set(data, forKey: Self.recentSessionsKey)
    }

    private static func parseSession(_ payload: String) -> VenueSession? {
        let value = payload.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !value.isEmpty else { return nil }

        if let data = value.data(using: .utf8),
           let object = try? JSONSerialization.jsonObject(with: data),
           let json = object as? [String: Any],
           let streamValue = (json["streamUrl"] ?? json["stream_url"]) as? String,
           let streamURL = normalizedStreamURL(streamValue) {
            let name = (json["venueName"] ?? json["venue_name"] ?? json["venue"]) as? String
            return VenueSession(
                id: (json["sessionId"] ?? json["session_id"]) as? String ?? streamURL.absoluteString,
                venueName: normalizedVenueName(name, streamURL: streamURL),
                streamURL: streamURL
            )
        }

        guard let suppliedURL = normalizedStreamURL(value),
              let components = URLComponents(url: suppliedURL, resolvingAgainstBaseURL: false) else {
            return nil
        }

        let query = (components.queryItems ?? []).reduce(into: [String: String]()) { values, item in
            if values[item.name] == nil {
                values[item.name] = item.value ?? ""
            }
        }
        let streamValue = query["streamUrl"] ?? query["stream_url"] ?? query["stream"]
        let streamURL = streamValue.flatMap { normalizedStreamURL($0) } ?? suppliedURL
        let name = query["venueName"] ?? query["venue_name"] ?? query["venue"] ?? query["name"]

        return VenueSession(
            id: query["sessionId"] ?? query["session_id"] ?? streamURL.absoluteString,
            venueName: normalizedVenueName(name, streamURL: streamURL),
            streamURL: streamURL
        )
    }

    private static func normalizedVenueName(_ value: String?, streamURL: URL) -> String {
        if let value = value?.trimmingCharacters(in: .whitespacesAndNewlines), !value.isEmpty {
            return value
        }
        return streamURL.host?.replacingOccurrences(of: "www.", with: "") ?? "Venue audio session"
    }

    private static func normalizedStreamURL(_ value: String) -> URL? {
        let streamValue = value.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !streamValue.isEmpty else { return nil }
        if let url = URL(string: streamValue), url.scheme != nil {
            return url
        }
        return URL(string: "https://\(streamValue)")
    }
}

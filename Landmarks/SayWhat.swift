import SwiftUI

struct SayWhat: View {
    @EnvironmentObject var appState: AppState

    var body: some View {
        VStack(spacing: 0) {

            // Top bar
            HStack {
                Image(systemName: "ear")
                    .foregroundColor(AppTheme.blue)

                Spacer()

                NavigationLink(destination: About()) {
                    Text("Say What?")
                        .font(.headline)
                        .fontWeight(.bold)
                        .foregroundColor(AppTheme.blue)
                        .padding(.horizontal, 10)
                        .padding(.vertical, 4)
                        .background(AppTheme.blue.opacity(0.08))
                        .cornerRadius(8)
                }

                Spacer()

                Image(systemName: "gearshape")
                    .foregroundColor(AppTheme.blue)
            }
            .padding()
            .background(Color.white)

            ScrollView {
                VStack(spacing: 28) {

                    Text("Hearing clearly\nshouldn't be a luxury.")
                        .font(.system(size: 29, weight: .bold))
                        .multilineTextAlignment(.center)
                        .foregroundColor(AppTheme.blue)
                        .padding(.top, 30)

                    Text("Say What? was born from a simple observation: public spaces are often designed for aesthetics, not acoustics. Our mission is to bridge the communication gap for those with hearing challenges using advanced real-time audio processing.")
                        .font(.body)
                        .multilineTextAlignment(.center)
                        .foregroundColor(AppTheme.text)
                        .lineSpacing(5)
                        .padding(.horizontal, 20)

                    Image("hearingMan")
                        .resizable()
                        .aspectRatio(1, contentMode: .fit)
                        .frame(maxWidth: .infinity)
                        .background(Color.white)
                        .cornerRadius(14)
                        .padding(.horizontal, 24)

                    Text("Ready to Listen?")
                        .font(.system(size: 25, weight: .bold))
                        .foregroundColor(AppTheme.text)

                    NavigationLink(destination: Setup()) {
                        HStack {
                            Image(systemName: "qrcode.viewfinder")
                            Text("SCAN QR CODE")
                                .fontWeight(.bold)
                        }
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(AppTheme.blue)
                        .cornerRadius(8)
                        .padding(.horizontal, 24)
                    }

                    NavigationLink(destination: About()) {
                        HStack(spacing: 6) {
                            Image(systemName: "questionmark.circle")
                            Text("About Say What?")
                        }
                        .font(.system(size: 14, weight: .medium))
                        .foregroundColor(AppTheme.blue)
                    }

                    Spacer(minLength: 30)
                }
            }
            .background(AppTheme.background)
            .frame(maxWidth: .infinity, maxHeight: .infinity)

            BottomNavBar(selectedTab: .home)
        }
        .background(AppTheme.background)
        .navigationBarHidden(true)
    }
}

struct VenueRow: View {
    let title: String
    let subtitle: String
    let icon: String

    var body: some View {
        HStack {
            Image(systemName: icon)
                .foregroundColor(AppTheme.blue)
                .frame(width: 45, height: 45)
                .background(AppTheme.blue.opacity(0.10))
                .cornerRadius(10)

            VStack(alignment: .leading, spacing: 4) {
                Text(title)
                    .font(.subheadline)
                    .fontWeight(.bold)
                    .foregroundColor(AppTheme.text)

                Text(subtitle)
                    .font(.caption)
                    .foregroundColor(.gray)
            }

            Spacer()

            Image(systemName: "chevron.right")
                .font(.caption)
                .foregroundColor(AppTheme.blue)
        }
        .padding()
        .background(Color.white)
        .cornerRadius(10)
        .shadow(color: Color.black.opacity(0.05), radius: 5, x: 0, y: 3)
    }
}

struct SayWhat_Previews: PreviewProvider {
    static var previews: some View {
        SayWhat()
            .environmentObject(AppState())
    }
}

struct WelcomeView: View {
    var body: some View {
        ZStack(alignment: .bottom) {
            AppTheme.background.ignoresSafeArea()

            RoundedRectangle(cornerRadius: 70)
                .fill(AppTheme.blue.opacity(0.08))
                .frame(height: 72)
                .offset(y: 34)

            VStack(spacing: 0) {
                Spacer()

                ZStack {
                    Circle()
                        .fill(Color.white)
                        .frame(width: 82, height: 82)

                    Image(systemName: "waveform.circle")
                        .font(.system(size: 46))
                        .foregroundColor(AppTheme.blue)
                }

                Spacer().frame(height: 34)

                Text("Welcome to")
                    .font(.title3)
                    .foregroundColor(AppTheme.text)

                Text("Say What!")
                    .font(.system(size: 36, weight: .bold))
                    .foregroundColor(AppTheme.blue)

                Spacer().frame(height: 22)

                Text("Click here to take a brief\ntour of the app!")
                    .font(.body)
                    .multilineTextAlignment(.center)
                    .foregroundColor(AppTheme.text)
                    .lineSpacing(5)

                Spacer().frame(height: 38)

                NavigationLink(destination: SayWhat()) {
                    HStack {
                        Spacer()
                        Text("Take the Tour")
                            .font(.headline)
                            .fontWeight(.bold)
                        Image(systemName: "chevron.right")
                        Spacer()
                    }
                    .foregroundColor(.white)
                    .frame(height: 56)
                    .background(AppTheme.blue)
                    .cornerRadius(8)
                }
                .padding(.horizontal, 30)

                NavigationLink(destination: SayWhat()) {
                    Text("Skip for now")
                        .font(.subheadline)
                        .foregroundColor(.gray)
                        .padding(20)
                }

                Spacer()
            }
        }
        .navigationBarHidden(true)
    }
}

struct RecentVenuesView: View {
    @EnvironmentObject var appState: AppState

    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Image(systemName: "ear")
                    .foregroundColor(AppTheme.blue)
                Spacer()
                Text("Say What?")
                    .font(.title2)
                    .fontWeight(.bold)
                    .foregroundColor(AppTheme.blue)
                Spacer()
                Image(systemName: "gearshape")
                    .foregroundColor(AppTheme.blue)
            }
            .padding()
            .background(Color.white)

            ScrollView {
                VStack(alignment: .leading, spacing: 14) {
                    HStack {
                        Text("Recent Venues")
                            .font(.title2)
                            .fontWeight(.bold)
                        Spacer()
                        Text("View all")
                            .font(.caption)
                            .foregroundColor(AppTheme.blue)
                    }

                    if appState.recentSessions.isEmpty {
                        Text("Your connected venues will appear here.")
                            .font(.body)
                            .foregroundColor(.gray)
                            .padding(.vertical, 28)
                    } else {
                        ForEach(appState.recentSessions) { session in
                            VenueRow(
                                title: session.venueName,
                                subtitle: "Connected session",
                                icon: "building.2"
                            )
                        }
                    }
                }
                .padding(20)
            }
            .background(AppTheme.background)

            BottomNavBar(selectedTab: .recent)
        }
        .navigationBarHidden(true)
    }
}

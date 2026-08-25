//
//  ListeningView.swift
//  Landmarks
//
//  Created by Maryam Bouguerrra on 4/19/26.
//  Copyright © 2026 Ken Samel. All rights reserved.
//

import SwiftUI

struct ListeningView: View {
    
    @EnvironmentObject var audioManager: AudioManager
    @EnvironmentObject var appState: AppState
    
    @State private var waveformHeights: [CGFloat] = [40, 70, 60, 80, 55, 35, 90, 45, 65, 30]
    @State private var showEndConfirmation = false
    @State private var sessionEnded = false
    
    var body: some View {
        VStack(spacing: 0) {
            
            // Top Bar
            HStack {
                Image(systemName: "ear")
                    .foregroundColor(AppTheme.blue)
                
                Spacer()
                
                Text("Say What?")
                    .font(.title2)
                    .fontWeight(.bold)
                    .foregroundColor(AppTheme.blue)
                    .padding(.horizontal, 10)
                    .padding(.vertical, 4)
                    .background(AppTheme.blue.opacity(0.08))
                    .cornerRadius(8)
                
                Spacer()
                
                Image(systemName: "gearshape")
                    .foregroundColor(AppTheme.blue)
            }
            .padding()
            .background(Color.white)
            
            // Main Audio Content
            ScrollView {
                VStack(spacing: 22) {
                    Text("Connected!")
                        .font(.system(size: 28, weight: .bold))
                        .foregroundColor(AppTheme.text)
                        .frame(maxWidth: .infinity, alignment: .leading)
                    
                    // Live Session Card
                    VStack(alignment: .leading, spacing: 12) {
                        HStack {
                            Text("● LIVE")
                                .font(.caption)
                                .fontWeight(.bold)
                                .foregroundColor(.white)
                                .padding(.horizontal, 12)
                                .padding(.vertical, 8)
                                .background(Color.red)
                                .cornerRadius(20)
                            
                            Text("Session Active")
                                .font(.headline)
                                .foregroundColor(AppTheme.text)
                            
                            Spacer()
                        }
                        
                        Text(appState.currentSession?.venueName ?? "Venue audio session")
                            .font(.largeTitle)
                            .fontWeight(.bold)
                            .foregroundColor(AppTheme.text)
                        
                        HStack {
                            Image(systemName: "clock")
                            Text(audioManager.playbackStatus)
                        }
                        .font(.subheadline)
                        .foregroundColor(.gray)
                        
                        // Animated Waveform
                        HStack(alignment: .bottom, spacing: 8) {
                            ForEach(waveformHeights.indices, id: \.self) { index in
                                RoundedRectangle(cornerRadius: 4)
                                    .fill(AppTheme.blue)
                                    .frame(width: 10, height: waveformHeights[index])
                                    .animation(.easeInOut(duration: 0.25), value: waveformHeights[index])
                            }
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(AppTheme.background)
                        .cornerRadius(12)
                        .onReceive(
                            Timer.publish(every: 0.35, on: .main, in: .common).autoconnect()
                        ) { _ in
                            if audioManager.isPlaying {
                                waveformHeights = waveformHeights.map { _ in
                                    CGFloat.random(in: 20...95)
                                }
                            }
                        }
                    }
                    .padding()
                    .background(Color.white)
                    .cornerRadius(14)
                    .overlay(
                        RoundedRectangle(cornerRadius: 14)
                            .stroke(Color.gray.opacity(0.25), lineWidth: 1)
                    )
                    .shadow(color: Color.black.opacity(0.06), radius: 6)
                    .padding(.horizontal)
                    
                    NavigationLink(destination: TranslationView()) {
                        Text("Translate")
                            .font(.system(size: 25, weight: .bold))
                            .foregroundColor(.white)
                            .padding(.horizontal, 22)
                            .frame(height: 58)
                            .background(AppTheme.blue)
                            .cornerRadius(8)
                    }

                    Button(action: { showEndConfirmation = true }) {
                        Text("End Session")
                            .font(.system(size: 25, weight: .bold))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 58)
                            .background(AppTheme.blue)
                            .cornerRadius(8)
                    }
                    .padding(.horizontal)

                    NavigationLink(destination: Setup()) {
                        Text("Back")
                            .fontWeight(.bold)
                            .foregroundColor(.white)
                            .padding(.horizontal, 34)
                            .frame(height: 42)
                            .background(Color(red: 0.31, green: 0.41, blue: 0.79))
                            .cornerRadius(7)
                    }

                    NavigationLink(destination: SayWhat(), isActive: $sessionEnded) {
                        EmptyView()
                    }
                    
                    // Status Info
                    VStack(spacing: 8) {
                        Text("Connection: \(appState.connectionStatus)")
                            .font(.caption)
                            .foregroundColor(.gray)
                        
                        if audioManager.isBuffering {
                            Text("Buffering...")
                                .font(.caption)
                                .foregroundColor(.orange)
                        }
                    }
                    .padding(.bottom, 20)
                }
                .padding(.top, 25)
            }
            .background(AppTheme.background)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            
            BottomNavBar(selectedTab: .scan)
        }
        .navigationBarHidden(true)
        .alert(isPresented: $showEndConfirmation) {
            Alert(
                title: Text("Are you sure you want to end the session?"),
                primaryButton: .destructive(Text("Yes")) {
                    audioManager.stop()
                    appState.isConnected = false
                    appState.connectionStatus = "Not Connected To Venue's Audio Stream"
                    sessionEnded = true
                },
                secondaryButton: .cancel(Text("Cancel"))
            )
        }
    }
}

struct TranslationView: View {
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
                VStack(spacing: 14) {
                    VStack(alignment: .leading, spacing: 9) {
                        HStack {
                            Text("● LIVE")
                                .font(.caption)
                                .fontWeight(.bold)
                                .foregroundColor(.white)
                                .padding(.horizontal, 10)
                                .padding(.vertical, 6)
                                .background(Color.red)
                                .cornerRadius(16)
                            Text("Session Active")
                            Spacer()
                            Image(systemName: "bookmark")
                                .foregroundColor(AppTheme.blue)
                        }
                        Text("\(appState.currentSession?.venueName ?? "Venue audio session") ↗")
                            .font(.title2)
                            .fontWeight(.bold)
                        Text("◷  00:42:17")
                            .font(.caption)
                            .foregroundColor(.gray)
                    }
                    .padding()
                    .background(Color.white)
                    .cornerRadius(8)

                    TranslationCard(
                        language: "English (US)",
                        copy: "Out, damned spot! Out, I say! One, two—why, then ‘tis time to do't."
                    )

                    Image(systemName: "arrow.down")
                        .foregroundColor(.white)
                        .frame(width: 42, height: 42)
                        .background(AppTheme.blue)
                        .clipShape(Circle())

                    TranslationCard(
                        language: "Spanish (Spain)",
                        copy: "¡Fuera, maldita mancha! ¡Fuera, te digo! Una, dos... bien, entonces es hora de hacerlo."
                    )

                    NavigationLink(destination: ListeningView()) {
                        Text("Back")
                            .fontWeight(.bold)
                            .foregroundColor(.white)
                            .padding(.horizontal, 34)
                            .frame(height: 42)
                            .background(Color(red: 0.31, green: 0.41, blue: 0.79))
                            .cornerRadius(7)
                    }
                    .padding(.top, 16)
                }
                .padding(20)
            }
            .background(AppTheme.background)
        }
        .navigationBarHidden(true)
    }
}

private struct TranslationCard: View {
    let language: String
    let copy: String

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(language)
                .font(.title3)
                .fontWeight(.bold)
                .foregroundColor(AppTheme.blue)
            Text(copy)
                .font(.body)
                .fontWeight(.semibold)
                .foregroundColor(.gray)
                .lineSpacing(4)
            Spacer()
        }
        .padding()
        .frame(maxWidth: .infinity, minHeight: 170, alignment: .leading)
        .background(Color.white)
        .cornerRadius(8)
    }
}

struct ListeningView_Previews: PreviewProvider {
    static var previews: some View {
        ListeningView()
            .environmentObject(AppState())
            .environmentObject(AudioManager())
    }
}

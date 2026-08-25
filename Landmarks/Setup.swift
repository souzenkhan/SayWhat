//
//  Setup.swift
//  Landmarks
//
//  Created by Ken Samel on 3/25/2023.
//  Copyright © 2023 Ken Samel. All rights reserved.
//
//  Wednesday, April 8, 2026 @ 12:20:22 
//

import SwiftUI

struct Setup: View {
    @EnvironmentObject var appState: AppState
    @EnvironmentObject var audioManager: AudioManager

    @State private var showURLField = false
    @State private var streamURL = ""

    var body: some View {
        VStack(spacing: 0) {

            // Top bar
            HStack {
                Image(systemName: "ear")
                    .foregroundColor(AppTheme.blue)

                Spacer()

                Text("Say What?")
                    .font(.title2)
                    .bold()
                    .foregroundColor(AppTheme.blue)

                Spacer()

                Image(systemName: "gearshape")
                    .foregroundColor(AppTheme.blue)
            }
            .padding()
            .background(Color.white)

            ScrollView {
                VStack(spacing: 20) {

                    Text("Connect to Venue\nAudio")
                        .font(.title)
                        .bold()
                        .multilineTextAlignment(.center)
                        .foregroundColor(AppTheme.text)

                    VStack(spacing: 24) {
                        ZStack {
                            Circle()
                                .fill(Color.blue.opacity(0.08))
                                .frame(width: 90, height: 90)

                            Image(systemName: "qrcode.viewfinder")
                                .font(.system(size: 42))
                                .foregroundColor(AppTheme.blue)
                        }

                        NavigationLink(destination: QRScannerView()) {
                            HStack {
                                Image(systemName: "camera")
                                Text("Scan Venue QR Code")
                            }
                            .font(.headline)
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(AppTheme.blue)
                            .cornerRadius(10)
                        }

                        Text("Look for a “Say What?” poster at the counter or on your table.")
                            .font(.caption)
                            .multilineTextAlignment(.center)
                            .foregroundColor(AppTheme.text)

                        Divider()

                        NavigationLink(destination: Help()) {
                            HStack {
                                Image(systemName: "questionmark.circle")
                                Text("Need help connecting?")
                            }
                            .font(.caption)
                            .foregroundColor(AppTheme.blue)
                        }
                    }
                    .padding(28)
                    .background(Color.white)
                    .cornerRadius(14)
                    .shadow(color: Color.black.opacity(0.05), radius: 8)
                    .padding(.horizontal)

                    Button(action: {
                        showURLField.toggle()
                    }) {
                        Text("Connect Using URL")
                            .bold()
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(AppTheme.blue)
                            .cornerRadius(10)
                    }
                    .padding(.horizontal)

                    if showURLField {
                        TextField("Type URL", text: $streamURL)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                            .disableAutocorrection(true)
                            .autocapitalization(.none)
                            .padding(.horizontal)

                        Button("Enter") {
                            if let session = appState.connect(using: streamURL) {
                                audioManager.loadStream(from: session.streamURL.absoluteString)
                            }
                        }
                        .font(.headline)
                        .foregroundColor(.white)
                        .padding(.horizontal, 20)
                        .padding(.vertical, 8)
                        .background(AppTheme.blue)
                        .cornerRadius(10)
                    }
                }
                .padding(.top, 20)
            }
            .background(AppTheme.background)
            .frame(maxWidth: .infinity, maxHeight: .infinity)

            BottomNavBar(selectedTab: .scan)
        }
        .navigationBarHidden(true)
    }
}

struct Setup_Previews: PreviewProvider {
    static var previews: some View {
        Setup()
    }
}

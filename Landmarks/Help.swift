//
//  Help.swift
//  Landmarks
//
//  Created by Ken Samel on 3/25/2023.
//  Copyright © 2023 Ken Samel. All rights reserved.
//
//  Wednesday, April 8, 2026 @ 12:20:34 
//

import SwiftUI
import UIKit

struct Help: View {
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

                Spacer()

                Image(systemName: "gearshape")
                    .foregroundColor(AppTheme.blue)
            }
            .padding()
            .background(Color.white)

            ScrollView {
                VStack(alignment: .leading, spacing: 24) {

                    Text("Help & Support")
                        .font(.title2)
                        .fontWeight(.bold)
                        .foregroundColor(AppTheme.text)
                        .padding(.top, 35)

                    Text("Find answers to common questions and troubleshooting tips for your hearing assistance experience.")
                        .font(.body)
                        .foregroundColor(AppTheme.text)
                        .lineSpacing(5)

                    Button(action: {
                        if let url = URL(string: UIApplication.openSettingsURLString) {
                            UIApplication.shared.open(url)
                        }
                    }) {
                        Text("CLICK TO GO TO THE SETTINGS APP")
                            .font(.caption)
                            .fontWeight(.bold)
                            .underline()
                            .foregroundColor(AppTheme.text)
                    }
                    .frame(maxWidth: .infinity)

                    VStack(alignment: .leading, spacing: 22) {
                        Label("How to Connect", systemImage: "antenna.radiowaves.left.and.right")
                            .font(.headline)
                            .foregroundColor(AppTheme.blue)

                        HelpStep(
                            number: "1",
                            title: "JOIN WI-FI",
                            description: "Go to Settings, open Wi-Fi, and connect to the venue’s public network."
                        )

                        HelpStep(
                            number: "2",
                            title: "SCAN QR CODE",
                            description: "Open Say What?, tap Connect, and scan the venue QR code."
                        )

                        HelpStep(
                            number: "3",
                            title: "START LISTENING",
                            description: "Go to the Audio screen and press Play to begin listening."
                        )
                    }
                    .padding(24)
                    .background(AppTheme.blue.opacity(0.10))
                    .cornerRadius(14)

                    VStack(alignment: .leading, spacing: 14) {
                        Image(systemName: "wifi.slash")
                            .foregroundColor(.red)
                            .padding(10)
                            .background(Color.red.opacity(0.08))
                            .clipShape(Circle())

                        Text("Connection Lost")
                            .font(.title3)
                            .fontWeight(.bold)
                            .foregroundColor(AppTheme.text)

                        Text("If the stream stops or cannot connect, open the connection lost screen for troubleshooting steps.")
                            .font(.caption)
                            .foregroundColor(AppTheme.text)

                        NavigationLink(destination: ConnectionLostView()) {
                            Text("View Connection Fixes")
                                .fontWeight(.bold)
                                .foregroundColor(.white)
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(AppTheme.blue)
                                .cornerRadius(20)
                        }
                    }
                    .padding(24)
                    .background(Color.white)
                    .cornerRadius(14)
                    .overlay(
                        RoundedRectangle(cornerRadius: 14)
                            .stroke(AppTheme.blue.opacity(0.18), lineWidth: 1)
                    )
                    .shadow(color: Color.black.opacity(0.05), radius: 6)

                    Spacer(minLength: 30)
                }
                .padding(.horizontal, 22)
            }
            .background(AppTheme.background)
            .frame(maxWidth: .infinity, maxHeight: .infinity)

            BottomNavBar(selectedTab: .help)
        }
        .navigationBarHidden(true)
    }
}

struct HelpStep: View {
    let number: String
    let title: String
    let description: String

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            Text(number)
                .font(.headline)
                .fontWeight(.bold)
                .foregroundColor(.white)
                .frame(width: 36, height: 36)
                .background(AppTheme.blue)
                .clipShape(Circle())

            Text(title)
                .font(.caption)
                .fontWeight(.bold)
                .foregroundColor(AppTheme.text)

            Text(description)
                .font(.caption)
                .foregroundColor(AppTheme.text)
                .lineSpacing(4)
        }
    }
}

struct Help_Previews: PreviewProvider {
    static var previews: some View {
        Help()
    }
}

//
//  ConnectionLostView.swift
//  Landmarks
//
//  Created by Maryam Bouguerrra on 6/5/26.
//  Copyright © 2026 Ken Samel. All rights reserved.
//

import SwiftUI
import UIKit

struct ConnectionLostView: View {
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
                VStack(spacing: 28) {

                    Image(systemName: "wifi.slash")
                        .font(.system(size: 70))
                        .foregroundColor(.red)
                        .padding(.top, 45)

                    Text("Connection Lost")
                        .font(.title)
                        .fontWeight(.bold)
                        .foregroundColor(AppTheme.text)

                    Text("We're having trouble connecting to the audio stream in this venue.")
                        .font(.title3)
                        .multilineTextAlignment(.center)
                        .foregroundColor(.gray)
                        .lineSpacing(6)
                        .padding(.horizontal, 35)

                    Button(action: {
                        if let url = URL(string: UIApplication.openSettingsURLString) {
                            UIApplication.shared.open(url)
                        }
                    }) {
                        Text("CLICK TO GO TO THE SETTINGS APP")
                            .font(.headline)
                            .fontWeight(.bold)
                            .underline()
                            .foregroundColor(AppTheme.text)
                    }
                    .padding(.top, 10)

                    VStack(alignment: .leading, spacing: 22) {
                        Text("HOW TO FIX THIS")
                            .font(.headline)
                            .fontWeight(.bold)
                            .foregroundColor(AppTheme.blue)

                        FixRow(
                            icon: "wifi",
                            title: "Check your Wi-Fi",
                            description: "Ensure you are connected to the venue's public network."
                        )

                        FixRow(
                            icon: "location.north.fill",
                            title: "Stay in range",
                            description: "Try moving closer to the stage or service counter."
                        )

                        FixRow(
                            icon: "antenna.radiowaves.left.and.right",
                            title: "Reset connection",
                            description: "Turn your Wi-Fi off and back on again in Settings."
                        )
                    }
                    .padding(24)
                    .background(Color.white)
                    .cornerRadius(14)
                    .shadow(color: Color.black.opacity(0.06), radius: 8)
                    .padding(.horizontal, 24)

                    NavigationLink(destination: Setup()) {
                        HStack {
                            Image(systemName: "arrow.clockwise")
                            Text("Try Again")
                                .fontWeight(.bold)
                        }
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(AppTheme.blue)
                        .cornerRadius(10)
                    }
                    .padding(.horizontal, 24)

                    NavigationLink(destination: Help()) {
                        HStack {
                            Image(systemName: "questionmark.circle")
                            Text("Go to Help")
                                .fontWeight(.bold)
                        }
                        .foregroundColor(AppTheme.blue)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .overlay(
                            RoundedRectangle(cornerRadius: 10)
                                .stroke(AppTheme.blue, lineWidth: 1.5)
                        )
                    }
                    .padding(.horizontal, 24)

                    Spacer(minLength: 40)
                }
            }
            .background(AppTheme.background)
            .frame(maxWidth: .infinity, maxHeight: .infinity)

            BottomNavBar(selectedTab: .home)
        }
        .navigationBarHidden(true)
    }
}

struct FixRow: View {
    let icon: String
    let title: String
    let description: String

    var body: some View {
        HStack(alignment: .top, spacing: 15) {
            Image(systemName: icon)
                .foregroundColor(AppTheme.blue)
                .frame(width: 38, height: 38)
                .background(AppTheme.blue.opacity(0.10))
                .clipShape(Circle())

            VStack(alignment: .leading, spacing: 4) {
                Text(title)
                    .fontWeight(.bold)
                    .foregroundColor(AppTheme.text)

                Text(description)
                    .font(.subheadline)
                    .foregroundColor(.gray)
                    .lineSpacing(3)
            }
        }
    }
}

struct ConnectionLostView_Previews: PreviewProvider {
    static var previews: some View {
        ConnectionLostView()
    }
}

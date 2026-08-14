//
//  BottomNavBar.swift
//  Landmarks
//
//  Created by Maryam Bouguerrra on 6/2/26.
//  Copyright © 2026 Ken Samel. All rights reserved.
//

import SwiftUI

enum Tab {
    case home
    case scan
    case audio
    case recent
    case help
}

struct BottomNavBar: View {

    let selectedTab: Tab

    var body: some View {

        HStack {

            Spacer()

            NavigationLink(destination: SayWhat()) {
                VStack(spacing: 4) {
                    Image(systemName: "house")
                        .font(.system(size: 18))

                    Text("Home")
                        .font(.caption2)
                }
                .foregroundColor(
                    selectedTab == .home
                    ? AppTheme.blue
                    : .gray
                )
            }

            Spacer()

            NavigationLink(destination: Setup()) {
                VStack(spacing: 4) {
                    Image(systemName: "qrcode.viewfinder")
                        .font(.system(size: 18))

                    Text("Connect")
                        .font(.caption2)
                }
                .foregroundColor(
                    selectedTab == .scan
                    ? AppTheme.blue
                    : .gray
                )
            }

            Spacer()

            NavigationLink(destination: RecentVenuesView()) {
                VStack(spacing: 4) {
                    Image(systemName: "clock.arrow.circlepath")
                        .font(.system(size: 18))

                    Text("Recent")
                        .font(.caption2)
                }
                .foregroundColor(
                    selectedTab == .recent
                    ? AppTheme.blue
                    : .gray
                )
            }

            Spacer()

            NavigationLink(destination: Help()) {
                VStack(spacing: 4) {
                    Image(systemName: "questionmark.circle")
                        .font(.system(size: 18))

                    Text("Help")
                        .font(.caption2)
                }
                .foregroundColor(
                    selectedTab == .help
                    ? AppTheme.blue
                    : .gray
                )
            }

            Spacer()
        }
        .padding(.vertical, 12)
        .background(Color.white)
    }
}

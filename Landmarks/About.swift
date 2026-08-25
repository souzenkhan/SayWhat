//
//  About.swift
//  Landmarks
//
//  Created by Ken Samel on 3/25/2023.
//  Copyright © 2023 Ken Samel. All rights reserved.
//
//  Wednesday, April 8, 2026 @ 12:20:53 
//

import SwiftUI

struct About: View {
    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Image(systemName: "ear")
                    .font(.title2)
                    .foregroundColor(AppTheme.blue)

                Spacer()

                Text("Say What?")
                    .font(.title2)
                    .bold()
                    .foregroundColor(AppTheme.blue)

                Spacer()

                Image(systemName: "gearshape")
                    .font(.title2)
                    .foregroundColor(AppTheme.blue)
            }
            .padding()
            .background(Color.white)

            ScrollView {
                VStack(spacing: 30) {
                    FeatureCard(
                        icon: "ear",
                        title: "Clarity First",
                        description: "Prioritizing the spoken word through intelligent noise suppression technology."
                    )

                    FeatureCard(
                        icon: "person.2.rectangle.stack",
                        title: "Universal Design",
                        description: "Accessible by everyone, regardless of age or technical ability. Simple by default."
                    )

                    FeatureCard(
                        icon: "shield.fill",
                        title: "Privacy Locked",
                        description: "All audio processing happens locally on your device."
                    )
                }
                .padding(.top, 45)
            }
            .background(AppTheme.background)

            BottomNavBar(selectedTab: .home)
        }
        .navigationBarHidden(true)
    }
}

struct About_Previews: PreviewProvider {
    static var previews: some View {
        About()
    }
//    print("Leaving About")

}

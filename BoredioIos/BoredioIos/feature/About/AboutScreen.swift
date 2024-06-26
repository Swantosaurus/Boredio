//
//  AboutScreen.swift
//  BoredioIos
//
//  Created by Václav Kobera on 26.06.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared

struct AboutScreen: View {
    //@State var isExpandedLicense: Bo
    //@State var isExpandedDataSources
    //@State var isExpandedPrivacyPolicy
    @State var viewModel: AboutViewModel? = nil
    
    var totalWidth = UIScreen.main.bounds.width
    var body: some View {
        ScrollView {
            VStack(alignment:.leading){
                DisclosureGroup {
                    Text(NSLocalizedString("aboutScreenLicenseText", comment: ""))
                } label: {
                    Text(NSLocalizedString("aboutScreenLicenseTitle", comment: ""))
                        .font(.system(size: 24))
                }
                .frame(width: totalWidth - 32)
                
                dataSources
                
                DisclosureGroup {
                    Text(NSLocalizedString("aboutScreenPrivacyPolicyText", comment: ""))
                        .padding()
                } label: {
                    Text(NSLocalizedString("aboutScreenPrivacyPolicyTitle", comment: ""))
                        .font(.system(size: 24))
                }
                .frame(width: totalWidth - 32)
            }
            Spacer()
        }
        .task {
            let vm = KotlinDependencies.shared.getAboutViewModel()
            self.viewModel = vm
        }
    }
    
    var dataSources: some View {
        DisclosureGroup {
            VStack(alignment: .leading){
                let boredUrl = URL(string: viewModel?.inAppUrls.boredApi ?? "")
                getSourceTitle(text: NSLocalizedString("aboutScreenBoredApiTitle", comment: ""), url: boredUrl)
                Text(NSLocalizedString("aboutScreenBoredApiText", comment: ""))
                
                let dalleUrl = URL(string: viewModel?.inAppUrls.dalleApi ?? "")
                getSourceTitle(text: NSLocalizedString("aboutScreenOpenAiDalleTitle", comment: ""), url: dalleUrl)
                Text(NSLocalizedString("aboutScreenOpenAiDalleText", comment: ""))
                
                let googleCloudUrl = URL(string: viewModel?.inAppUrls.googleCould ?? "")
                getSourceTitle(text: NSLocalizedString("aboutScreenGoogleAPITitle", comment: ""), url: googleCloudUrl)
                Text(NSLocalizedString("aboutScreenGoogleAPIText", comment: ""))
            }
            .frame(width: totalWidth - 48)
        } label: {
            Text(NSLocalizedString("aboutScreenDataSourcesTitle", comment: ""))
                .font(.system(size: 24))
        }
        .frame(width: totalWidth - 32)
    }
    
    
    func getSourceTitle(text: String, url: URL?) -> some View {
        ZStack {
            if(url != nil) {
                Link(text, destination: url!)
            } else {
                Text(text)
            }
        }
        .font(.system(size: 20))
        
        .padding(.top, 8)
        .padding(.bottom, 2)
    }
}


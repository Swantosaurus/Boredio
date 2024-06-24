//
//  DailyFeedScreen.swift
//  BoredioIos
//
//  Created by Václav Kobera on 24.06.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared
import SwiftUI


struct DailyFeedScreen: View {
    @State var dailyFeedViewModel: DailyFeedViewModel?
    
    @State var dailyFeedState: DailyFeedState?
    
    @State var rerolls: KotlinInt?
    
    @State var dayResetState : DayResetState?
    
    var L : Int? = {
        do {
            let dir = try FileManager.default.url(for: .documentDirectory, in: .userDomainMask, appropriateFor: nil, create: false).absoluteString
            let directoryContents = try FileManager.default.contentsOfDirectory(atPath: dir)
            
            for url in directoryContents {
                print("Document contents:" + url)
            }
            return 1
        } catch {
           return 0
        }
    }()
    
    
    var body: some View {
        content
            .task{
                let viewModel = KotlinDependencies.shared.getDailyFeedViewModel()
                await withTaskCancellationHandler(operation: {
                    self.dailyFeedViewModel = viewModel
                    
                    for await rerolls in viewModel.rerolls {
                        print("rerols did change \(String(describing: rerolls))")
                        self.rerolls = rerolls
                    }
                }) {
                    viewModel.clear()
                }
            }
            .task {
                let viewModel = KotlinDependencies.shared.getDailyFeedViewModel()
                
                for await dailyFeedState in viewModel.dailyFeedState {
                    print("daily feed state did change \(dailyFeedState)")
                    self.dailyFeedState = dailyFeedState
                }
            }
            .task {
                let viewModel = KotlinDependencies.shared.getDailyFeedViewModel()
                
                for await dayResetState in viewModel.dayReloadState {
                    print("dayResetState did change \(dayResetState)")
                    self.dayResetState = dayResetState
                }
            }
    }
    
    var content: some View {
        VStack {
            switch dailyFeedState {
            case is DailyFeedStateError:
                Button(NSLocalizedString("reloadButton", comment: ""), action: {dailyFeedViewModel?.retryInit()})
            case is DailyFeedStateLoading, .none:
                ProgressView()
            case let ready as DailyFeedStateReady:
                feedList(ready.dailyActivities)
            case .some(_):
                ErrorView(message: "unknown state for dailyFeedState - .some(_)")
            }
        }
    }
    
    
    private func feedList(_ dailyActivities: [Activity]) -> some View {
        ScrollView {
            LazyVGrid(columns: [GridItem(.adaptive(minimum: 200))], spacing: 8, content: {
                ForEach(dailyActivities, id: \.self.key) { activityData in
                        ZStack{
                            if let urlString = activityData.path {
                                let _ = print("url:" + urlString)
                                

                                if let uiImage = UIImage(contentsOfFile: urlString) {
                                    let _ = print("got ui image")
                                    Image(uiImage: uiImage)
                                }
                            }
                            Text(activityData.activity)
                        }
                }
            })
        }
    }
}

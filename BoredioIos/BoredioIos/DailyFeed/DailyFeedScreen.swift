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
    
    var body: some View {
        content
            .task{
                let viewModel = KotlinDependencies.shared.getDailyFeedViewModel()
                await withTaskCancellationHandler(operation: {
                    self.dailyFeedViewModel = viewModel
                    
                    for await rerolls in viewModel.rerolls {
                        self.rerolls = rerolls
                    }
                    for await dailyFeedState in viewModel.dailyFeedState {
                        self.dailyFeedState = dailyFeedState
                    }
                    for await dayResetState in viewModel.dayReloadState {
                        self.dayResetState = dayResetState
                    }
                }) {
                    viewModel.clear()
                }
            }
    }
    
    var content: some View {
        VStack {
        }
    }
}

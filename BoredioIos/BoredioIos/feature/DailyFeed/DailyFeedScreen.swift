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

class ObservableDailyFeedState: ObservableObject {
    @Published var dailyFeedState: DailyFeedState?
    
    init(dailyFeedState: DailyFeedState? = nil) {
        self.dailyFeedState = dailyFeedState
    }
    
    
    func set(_ x: DailyFeedState) {
        self.dailyFeedState = x
    }
}

class ObservableDayResetState: ObservableObject {
    @Published var dayResetState: DayResetState?
    
    init(dayResetState: DayResetState? = nil) {
        self.dayResetState = dayResetState
    }
    
    func set(_ x: DayResetState) {
        self.dayResetState = x
    }
}


struct DailyFeedScreen: View {
    @State var dailyFeedViewModel: DailyFeedViewModel?
    
    @ObservedObject var dailyFeedState: ObservableDailyFeedState = ObservableDailyFeedState()
    
    @State var rerolls: Int?
    
    @ObservedObject var dayResetState : ObservableDayResetState = ObservableDayResetState()
    
    var body: some View {
        content
            .task{
                let viewModel = KotlinDependencies.shared.getDailyFeedViewModel()
                await withTaskCancellationHandler(operation: {
                    self.dailyFeedViewModel = viewModel
                    
                    for await rerolls in viewModel.rerolls {
                        self.rerolls = rerolls?.intValue
                    }
                }) {
                    viewModel.clear()
                }
            }
            .task {
                let viewModel = KotlinDependencies.shared.getDailyFeedViewModel()
                
                for await dailyFeedState in viewModel.dailyFeedState {
                    print("daily feed")
                    self.dailyFeedState.set(dailyFeedState)
                }
            }
            .task {
                let viewModel = KotlinDependencies.shared.getDailyFeedViewModel()
                
                for await dayResetState in viewModel.dayReloadState {
                    self.dayResetState.set(dayResetState)
                }
            }
    }
    
    var content: some View {
        VStack {
            switch dailyFeedState.dailyFeedState {
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
                                //TODO Document absolute patch might changes refactor
                                let relativeUrl = urlString.split(separator: "Documents/")[1]
                                let documentsUrl = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0]
                                let modifiedUrl = documentsUrl + "/" + relativeUrl
                                if let uiImage = UIImage(contentsOfFile: modifiedUrl) {
                                    Image(uiImage: uiImage)
                                        .resizable()
                                        .aspectRatio(contentMode: /*@START_MENU_TOKEN@*/.fill/*@END_MENU_TOKEN@*/)
                                        
                                }
                            }
                            ActivityOverlay(activityData: activityData)
                        }
                        .aspectRatio(1, contentMode: .fit)
                        .cornerRadius(10)
                }
            })
            .padding(8)
        }
    }
    
    private let overlayActionsColor = Color(uiColor: UIColor (red: 245/255.0, green: 245/255.0, blue:245/255.0, alpha: 1.0))
    
    func ActivityOverlay(activityData: Activity) -> some View {
        let color = if(activityData.completed) {
            UIColor(red: 75/255.0, green:181/255.0, blue:67/255.0, alpha:0.6)
        } else if(activityData.ignore) {
            UIColor(red: 181/255.0, green:67/255.0, blue:67/255.0, alpha:0.6)
        } else {
            UIColor(red: 0.0, green: 0.0, blue: 0.0, alpha: 0.4)
        }
        
        return Rectangle()
            .foregroundColor(
                Color(uiColor: color)
            )
            .overlay {
                ZStack {
                    Text(activityData.activity)
                        .padding(8)
                        .foregroundStyle(overlayActionsColor)
                        .font(.largeTitle)
                        .multilineTextAlignment(.center)
                    
                    if(!activityData.completed && !activityData.ignore){
                        interactiveElements(activityData: activityData)
                    }
                }
            }
            
    }
    
    @State private var noRerollsAlertDialog = false
    
    func interactiveElements(activityData: Activity) -> some View {
        VStack{
            Spacer()
            HStack {
                Button(action: {dailyFeedViewModel?.ignore(activity: activityData)}, label: {
                    Image(systemName: "trash.fill")
                        .resizable()
                        .frame(width: 24, height: 24)
                        .padding(.horizontal, 8)
                        .foregroundColor(overlayActionsColor)
                })
                Button(action: {dailyFeedViewModel?.reroll(activity: activityData, onNoRerolls: {
                    self.noRerollsAlertDialog = true
                })
                }, label: {
                    Image(systemName: "arrow.counterclockwise")
                        .resizable()
                        .frame(width: 24, height: 24)
                        .foregroundColor(overlayActionsColor)
                })
                .alert(isPresented: $noRerollsAlertDialog, content: {
                    Alert(title: Text(NSLocalizedString("noRerollsTitle", comment: "")), message: Text(NSLocalizedString("noRerollsText", comment: "")), dismissButton: .default(Text(NSLocalizedString("ok", comment: ""))))
                })
                Spacer()
                Button(action: {dailyFeedViewModel?.complete(activity: activityData,  rating: 0)}, label: {
                    Image(systemName: "checkmark.square.fill")
                        .resizable()
                        .frame(width: 24, height: 24)
                        .padding(.horizontal, 8)
                        .foregroundColor(overlayActionsColor)
                })
            }
            .padding(8)
            .padding(.bottom, 8)
        }
    }
}

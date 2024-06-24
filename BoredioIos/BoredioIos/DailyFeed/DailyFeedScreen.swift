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
                        .frame(width: 30, height: 30)
                        .foregroundColor(overlayActionsColor)
                })
                Button(action: {dailyFeedViewModel?.reroll(activity: activityData, onNoRerolls: {
                    self.noRerollsAlertDialog = true
                })
                }, label: {
                    Image(systemName: "arrow.counterclockwise")
                        .resizable()
                        .frame(width: 30, height: 30)
                        .foregroundColor(overlayActionsColor)
                })
                .alert(isPresented: $noRerollsAlertDialog, content: {
                    Alert(title: Text(NSLocalizedString("noRerollsTitle", comment: "")), message: Text(NSLocalizedString("noRerollsText", comment: "")), dismissButton: .default(Text(NSLocalizedString("ok", comment: ""))))
                })
                Spacer()
                Button(action: {dailyFeedViewModel?.complete(activity: activityData,  rating: 0)}, label: {
                    Image(systemName: "checkmark.square.fill")
                        .resizable()
                        .frame(width: 30, height: 30)
                        .foregroundColor(overlayActionsColor)
                })
            }
            .padding(8)
        }
    }
}

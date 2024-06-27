//
//  StoregeScreen.swift
//  BoredioIos
//
//  Created by Václav Kobera on 27.06.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared
import SwiftUI


class ObservableSelector: ObservableObject {
    @Published var selectedFilter: StorageFilter = StorageFilterAll()
    
    func set(_ x: StorageFilter) {
        self.selectedFilter = x
    }
}

class ObservableActivities: ObservableObject {
    @Published var activities: StorageActivities = StorageActivitiesLoading()
    
    func set(_ x: StorageActivities) {
        self.activities = x
    }
}


struct StoregeScreen: View {
    @State var viewModel: StorageViewModel?
    
    @ObservedObject var observedFilter = ObservableSelector()
    @ObservedObject var observedActivities = ObservableActivities()
    
    var body: some View {
        content
            .task {
                let vm = KotlinDependencies.shared.getStorageViewModel()
                
                await withTaskCancellationHandler(operation: {
                    self.viewModel = vm
                    
                    for await filter in vm.selectedFilter {
                        self.observedFilter.set(filter)
                    }
                }) {
                    vm.clear()
                }
            }
            .task {
                let vm = KotlinDependencies.shared.getStorageViewModel()

                for await activities in vm.activities {
                    self.observedActivities.set(activities)
                }
            }
    }
    
    
    var content: some View {
        VStack {
            switch self.observedActivities.activities {
            case is StorageActivitiesLoading:
                ProgressView()
            case let success as StorageActivitiesSuccess:
                activityList(activities: success.activities)
                Spacer()
                bottomRow(selectedFilter: self.observedFilter.selectedFilter)
            default:
                ErrorView(message: "Unknown screen state of \(self.observedActivities.activities)")
            }
        }
    }
    
    func activityList(activities: [Activity]) -> some View {
        ScrollView {
            LazyVStack{
                ForEach(activities, id: \.self.key ) { activity in
                    actitiyBox(activity: activity)
                }
            }
        }
    }
    
    func actitiyBox(activity: Activity) -> some View {
        ZStack {
            Color.gray
                .opacity(0.4)
                .cornerRadius(10)
            VStack {
                Text(activity.activity)
                    .font(.system(size: 32))
                    .padding(8)
                    .multilineTextAlignment(.center)
                Divider()
                    .padding(0)
                HStack() {
                    VStack(alignment: .leading) {
                        Text(NSLocalizedString("searchCardTypeLabel", comment: ""))
                        Text(NSLocalizedString("searchCardParticipants", comment: ""))
                        Text(NSLocalizedString("searchCardPrice", comment: ""))
                        Text(NSLocalizedString("searchCardAccessibility", comment: ""))
                    }
                    .fontWeight(.bold)
                    VStack(alignment: .leading) {
                        //TODO capitalize 1st char  + value mapping
                        Text(activity.type.name.lowercased())
                        Text("\(activity.participants)")
                        Text("\(activity.price.getPriceDescription())")
                        Text("\(activity.accessibility.getAccesibilityDescription())")
                    }
                }
                .padding(8)
                Divider()
                HStack {
                    Spacer()
                    Button(action: {
                        self.viewModel?.favoriteToggle(activity: activity)
                    }) {
                        if(activity.favorite) {
                            Image(systemName: "star.fill")
                                .foregroundColor(.yellow)
                        } else {
                            Image(systemName: "star")
                        }
                    }
                    Spacer()
                    Button(action: {
                        self.viewModel?.ignoreToggle(activity: activity)
                    }) {
                        let color : Color = if(activity.ignore) {
                            .red
                        } else {
                            .gray
                        }
                        Image(systemName: "exclamationmark.square.fill")
                            .foregroundColor(color)
                    }
                    Spacer()
                    Button {
                        self.viewModel?.deleteImage(activity: activity)
                    } label: {
                        Image(systemName: "millsign.square")
                            .foregroundColor(.red)
                    }
                    .disabled(activity.path == nil || activity.isDailyFeed)
                    Spacer()
                    Button {
                        self.viewModel?.delete(activity: activity)
                    } label: {
                        Image(systemName: "trash")
                    }
                    .disabled(activity.isDailyFeed)
                    Spacer()
                }
                .padding(8)
                .padding(.bottom, 8)
            }
        }
        .padding(8)
    }
    
    
    func bottomRow(selectedFilter: StorageFilter) -> some View {
        Text("TODO")
    }
}



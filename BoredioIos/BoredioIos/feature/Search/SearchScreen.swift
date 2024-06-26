//
//  SearchScreen.swift
//  BoredioIos
//
//  Created by Václav Kobera on 25.06.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared
import SwiftUIFlow

//is there any workaround ??
class ObservableCallParameters: ObservableObject {
    @Published var callParams: CallParameters = CallParameters.Companion.shared.DEFAULT
    
    
    func set(_ cp: CallParameters) {
        self.callParams = cp
    }
}

class ObservableSearchState: ObservableObject {
    @Published var searchState: SearchState = SearchStateInit()
    
    func set(_ ss: SearchState) {
        self.searchState = ss
    }
}

struct SearchScreen: View {
    @State var searchViewModel: SearchViewModel?

    @ObservedObject var currentParams: ObservableCallParameters = ObservableCallParameters()
    @State var canLoadMore: Bool?
    @ObservedObject var searchState: ObservableSearchState = ObservableSearchState()
    @State var showingBottomSheet: Bool = false
    
    
    //all this values has to be from 0 to 1 and than mapped to whatever we need
    @State var minParticipants: Double = 0.0
    @State var maxParticipants: Double = 1.0
    @State var minPrice: Double = 0.0
    @State var maxPrice: Double = 1.0
    @State var minAccessibility: Double = 0.0
    @State var maxAccessibility: Double = 1.0
    
    var body: some View {
        content
            .task {
                let viewModel = KotlinDependencies.shared.getSearchViewModel()
                await withTaskCancellationHandler(operation: {
                    self.searchViewModel = viewModel
                    
                    for await canLoadMore in viewModel.canLoadMore {
                        self.canLoadMore = canLoadMore.boolValue
                    }
                }) {
                    viewModel.clear()
                }
            }
            .task {
                let viewModel = KotlinDependencies.shared.getSearchViewModel()

                for await currentParams in viewModel.currentParams {
                    self.minPrice = currentParams.minPrice
                    self.maxPrice = currentParams.maxPrice
                    self.minParticipants = Double(currentParams.minParticipants - 1)/9
                    self.maxParticipants = Double(currentParams.maxParticipants - 1)/9
                    self.minAccessibility = currentParams.minAccessibility
                    self.maxAccessibility = currentParams.maxAccessibility
                    
                    self.currentParams.set(currentParams)
                }
            }
            .task {
                let vm = KotlinDependencies.shared.getSearchViewModel()
                
                for await searchState in vm.searchState {
                    self.searchState.set(searchState)
                }
            }
            .task {
                let vm = KotlinDependencies.shared.getSearchViewModel()
                
                for await showingBottomSheet in vm.showingBottomSheet {
                    self.showingBottomSheet = showingBottomSheet.boolValue
                }
            }
            
    }
    
    var content: some View {
        VStack {
            switch searchState.searchState {
            case is SearchStateInit:
                initScreen()
            case is SearchStateError:
                errorScreen()
            case is SearchStateEmpty:
                emptyScreen()
            case is SearchStateLoading:
                ProgressView()
            case let success as SearchStateSuccess:
                ScrollView {
                    activityList(activities: success.activities)
                }
            default:
                ErrorView(message: "uknown state of \(String(describing: searchState.searchState))")
            }
        }
        .sheet(isPresented: $showingBottomSheet, onDismiss: {
            searchViewModel?.setSheet(to: false)
        }) {
            sheetContent(currentParams: currentParams.callParams)
        }
    }
    
    func initScreen() -> some View {
        VStack {
            Text(NSLocalizedString("searchInitScreenText", comment: ""))
                .padding(8)
                .multilineTextAlignment(.center)
            Button(NSLocalizedString("searchInitButton", comment: "")) {
                searchViewModel?.setSheet(to: true)
            }
            .buttonStyle(.bordered)
        }
    }
    
    func errorScreen() -> some View {
        VStack {
            Text(NSLocalizedString("searchErrorScreenText", comment: ""))
            Button(NSLocalizedString("searchErrorReloadButton", comment: "")) {
                searchViewModel?.reload()
            }
            .buttonStyle(.bordered)
        }
    }
    
    //TODO no activities for filter -- need fix on sherd module
    func emptyScreen() -> some View {
        VStack {
            Text(NSLocalizedString("searchEmptyScreenText", comment: ""))
        }
    }
    
    func activityList(activities: [Activity]) -> some View {
        LazyVStack {
            ForEach(activities, id: \.self.key) { activityData in
                actiityBox(activity: activityData)
            }
            if(canLoadMore == true){
                Button(NSLocalizedString("searchLoadMoreButton", comment: "")) {
                    searchViewModel?.loadMore()
                }
            }
        }
    }
    
    func actiityBox(activity: Activity) -> some View {
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
                        searchViewModel?.saveActivity(activity: activity)
                    }) {
                        Image(systemName: "square.and.arrow.down")
                    }
                    .disabled(activity.isStored)
                    Button(action: {
                        searchViewModel?.likeActivityToggle(activity: activity)
                    }) {
                        if(activity.favorite) {
                            Image(systemName: "star.fill")
                                .foregroundColor(.yellow)
                        } else {
                            Image(systemName: "star")
                        }
                    }
                }
                .padding(8)
            }
        }
        .padding(8)
    }
    
    func sheetContent(currentParams: CallParameters) -> some View {
        VStack(alignment: .center) {
            Text(NSLocalizedString("searchFilterTypeLabel", comment: ""))
                .padding(EdgeInsets(top: 20, leading: 0, bottom: 0, trailing: 0))
            ScrollView(.vertical) {
                VFlow(alignment: .leading) {
                    Spacer()
                    ForEach(ActivityType.allCases, id: \.self.ordinal) { activityType in
                        let isInParams = currentParams.isInParameters(activityType: activityType)
                        let color : Color? = if(isInParams) {
                            Color.green
                        } else {
                            nil
                        }
                        Button(activityType.name){
                            if(!isInParams){
                                searchViewModel?.changeParams(calledParameters: currentParams.addType(activityType: activityType))
                            }
                            else {
                                searchViewModel?.changeParams(calledParameters: currentParams.removeType(activityType: activityType))
                            }
                        }
                        .buttonStyle(.bordered)
                        .tint(
                            color
                        )
                    }
                }
            }
            .padding(8)
            .frame(height: 150)
            
            Divider()
            Text(NSLocalizedString("searchFilterParticipantsLabel", comment: ""))
            Text("\(Int((minParticipants*9+1).rounded(.toNearestOrAwayFromZero))) - \(Int((maxParticipants*9+1).rounded(.toNearestOrAwayFromZero)))")
            DoubleSlider(
                startFraction: $minParticipants,
                endFraction: $maxParticipants,
                onDrop: { start, end in
                    searchViewModel?
                        .changeParams(
                            calledParameters: currentParams
                                .setParticipants(min: Int32((start*9+1).rounded(.toNearestOrAwayFromZero)), max: Int32((end*9+1).rounded(.toNearestOrAwayFromZero)))
                        )
                }
            )
            Text(NSLocalizedString("searchFilterPriceLabel", comment: ""))
            Text("\(minPrice.getPriceDescription()) - \(maxPrice.getPriceDescription())")
            DoubleSlider(startFraction: $minPrice, endFraction: $maxPrice) { start, end in
                searchViewModel?.changeParams(calledParameters: currentParams.setPrice(min: start, max: end))
            }
            Text(NSLocalizedString("searchFilterAccessbilityLabel", comment: ""))
            Text("\(minAccessibility.getAccesibilityDescription()) - \(maxAccessibility.getAccesibilityDescription())")
            
            DoubleSlider(
                startFraction: $minAccessibility,
                endFraction: $maxAccessibility
            ) { start, end in
                searchViewModel?.changeParams(calledParameters: currentParams.setAccessibility(min: start, max: end))
            }
            
            Divider()
            HStack {
                Spacer()
                Button(NSLocalizedString("searchScreenResetFiltersButton", comment: "")) {
                    searchViewModel?.changeParams(calledParameters: CallParameters.Companion.shared.DEFAULT)
                }
                Spacer()
                Button(NSLocalizedString("searchInitButton", comment: "")) {
                    searchViewModel?.search()
                    searchViewModel?.setSheet(to: false)
                }
                Spacer()
            }
            .padding()
            .buttonStyle(.bordered)
            Spacer()
        }
        .presentationDetents([.large])
    }
}


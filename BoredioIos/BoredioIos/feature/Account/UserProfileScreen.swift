//
//  UserProfileScreen.swift
//  BoredioIos
//
//  Created by Václav Kobera on 27.06.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared
import SwiftUI
import Charts


class ObservableCompleted: ObservableObject {
    @Published var completed: CompletedActivities = CompletedActivities.Companion.shared.Empty
    
    func set(_ x: CompletedActivities) {
        self.completed = x
    }
}

protocol UserProfileScreenNavigationDelegate: NSObject {
    func navigateStorage()
}

struct UserProfileScreen: View{
    weak var navDelegate: UserProfileScreenNavigationDelegate?
    
    init(navDelegate: UserProfileScreenNavigationDelegate?) {
        self.navDelegate = navDelegate
    }
    
    @State var viewModel: AccountViewModel?
    
    @ObservedObject var completed = ObservableCompleted()
    
    @State var totalDownloadedActivities: Int = -1
    @State var imagesGenerated: Int = -1
    @State var totalImageSpace: String = ""
    
    var body: some View {
        VStack {
            completedActiities()
            Spacer()
                .frame(height: 20)
            storage()
            Spacer()
        }
        .task {
            let vm = KotlinDependencies.shared.getAccountViewModel()
            
            await withTaskCancellationHandler(operation: {
                self.viewModel = vm
                vm.loadData()
                
                for await completed in vm.completed {
                    self.completed.set(completed)
                }
            }) { 
                vm.clear()
            }
        }
        .task {
            let vm = KotlinDependencies.shared.getAccountViewModel()
            
            for await iCompleted in vm.imagesGenerated {
                self.imagesGenerated = iCompleted.intValue
            }
        }
        .task {
            let vm = KotlinDependencies.shared.getAccountViewModel()
            
            for await tISpace in vm.totalImageSpace {
                print("total image space changed \(tISpace)")
                self.totalImageSpace = tISpace
            }
        }
        .task {
            let vm = KotlinDependencies.shared.getAccountViewModel()
            
            for await tDActivities in vm.totalDownloadedActivities {
                self.totalDownloadedActivities = tDActivities.intValue
            }
        }
    }
    
    func completedActiities() -> some View {
        VStack {
            Text(NSLocalizedString("accountScreenCompeltedTitle", comment: ""))
                .font(.system(size: 24, weight: .bold))
                .padding(.top, 12)
                .padding(.bottom, 8)
            HStack {
                Spacer()
                Text(NSLocalizedString("accountScreenCompeltedToday", comment: "")  + " ")
                    .font(.system(size: 16, weight: .bold))
                Text("\(completed.completed.today)")
                    .font(.system(size: 16))
                Spacer()
                Text(NSLocalizedString("accountScreenCompeltedTotal", comment: "") + " ")
                    .font(.system(size: 16, weight: .bold))

                Text("\(completed.completed.total)")
                Spacer()
            }
            
            Chart {
                ForEach(Array(completed.completed.getDays().map{ it in it.intValue}.enumerated()), id: \.offset ){ index, value in
                    BarMark(x: .value("Date", index), y: .value("cnt", value))
                }
            }
            .chartXScale(domain: 0 ... 7)
            .chartXAxis(){
                AxisMarks() { value in
                    AxisValueLabel("\(value.index)", centered: true)
                }
            }
            .chartYScale(domain: 0 ... 6)
            .padding(8)
            .frame(height: 150)
        }
    }
    
    func storage() -> some View {
        VStack {
            Text(NSLocalizedString("accountScreenStorageTitle", comment: ""))
                .font(.system(size: 24, weight: .bold))
                .padding(.top, 12)
                .padding(.bottom, 8)
            
            HStack {
                Text(NSLocalizedString("accountScreenStorageLoaclActivities", comment: "") + " ")
                    .font(.system(size: 16, weight: .bold))
                Text("\(totalDownloadedActivities)")
                    .font(.system(size: 16))
            }
            .padding(.bottom, 4)
                
            HStack {
                Text(NSLocalizedString("accountScreenStorageImagesDownloaded", comment: "") + " ")
                    .font(.system(size: 16, weight: .bold))
                Text("\(imagesGenerated)")
                    .font(.system(size: 16))
            }
            .padding(.bottom, 4)
            
            
            HStack {
                Text(NSLocalizedString("accountScreenStorageImagesDownloadedMemory", comment: "") + " ")
                    .font(.system(size: 16, weight: .bold))
                Text("\(totalImageSpace)")
                    .font(.system(size: 16))
            }
            .padding(.bottom, 8)
            
            Button(NSLocalizedString("accountScreenStorageManageButton", comment: "")) {
                navDelegate?.navigateStorage()
            }
            .buttonStyle(.bordered)
        }
    }
}

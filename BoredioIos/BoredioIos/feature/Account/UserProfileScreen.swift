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
            HStack {
                Spacer()
                //TODO bold
                Text(NSLocalizedString("accountScreenCompeltedToday", comment: "")  + " ")
                
                Text("\(completed.completed.today)")
                Spacer()
                //TODO bold
                Text(NSLocalizedString("accountScreenCompeltedTotal", comment: "") + " ")
                Text("\(completed.completed.total)")
                Spacer()
            }
            
            //TODO Graph
        }
    }
    
    func storage() -> some View {
        VStack {
            Text(NSLocalizedString("accountScreenStorageTitle", comment: ""))
            HStack {
                Spacer()
                //TODO bold
                Text(NSLocalizedString("accountScreenStorageLoaclActivities", comment: "") + " ")
                Text("\(totalDownloadedActivities)")
                Spacer()
                VStack {
                    HStack {
                        //TODO bold
                        Text(NSLocalizedString("accountScreenStorageImagesDownloaded", comment: "") + " ")
                        Text("\(imagesGenerated)")
                    }
                    HStack {
                        //TODO bold
                        Text(NSLocalizedString("accountScreenStorageImagesDownloadedMemory", comment: "") + " ")
                        Text("\(totalImageSpace)")
                    }
                }
                Spacer()
            }
            Button(NSLocalizedString("accountScreenStorageManageButton", comment: "")) {
                
            }
            .buttonStyle(.bordered)
        }
    }
}

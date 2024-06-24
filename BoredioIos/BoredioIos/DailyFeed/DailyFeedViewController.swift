//
//  DailyFeedViewController.swift
//  BoredioIos
//
//  Created by Václav Kobera on 24.06.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import ACKategories
import shared

final class DailyFeedViewController: UIViewController {
    private let viewModel = KotlinDependencies.shared.getDailyFeedViewModel()
    
    private var rerolls: Int = 2;
    
    override func loadView() {
        super.loadView()
        let rootView = DailyFeedScreen()
        let vc = UIHostingController(rootView: rootView)
        embedController(vc)
        Task {
            for await rerolls in viewModel.rerolls {
                self.rerolls = rerolls!.intValue
                onRerollsChange()
            }
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        navigationItem.title = NSLocalizedString("dailyFeedScreenTitle", comment: "")
        onRerollsChange()
    }
    
    private func onRerollsChange() {
        // well prolly cant make it plain text xdd
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: NSLocalizedString("rerolls", comment: "") + ": \(rerolls)" , style: .plain, target: nil, action: nil)
    }
}

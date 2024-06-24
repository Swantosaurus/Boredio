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

final class DailyFeedViewController: Base.ViewController {
    
    override func loadView() {
        super.loadView()
        let rootView = DailyFeedScreen()
        let vc = UIHostingController(rootView: rootView)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        navigationItem.title =
    }
}

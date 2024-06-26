//
//  DailyFeedFlowCoordinator.swift
//  BoredioIos
//
//  Created by Václav Kobera on 24.06.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import ACKategories
import UIKit

final class DailyFeedFlowCoordinator: Base.FlowCoordinatorNoDeepLink {
    override func start() -> UIViewController {
        super.start()
        
        let vc = DailyFeedViewController()
        let navVC = UINavigationController(rootViewController: vc)
        rootViewController = vc
        navigationController = navVC
        
        return navVC
    }
}

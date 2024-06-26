//
//  SearchScreenFlowCoordinator.swift
//  BoredioIos
//
//  Created by Václav Kobera on 25.06.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import ACKategories
import UIKit

final class SearchScreenFlowCoordinator: Base.FlowCoordinatorNoDeepLink {
    
    override func start() -> UIViewController {
        super.start()
        
        
        let vc = SearchScreenViewController()
        let navVC = UINavigationController(rootViewController: vc)
        
        rootViewController = vc
        navigationController = navVC
        
        return navVC
    }
}

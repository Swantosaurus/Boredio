//
//  AboutFlowCoordinator.swift
//  BoredioIos
//
//  Created by Václav Kobera on 26.06.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import ACKategories
import UIKit

class AboutFlowCoordinator: Base.FlowCoordinatorNoDeepLink {
    override func start() -> UIViewController {
        super.start()
        let vc = AboutViewController()
        let navVc = UINavigationController(rootViewController: vc)
        //navVc.navigationItem.title = "TTTT"
        
        rootViewController = vc
        navigationController = navVc
        return navVc
    }
}

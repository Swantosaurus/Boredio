//
//  StorageFlow.swift
//  BoredioIos
//
//  Created by Václav Kobera on 27.06.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import ACKategories
import UIKit


class StorageFlow: Base.FlowCoordinatorNoDeepLink {
    
    override func start(with navigationController: UINavigationController) {
        let vc = StorageController() // WHAT AM I DOIN THERE????
        rootViewController = vc
        super.start(with: navigationController)
        
        
        navigationController.pushViewController(vc, animated: true)
    }
}

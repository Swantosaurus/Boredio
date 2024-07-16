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
        //
        super.start(with: navigationController)
        
        let vc = StorageController() // WHAT AM I DOIN THERE????
        rootViewController = vc

        navigationController.pushViewController(vc, animated: true)
    }
    
    override func start() -> UIViewController {
        super.start()
        
        let vc = StorageController()
        rootViewController = vc
        
        return vc
    }
}

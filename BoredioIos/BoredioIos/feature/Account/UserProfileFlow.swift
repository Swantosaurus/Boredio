//
//  UserProfileFlow.swift
//  BoredioIos
//
//  Created by Václav Kobera on 27.06.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import ACKategories
import UIKit

class UserProfileFlow: Base.FlowCoordinatorNoDeepLink {
    override func start() -> UIViewController {
        super.start()
        
        let vc = UserProfileController(delegate: self)
        let navVC = UINavigationController(rootViewController: vc)
        
        rootViewController = vc
        navigationController = navVC
        
        return navVC
    }
}

extension UserProfileFlow: UserProfileScreenNavigationDelegate {
    func navigateStorage() {
        guard let navigationController else { return }
        
        
        let fc = StorageFlow()
        addChild(fc)
        fc.start(with: navigationController)
    }
}

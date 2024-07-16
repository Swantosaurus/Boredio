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
    weak var delegate: UserProfileScreenNavigationDelegate?

    init(delegate: UserProfileScreenNavigationDelegate?) {
        super.init()
        self.delegate = delegate
    }
    
    override func start() -> UIViewController {
        super.start()
        
        let vc = UserProfileController(delegate: delegate)
        let navVC = UINavigationController(rootViewController: vc)
        
        rootViewController = vc
        navigationController = navVC
        
        return navVC
    }
}

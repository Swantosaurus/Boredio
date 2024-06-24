//
//  BottomBarNavigationViewController.swift
//  BoredioIos
//
//  Created by Václav Kobera on 24.06.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import ACKategories
import UIKit

final class BottomBarNavigationFlow: Base.FlowCoordinatorNoDeepLink {
    private weak var tabBar: UITabBarController!
    private weak var window: UIWindow!
    
    override func start(in window: UIWindow) {
        self.window = window
        
        super.start(in: window)
        
        
        setupTabBar()
    }
    
    func setupTabBar() {
        
        //TODO i cant make it before rootViewController is set duno how this should work
        let dailyFeedFlow = DailyFeedFlowCoordinator()
        addChild(dailyFeedFlow)
        let dailyFeedNavController = dailyFeedFlow.start()
        dailyFeedNavController.tabBarItem.title = NSLocalizedString("dayFeedTabBarTitle", comment: "")
        dailyFeedNavController.tabBarItem.image = UIImage(systemName: "newspaper")
        
        let tabBarController = UITabBarController()
        tabBarController.viewControllers = [
            dailyFeedNavController
        ]
        rootViewController = tabBarController
        
        window?.rootViewController = tabBarController
        window?.makeKeyAndVisible()
        
        self.tabBar = tabBarController
    }
}

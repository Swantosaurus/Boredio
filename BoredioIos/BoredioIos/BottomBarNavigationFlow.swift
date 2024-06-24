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
        //TODO make all controllers in bottom bar
        
        let dailyFeedController = DailyFeedViewController()
        dailyFeedController.tabBarItem.title = NSLocalizedString("dayFeedTabBarTitle", comment: "")
        dailyFeedController.tabBarItem.image = UIImage(systemName: "newspaper")
        
        let tabBarController = UITabBarController()
        tabBarController.viewControllers = [
            dailyFeedController
        ]
        rootViewController = tabBarController
        
        window?.rootViewController = tabBarController
        window?.makeKeyAndVisible()
        
        self.tabBar = tabBarController
    }
}

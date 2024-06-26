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
        
        // MARK: Daily Feed
        let dailyFeedFlow = DailyFeedFlowCoordinator()
        addChild(dailyFeedFlow)
        let dailyFeedNavController = dailyFeedFlow.start()
        dailyFeedNavController.tabBarItem.title = NSLocalizedString("dayFeedTabBarTitle", comment: "")
        dailyFeedNavController.tabBarItem.image = UIImage(systemName: "newspaper")
        
        // MARK: Search
        
        let searchFlow = SearchScreenFlowCoordinator()
        addChild(dailyFeedFlow)
        let searchNC = searchFlow.start()
        searchNC.tabBarItem.title = NSLocalizedString("searchTabBarTitle", comment: "")
        searchNC.tabBarItem.image = UIImage(systemName: "rectangle.and.text.magnifyingglass")
        
        
        
        
        // MARK: About
        let aboutFlow = AboutFlowCoordinator()
        addChild(aboutFlow)
        let aboutNC = aboutFlow.start()
        aboutNC.tabBarItem.title = NSLocalizedString("aboutTabBarTitle", comment: "")
        aboutNC.tabBarItem.image = UIImage(systemName: "info.circle")
        
        // MARK: TAB BAR
        
        let tabBarController = UITabBarController()
        tabBarController.viewControllers = [
            dailyFeedNavController,
            searchNC,
            aboutNC
        ]
        rootViewController = tabBarController
        
        window?.rootViewController = tabBarController
        window?.makeKeyAndVisible()
        
        self.tabBar = tabBarController
    }
}

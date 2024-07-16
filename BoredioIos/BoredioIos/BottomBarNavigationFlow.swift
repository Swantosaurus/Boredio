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
    weak var delegate: UserProfileScreenNavigationDelegate?
    
    init(delegate: UserProfileScreenNavigationDelegate) {
        super.init()
        self.delegate = delegate
    }
    
    override func start() -> UIViewController {
        super.start()
    
        return setupTabBar()
    }
    
    func setupTabBar() -> UIViewController {
        
        // MARK: Daily Feed
        let dailyFeedFlow = DailyFeedFlowCoordinator()
        addChild(dailyFeedFlow)
        let dailyFeedNavController = dailyFeedFlow.start()
        dailyFeedNavController.tabBarItem.title = NSLocalizedString("dayFeedTabBarTitle", comment: "")
        dailyFeedNavController.tabBarItem.image = UIImage(systemName: "newspaper")
        
        // MARK: User Profile
        
        let userProfileFlow = UserProfileFlow(delegate: delegate)
        addChild(userProfileFlow)
        let userProfileController = userProfileFlow.start()
        userProfileController.tabBarItem.title = NSLocalizedString("aboutTabBarTitle", comment: "")
        userProfileController.tabBarItem.image = UIImage(systemName: "person.crop.circle")
        
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
            userProfileController,
            searchNC,
            aboutNC
        ]
        
        rootViewController = tabBarController
        
        self.tabBar = tabBarController
        return tabBarController
    }
}

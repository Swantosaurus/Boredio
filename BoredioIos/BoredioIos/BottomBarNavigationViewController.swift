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

final class BottomBarNavigationViewController: Base.ViewController {
    private weak var tabBar: UITabBarController!
    
    override func loadView() {
        super.loadView()
        view.backgroundColor = .yellow
        //TODO make all controllers in bottom bar
        
        let dailyFeedController = DailyFeedViewController()
        dailyFeedController.tabBarItem.title = NSLocalizedString("dayFeedTabBarTitle", comment: "")
        dailyFeedController.tabBarItem.image = UIImage(systemName: "newspaper")
        
        let tabBarController = UITabBarController()
        tabBarController.viewControllers = [
            dailyFeedController
        ]
        
        embedController(tabBarController)
        self.tabBar = tabBarController
    }
}

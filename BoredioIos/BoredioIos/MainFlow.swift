//
//  MainFlow.swift
//  BoredioIos
//
//  Created by Václav Kobera on 16.07.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import ACKategories
import UIKit

final class MainFlow: Base.FlowCoordinatorNoDeepLink {
    private weak var window: UIWindow!
    private weak var mainNavController: UINavigationController!
    private weak var tabBarViewController: UIViewController!
    private weak var storageFlow: StorageFlow?

    override func start(in window: UIWindow) {
        self.window = window
        
        super.start(in: window)
        
        start()
    }
    
    
    private func start() {
        let bottomFlow = BottomBarNavigationFlow(delegate: self)
        let bottomBarVc = bottomFlow.start()
        
        
        let myVc = UINavigationController()
        myVc.navigationBar.isHidden = true
        
        self.rootViewController = myVc
        
        window?.rootViewController = myVc
        window?.makeKeyAndVisible()
        
        addChild(bottomFlow)
        myVc.pushViewController(bottomBarVc, animated: false)
        mainNavController = myVc
        tabBarViewController = bottomBarVc
    }
}

extension MainFlow: UserProfileScreenNavigationDelegate {
    func navigateStorage() {
        guard let mainNavController else { return }
        mainNavController.popToViewController(tabBarViewController, animated: false)
        
        let fc = StorageFlow(delegate: self)
        self.storageFlow = fc
        addChild(fc)
        let sVC = fc.start()
        
        mainNavController.navigationBar.isHidden = false
        
        mainNavController.pushViewController(sVC, animated: true)
    }
}

extension MainFlow: StorageControllerDelegate {
    func navigateUp() {
        mainNavController.navigationBar.isHidden = true
        removeChild(self.storageFlow!)
        storageFlow = nil
    }
}

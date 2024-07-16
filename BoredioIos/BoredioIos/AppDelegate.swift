//
//  AppDelegate.swift
//  BoredioIos
//
//  Created by Václav Kobera on 13.06.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import UIKit
import SwiftUI

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?
    
    let mainFlow = MainFlow()

    func application(_ application: UIApplication, didFinishLaunchingWithOptions
        launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {

        startKoin() // DI
        
        self.window = UIWindow(frame: UIScreen.main.bounds)
        mainFlow.start(in: self.window!)

        return true
    }
}

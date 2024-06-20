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

    // Lazy so it doesn't try to initialize before startKoin() is called
    //lazy var log = koin.loggerWithTag(tag: "AppDelegate")

    func application(_ application: UIApplication, didFinishLaunchingWithOptions
        launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {

        startKoin()

        let viewController = UIHostingController(rootView: ContentView())

        self.window = UIWindow(frame: UIScreen.main.bounds)
        self.window?.rootViewController = viewController
        self.window?.makeKeyAndVisible()

        //log.v(message: {"App Started"})
        return true
    }
}

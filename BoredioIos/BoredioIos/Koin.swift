//
//  Koin.swift
//  BoredioIos
//
//  Created by Václav Kobera on 13.06.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

func startKoin() {
    // You could just as easily define all these dependencies in Kotlin,
    // but this helps demonstrate how you might pass platform-specific
    // dependencies in a larger scale project where declaring them in
    // Kotlin is more difficult, or where they're also used in
    // iOS-specific code.

    let userDefaults = UserDefaults(suiteName: "KAMPSTARTER_SETTINGS")!
    let iosAppInfo = IosAppInfo()
    let doOnStartup = { NSLog("Hello from iOS/Swift!") }

    let koinApplication = KoinIOSKt.doInitKoinIos(
        appInfo: iosAppInfo,
        doOnStartup: doOnStartup,
        ImageGeneratorOpenAiApiKey(
    )
    _koin = koinApplication.koin
}

private var _koin: Koin_coreKoin?
var koin: Koin_coreKoin {
    return _koin!
}

class IosAppInfo: AppInfo {
    let appId: String = Bundle.main.bundleIdentifier!
}

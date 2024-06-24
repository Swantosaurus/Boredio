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
    let iosAppInfo = IosAppInfo()
    let doOnStartup = { NSLog("Hello from iOS/Swift!") }

    let koinApplication = KoinIOSKt.doInitKoinIos(
        appInfo: iosAppInfo,
        doOnStartup: doOnStartup,
        openAiApiKey: OpenAiApiKey()
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

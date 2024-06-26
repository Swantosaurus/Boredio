//
//  AccesibiltiyMapping.swift
//  BoredioIos
//
//  Created by Václav Kobera on 25.06.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation

extension Double {
    func getAccesibilityDescription() -> String {
        switch self {
        case -0.5..<0.2:
            return NSLocalizedString("mesureVeryHigh", comment: "")
        case 0.2..<0.4:
            return NSLocalizedString("mesureHigh", comment: "")
        case 0.4..<0.6:
            return NSLocalizedString("mesureMedium", comment: "")
        case 0.6..<0.8:
            return NSLocalizedString("mesureLow", comment: "")
        default:
            return NSLocalizedString("mesureVeryLow", comment: "")
        }
    }
}

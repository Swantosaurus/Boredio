//
//  ErrorView.swift
//  BoredioIos
//
//  Created by Václav Kobera on 24.06.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Foundation

struct ErrorView: View {
    let message: String
    
    var body: some View {
        VStack {
            Text("THIS SCREAN SHOULDNT APPEAR")
            Text("message: \(message)")
        }
    }
}

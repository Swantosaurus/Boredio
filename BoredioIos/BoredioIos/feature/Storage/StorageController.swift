//
//  StorageController.swift
//  BoredioIos
//
//  Created by Václav Kobera on 27.06.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import ACKategories
import SwiftUI

class StorageController: Base.ViewController {
    
    override func loadView() {
        super.loadView()
        let view = StoregeScreen()
        let vc = UIHostingController(rootView: view)
        embedController(vc)
    }
    
    
}

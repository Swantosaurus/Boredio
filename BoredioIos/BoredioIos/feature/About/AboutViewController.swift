//
//  AboutViewController.swift
//  BoredioIos
//
//  Created by Václav Kobera on 26.06.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import ACKategories
import SwiftUI

class AboutViewController: Base.ViewController {
    override func loadView() {
        super.loadView()
        let view = AboutScreen()
        let vc = UIHostingController(rootView: view)
        embedController(vc)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        navigationItem.title = NSLocalizedString("aboutScreenTitle", comment: "")
    }
}

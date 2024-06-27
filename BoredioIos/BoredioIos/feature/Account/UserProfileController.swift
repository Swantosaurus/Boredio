//
//  UserProfileController.swift
//  BoredioIos
//
//  Created by Václav Kobera on 27.06.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import ACKategories
import SwiftUI


class UserProfileController: Base.ViewController {
    weak var delegate: UserProfileScreenNavigationDelegate?

    init(delegate: UserProfileScreenNavigationDelegate) {
        super.init()
        self.delegate = delegate
    }
    
    override func loadView() {
        super.loadView()
        let view = UserProfileScreen(navDelegate: delegate)
        let vc = UIHostingController(rootView: view)
        embedController(vc)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        navigationItem.title = NSLocalizedString("userProfile", comment: "")
    }
}

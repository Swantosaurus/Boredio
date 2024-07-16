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
import shared

protocol StorageControllerDelegate: NSObject {
    func navigateUp()
}

class StorageController: Base.ViewController {
    weak var delegate: StorageControllerDelegate?
    
    init(delegate: StorageControllerDelegate?) {
        super.init()
        self.delegate = delegate
    }
    
    override func loadView() {
        super.loadView()
        let view = StoregeScreen()
        let vc = UIHostingController(rootView: view)
        embedController(vc)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        navigationItem.title = NSLocalizedString("storageScreenTitle", comment: "")
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        let vm =  KotlinDependencies.shared.getStorageViewModel()
        vm.selectFilter(filter: StorageFilterAll())
        
        if self.isMovingFromParent {
            delegate!.navigateUp()
        }
    }
}

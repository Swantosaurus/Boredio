//
//  SearchScreenViewController.swift
//  BoredioIos
//
//  Created by Václav Kobera on 25.06.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import ACKategories
import UIKit
import SwiftUI
import shared

protocol BottomSheetDelegate: NSObject {
    func setSheet(to: Bool?)
}

extension SearchViewModel: BottomSheetDelegate {
    func setSheet(to: Bool?) {
        if let to = to {
            setBottomSheet(to: KotlinBoolean(bool: to))
        } else {
            setBottomSheet(to: nil)
        }
    }
}

final class SearchScreenViewController: Base.ViewController {
    private weak var bottomSheetProvider: BottomSheetDelegate?
    
    override func loadView() {
        super.loadView()
        let view = SearchScreen()
        let vm = KotlinDependencies.shared.getSearchViewModel()
        self.bottomSheetProvider = vm
        let vc = UIHostingController(rootView: view)
        embedController(vc)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        navigationItem.title = NSLocalizedString("searchScreenTitle", comment: "")
        navigationItem.rightBarButtonItem = UIBarButtonItem(image: UIImage(systemName: "magnifyingglass"), style: .plain, target: self, action:  #selector(openSheet))
    }
    
    @objc private func openSheet() {
        bottomSheetProvider?.setSheet(to: true)
    }
}

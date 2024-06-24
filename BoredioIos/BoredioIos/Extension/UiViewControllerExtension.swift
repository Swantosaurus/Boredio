//
//  UiViewControllerExtension.swift
//  BoredioIos
//
//  Created by Václav Kobera on 24.06.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import UIKit

extension UIViewController {
    func embedController(_ vc: UIViewController) {
        addChild(vc)
        view.addSubview(vc.view)
        vc.view.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            vc.view!.topAnchor.constraint(equalTo: view.topAnchor),
            vc.view!.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            vc.view!.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            vc.view!.trailingAnchor.constraint(equalTo: view.trailingAnchor),
        ])
        vc.didMove(toParent: self)
    }
}

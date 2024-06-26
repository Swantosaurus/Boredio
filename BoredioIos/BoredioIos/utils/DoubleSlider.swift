//
//  DoubleSlider.swift
//  BoredioIos
//
//  Created by Václav Kobera on 25.06.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI


struct DoubleSlider: View {
    let lineHeight : CGFloat = 5
    let mainColor: Color = .black
    let onDrop: (Double, Double) -> Void
    let knobSize: CGFloat = 20
    var totalWidth = UIScreen.main.bounds.width - 16 - 2 * 20 //padding and 2 knobs
    
    @Binding var fraction1: Double
    @Binding var fraction2: Double
    
    @State var updater: Bool = false
    
    init(startFraction: Binding<Double>, endFraction: Binding<Double>, onDrop: @escaping (Double, Double) -> Void){
        if(startFraction.wrappedValue > endFraction.wrappedValue || endFraction.wrappedValue > 1.0){
            startFraction.wrappedValue = 0.0
            endFraction.wrappedValue = 1.0
        }
        
        self._fraction1 = startFraction
        self._fraction2 = endFraction
        self.onDrop = onDrop
    }
    
    
    var body: some View {
        VStack {
            ZStack(alignment: .leading){
                Rectangle()
                    .fill(mainColor.opacity(0.3))
                    .frame(height: self.lineHeight)
                Rectangle()
                    .fill(mainColor)
                    .frame(width: fraction2 * totalWidth - fraction1 * totalWidth, height: lineHeight)
                    .offset(x: fraction1 * totalWidth + knobSize)
                
                HStack(spacing: 0) {
                    Circle()
                        .fill(mainColor)
                        .frame(width: knobSize, height: knobSize)
                        .offset(x: fraction1 * totalWidth)
                        .gesture(
                            DragGesture()
                                .onChanged{ value in
                                    if(value.location.x >= 0 && value.location.x <= fraction2 * totalWidth) {
                                        self.fraction1 = value.location.x / totalWidth
                                    }
                                    //can go over edge
                                    else if(value.location.x <= 0) {
                                        self.fraction1 = 0
                                    }
                                }
                                .onEnded{ value in
                                    if(value.location.x >= 0 && value.location.x <= fraction2 * totalWidth) {
                                        self.fraction1 = value.location.x / totalWidth
                                    }
                                    //can go over edge
                                    else if(value.location.x <= 0) {
                                        self.fraction1 = 0
                                    }
                                    onDrop(self.fraction1, self.fraction2)
                                }
                        )
                    
                    Circle()
                        .fill(mainColor)
                        .frame(width: knobSize, height: knobSize)
                        .offset(x:fraction2 * totalWidth)
                        .gesture(
                            DragGesture()
                                .onChanged{ value in
                                    if(value.location.x <= totalWidth && value.location.x >= fraction1 * totalWidth) {
                                        fraction2 = value.location.x / totalWidth
                                    }
                                    else if (value.location.x >= totalWidth) {
                                        fraction2 = 1.0
                                    }
                                }
                                .onEnded { value in
                                    if(value.location.x <= totalWidth && value.location.x >= fraction1 * totalWidth) {
                                        fraction2 = value.location.x / totalWidth
                                    }
                                    else if (value.location.x >= totalWidth) {
                                        fraction2 = 1.0
                                    }
                                    onDrop(self.fraction1, self.fraction2)
                                }
                        )
                }
                    
            }
        }
        .padding(8)
    }
}

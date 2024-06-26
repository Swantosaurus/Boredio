package com.swantosaurus.boredio.util

import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToInt

//cant use format function in common module
fun Double.format(decimals: Int) : String  {
    val stringBuilder = StringBuilder()
    if(this < 0) {
        stringBuilder.append("-")
    }
    stringBuilder.append(this.absoluteValue.roundToInt())
    if(decimals != 0) {
        stringBuilder.append(".")
    }
    for(i in 1 until  decimals + 1) {
        val int = this.absoluteValue * 10.0.pow(i)
        stringBuilder.append(int.toInt() % 10)
    }
    return stringBuilder.toString()
}
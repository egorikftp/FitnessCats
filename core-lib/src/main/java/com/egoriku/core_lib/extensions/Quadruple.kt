package com.egoriku.core_lib.extensions

class Quadruple<A1, A2, A3, A4>(private val a1: A1, private val a2: A2, private val a3: A3, private val a4: A4) {
    operator fun component1(): A1 = a1
    operator fun component2(): A2 = a2
    operator fun component3(): A3 = a3
    operator fun component4(): A4 = a4
}
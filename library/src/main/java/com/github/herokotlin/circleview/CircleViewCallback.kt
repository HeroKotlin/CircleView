package com.github.herokotlin.circleview

// 外部回调
interface CircleViewCallback {

    fun onTouchDown() {

    }

    fun onTouchUp(inside: Boolean) {

    }

    fun onTouchMove(x: Float, y: Float) {

    }

    fun onTouchEnter() {

    }

    fun onTouchLeave() {

    }

}
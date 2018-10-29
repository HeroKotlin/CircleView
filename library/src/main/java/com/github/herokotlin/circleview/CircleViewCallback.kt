package com.github.herokotlin.circleview

// 外部回调
interface CircleViewCallback {

    fun onTouchDown() {

    }

    fun onTouchUp(inside: Boolean, isLongPress: Boolean) {

    }

    fun onTouchMove(x: Float, y: Float) {

    }

    fun onTouchEnter() {

    }

    fun onTouchLeave() {

    }

    fun onLongPressStart() {

    }

    fun onLongPressEnd() {

    }

}
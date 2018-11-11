package com.github.herokotlin.circleview

// 外部回调
interface CircleViewCallback {

    fun onTouchDown(circleView: CircleView) {

    }

    fun onTouchUp(circleView: CircleView, inside: Boolean, isLongPress: Boolean) {

    }

    fun onTouchMove(circleView: CircleView, x: Float, y: Float) {

    }

    fun onTouchEnter(circleView: CircleView) {

    }

    fun onTouchLeave(circleView: CircleView) {

    }

    fun onLongPressStart(circleView: CircleView) {

    }

    fun onLongPressEnd(circleView: CircleView) {

    }

}
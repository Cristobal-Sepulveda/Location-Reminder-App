package com.udacity.project4.util

import android.view.InputDevice
import android.view.MotionEvent
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.*

fun clickOnCenter(): ViewAction {
    return GeneralClickAction(
        Tap.SINGLE,
        GeneralLocation.VISIBLE_CENTER,
        Press.FINGER,
        InputDevice.SOURCE_MOUSE,
        MotionEvent.BUTTON_PRIMARY)
}

fun clickInCoordinates(x: Int, y: Int): ViewAction {
    return GeneralClickAction(
            Tap.SINGLE,
            CoordinatesProvider { view ->
                val screenPos = IntArray(2)
                view?.getLocationOnScreen(screenPos)
                val screenX = (screenPos[0] + x).toFloat()
                val screenY = (screenPos[1] + y).toFloat()
                floatArrayOf(screenX, screenY)
                },
            Press.FINGER,
            InputDevice.SOURCE_MOUSE,
            MotionEvent.BUTTON_PRIMARY)
}
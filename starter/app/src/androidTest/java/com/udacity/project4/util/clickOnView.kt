package com.udacity.project4.util

import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap

fun clickPercent(pctX: Float, pctY: Float): ViewAction? {
    return GeneralClickAction(
        Tap.SINGLE,
        { view ->
            val screenPos = IntArray(2)
            view.getLocationOnScreen(screenPos)
            val w = view.width
            val h = view.height
            val x = w * pctX
            val y = h * pctY
            val screenX = screenPos[0] + x
            val screenY = screenPos[1] + y
            floatArrayOf(screenX, screenY)
        },
        Press.FINGER
    )
}
package com.obyslab.memory_kotlin

import android.graphics.Camera
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Transformation
import kotlin.math.sin

class FlipAnimation(
    private var fromView: View,
    private var toView: View,
    centerX: Int,
    centerY: Int
) :
    Animation() {
    private var camera: Camera? = null
    private val centerX: Float
    private val centerY: Float
    private var forward = true
    private var visibilitySwapped = false

    init {
        this.centerX = centerX.toFloat()
        this.centerY = centerY.toFloat()

        setDuration(500)

        fillAfter = true
        interpolator = AccelerateDecelerateInterpolator()
    }

    fun reverse() {
        val temp = toView

        forward = false
        toView = fromView
        fromView = temp
    }

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)

        camera = Camera()
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        val radians = Math.PI * interpolatedTime
        var degrees = (180.0 * radians / Math.PI).toFloat()

        if (interpolatedTime >= 0.5f) {
            degrees -= 180f

            if (!visibilitySwapped) {
                fromView.visibility = View.GONE
                toView.visibility = View.VISIBLE
                visibilitySwapped = true
            }
        }

        if (forward) degrees = -degrees

        val matrix = t.matrix

        camera!!.save()
        camera!!.translate(0.0f, 0.0f, (150.0 * sin(radians)).toFloat())
        camera!!.rotateY(degrees)
        camera!!.getMatrix(matrix)
        camera!!.restore()

        matrix.preTranslate(-centerX, -centerY)
        matrix.postTranslate(centerX, centerY)
    }
}
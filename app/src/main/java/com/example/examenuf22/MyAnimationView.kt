package com.example.examenuf22

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MyAnimationView(context: Context) : View(context) {
    companion object {
        private const val RED = -0xf9f0
        private const val GREEN = -0xf900f9
    }

    private val balls = mutableListOf<ShapeHolder>()

    init {
        val colorAnim = ObjectAnimator.ofInt(this, "backgroundColor", RED, GREEN)
        colorAnim.duration = 3000
        colorAnim.setEvaluator(ArgbEvaluator())
        colorAnim.repeatCount = ValueAnimator.INFINITE
        colorAnim.repeatMode = ValueAnimator.REVERSE
        colorAnim.start()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_DOWN && event.action != MotionEvent.ACTION_MOVE) {
            return false
        }

        val newBall = addBall(event.x, height.toFloat() -50f)
        val startY = newBall.getY()
        val endY = 50f
        val h = height.toFloat()
        val eventY = event.y
        var duration = (500 * ((h - eventY) / h)).toInt()

        val bounceAnim = ObjectAnimator.ofFloat(newBall, "y", startY, endY).apply {
            this.duration = duration.toLong() * 5
            interpolator = AccelerateInterpolator()
        }

        val squashAnim1 = ObjectAnimator.ofFloat(newBall, "x", newBall.getX(), newBall.getX() - 25f).apply {
            this.duration = (duration / 4).toLong()
            repeatCount = 1
            repeatMode = ValueAnimator.REVERSE
            interpolator = DecelerateInterpolator()
        }

        val squashAnim2 = ObjectAnimator.ofFloat(newBall, "width", newBall.getWidth(), newBall.getWidth() + 50).apply {
            this.duration = (duration / 4).toLong()
            repeatCount = 1
            repeatMode = ValueAnimator.REVERSE
            interpolator = DecelerateInterpolator()
        }

        val stretchAnim1 = ObjectAnimator.ofFloat(newBall, "y", endY, endY + 25f).apply {
            this.duration = (duration / 4).toLong()
            repeatCount = 1
            repeatMode = ValueAnimator.REVERSE
            interpolator = DecelerateInterpolator()
        }

        val stretchAnim2 = ObjectAnimator.ofFloat(newBall, "height", newBall.getHeight(), newBall.getHeight() - 25).apply {
            this.duration = (duration / 4).toLong()
            repeatCount = 1
            repeatMode = ValueAnimator.REVERSE
            interpolator = DecelerateInterpolator()
        }



        val bouncer = AnimatorSet().apply {
            play(bounceAnim).before(squashAnim1)
            play(squashAnim1).with(squashAnim2)
            play(squashAnim1).with(stretchAnim1)
            play(squashAnim1).with(stretchAnim2)

        }

        val fadeAnim = ObjectAnimator.ofFloat(newBall, "alpha", 1f, 0f).apply {
            duration = 250
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    balls.remove(newBall)
                }
            })
        }

        AnimatorSet().apply {
            play(bouncer).before(fadeAnim)
            start()
        }

        return true
    }

    private fun addBall(x: Float, y: Float): ShapeHolder {
        val circle = OvalShape().apply { resize(50f, 50f) }
        val drawable = ShapeDrawable(circle)
        val shapeHolder = ShapeHolder(drawable).apply {
            setX(x - 25f)
            setY(y - 25f)
        }

        val red = (Math.random() * 255).toInt()
        val blue = (Math.random() * 255).toInt()
        val color = -0x1000000 or (red shl 16) or blue // NO PUC FER QUE POSI EL P**U COLOR

        val paint = drawable.paint
        paint.color = color
        shapeHolder.setPaint(paint)

        balls.add(shapeHolder)
        return shapeHolder
    }

    override fun onDraw(canvas: Canvas) {
        Log.d("OnDraw", "OnDraw")
        balls.forEach { shapeHolder ->
            canvas.save()
            canvas.translate(shapeHolder.getX(), shapeHolder.getY())
            shapeHolder.getShape()?.draw(canvas)
            canvas.restore()
        }
    }
}
package com.anwesh.uiprojects.horizontallineview

/**
 * Created by anweshmishra on 11/08/18.
 */

import android.app.Activity
import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color

val nodes : Int = 5

fun Canvas.drawHLNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val x : Float = w * 0.05f
    val factor : Int = 1 - 2 * (i % 2)
    val gap : Float = (w * 0.9f) / nodes
    paint.strokeWidth = Math.min(w, h) / 60
    paint.strokeCap = Paint.Cap.ROUND
    paint.color = Color.parseColor("#FF9800")
    save()
    translate(x + i * gap, h/2)
    rotate(90f * factor * scale)
    drawLine(0f, 0f, 0f, -gap * factor, paint)
    restore()
}

class HorizontalLineView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    var animationListener : AnimationListener? = null

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    fun addAnimationListener(onComplete : (Int) -> Unit, onReset : (Int) -> Unit) {
        animationListener = AnimationListener(onComplete, onReset)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var prevScale : Float = 0f, var dir : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            this.scale += 0.1f * this.dir
            if (Math.abs(this.scale - this.prevScale) > 1) {
                this.scale = this.prevScale + this.dir
                this.dir = 0f
                this.prevScale = this.scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class HLNode(var i : Int, val state : State = State()) {

        private var next : HLNode? = null

        private var prev : HLNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = HLNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawHLNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : HLNode {
            var curr : HLNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class LinkedHorizontalLine(var i : Int, val state : State = State()) {

        private var root : HLNode = HLNode(0)

        private var curr : HLNode = root

        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : HorizontalLineView) {

        private val hl : LinkedHorizontalLine = LinkedHorizontalLine(0)

        private val animator : Animator = Animator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            hl.draw(canvas, paint)
            animator.animate {
                hl.update {i, scl ->
                    animator.stop()
                    when(scl) {
                        1f -> view.animationListener?.onComplete?.invoke(i)
                        0f -> view.animationListener?.onReset?.invoke(i)
                    }
                }
            }
        }

        fun handleTap() {
            hl.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : HorizontalLineView {
            val view : HorizontalLineView = HorizontalLineView(activity)
            activity.setContentView(view)
            return view
        }
    }

    data class AnimationListener(var onComplete : (Int) -> Unit, var onReset : (Int) -> Unit)
}
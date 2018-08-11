package com.anwesh.uiprojects.linkedhorizontallineview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.anwesh.uiprojects.horizontallineview.HorizontalLineView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view : HorizontalLineView = HorizontalLineView.create(this)
        fullScreen()
        view.addAnimationListener({createToast("animation ${it} completed")}, {createToast("animation ${it} reset")})
    }

    fun createToast(msg : String) {
        Toast.makeText(this, msg,Toast.LENGTH_SHORT).show()
    }
}

fun MainActivity.fullScreen() {
    supportActionBar?.hide()
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}
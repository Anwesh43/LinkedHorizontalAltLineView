package com.anwesh.uiprojects.linkedhorizontallineview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.horizontallineview.HorizontalLineView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HorizontalLineView.create(this)
    }
}

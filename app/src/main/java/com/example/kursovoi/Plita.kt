package com.example.kursovoi

import android.graphics.Color
import android.graphics.Paint
data class Plita(
    var x: Float,
    var y: Float,
    var cvet: Paint = Paint().apply{
        color = listOf(Color.GREEN, Color.RED, Color.YELLOW, Color.BLUE, Color.CYAN, Color.GRAY).random()
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 0f
    }
)
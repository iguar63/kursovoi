package com.example.kursovoi
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
class GameView(context: Context, attrs: AttributeSet?) :
    SurfaceView(context, attrs),
    Runnable {
    private var schet = 0
    private val chek = listOf(Color.GREEN, Color.RED, Color.YELLOW, Color.BLUE, Color.CYAN, Color.GRAY)
    private var chekmas = mutableListOf<Int>()
    private val plita = mutableListOf<Plita>()
    private val movePlita = mutableListOf<Int>()
    private var delay = 5 ; private var counter = 0 // не сбрасываемые переменные
    private val adgePaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 15f};private val plitBorderPaint = Paint().apply {
        color = Color.MAGENTA
        style = Paint.Style.STROKE
        strokeWidth = 15f
    } // краски
    private var pointerId = -1 ; private var isTouching = false ; private var touchX = 0f ;private var touchY = 0f ; private var thread: Thread? = null ; private var isRunning = false // не менять
    init {
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                startGame()
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                stopGame()
            }
        })
    }
    private fun startGame() {
        firstspawn()
        if (thread == null) {
            isRunning = true
            thread = Thread(this)
            thread?.start()
        }
    }
    private fun stopGame() {
        isRunning = false
        thread?.join()
        thread = null
    }
    override fun run() {
        while (isRunning) {
            update()
            draw()
            try {
                Thread.sleep(16)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
    private fun update() {
        swap()
        delete()
        /*for (plit in plita){
            if (plit.cvet.color == Color.BLACK && plita.indexOf(plit) !in 0..5){
                plit.cvet.color = plita[plita.indexOf(plit)-6].cvet.color
            }
        }*/
    }
    private fun delete(){
        var cheked = mutableListOf<Int>()
        var cheking = mutableListOf<Int>()
        for (plit in plita) if(plit.cvet.color == chek[schet]) cheking.add(plita.indexOf(plit))
        for (k in cheking)for (j in cheking) for (i in cheking) if (i - j == 6 && j - k == 6){ cheked.add(i);cheked.add(j);cheked.add(k)}
        for (k in cheking)for (j in cheking) for (i in cheking) if ((i+1)%6 != 0 && j%6 != 0 && (j+1)%6 != 0 && k%6 != 0) if (i - j == 1 && j - k == 1){ cheked.add(i);cheked.add(j);cheked.add(k)}
        for(i in cheked)plita[i].cvet.color = Color.BLACK
        schet++
        if (schet == 6) schet = 0
    }
    private fun draw() {
        val holder = holder ?: return
        val canvas = holder.lockCanvas() ?: return
        canvas.drawColor(Color.BLACK)
        for (plit in plita) {
            canvas.drawRect(plit.y, plit.x, plit.y + 200f, plit.x + 200f, plit.cvet)
        }
        for (i in 0..6) {
            canvas.drawRect(95f+(210f*i),795f,95f+12f+(210f*i),795f+(210f*6), adgePaint)
            canvas.drawRect(95f,795f+(210f*i),95f+(212f*6),795f+12f+(210f*i), adgePaint)
        }
        if (movePlita.isNotEmpty()){
            canvas.drawRect(plita[movePlita[0]].y - 210f ,plita[movePlita[0]].x,plita[movePlita[0]].y +410f,plita[movePlita[0]].x + 210f, plitBorderPaint)
            canvas.drawRect(plita[movePlita[0]].y ,plita[movePlita[0]].x -210f ,plita[movePlita[0]].y + 210f,plita[movePlita[0]].x + 410f, plitBorderPaint)
        }
        canvas.drawRect(0f ,0f,10000f,785f, adgePaint)
        canvas.drawRect(0f ,0f,85f,10000f, adgePaint)
        canvas.drawRect(  plita.last().y+225f ,0f,10000f,10000f, adgePaint)
        canvas.drawRect(  0f ,plita.last().x+225f,10000f,10000f, adgePaint)
        holder.unlockCanvasAndPost(canvas)
    }
    private fun firstspawn() {
        var left = 100f
        var top = 800f
        for (i in 0 until 6){
            for (j in 0 until 6){
                plita.add(Plita(top, left))
                left+=210f
            }
            top+=210f
            left-=210f*6
        }
    }
    private fun swap(){
        if (counter == 0 )choice()
        if (counter == 1) {
            if (touchX > plita[movePlita[0]].y - 210f && touchX < plita[movePlita[0]].y + 410f && touchY > plita[movePlita[0]].x && touchY < plita[movePlita[0]].x + 200f) choice()
            if (touchX > plita[movePlita[0]].y && touchX < plita[movePlita[0]].y + 200f && touchY > plita[movePlita[0]].x - 210f && touchY < plita[movePlita[0]].x + 410f) choice()
        }
        if (counter == 2){
            plita[movePlita[0]].cvet.color = plita[movePlita[1]].cvet.color.also { plita[movePlita[1]].cvet.color = plita[movePlita[0]].cvet.color }
            movePlita.clear()
            counter=0
        }
    }
    private fun choice(){
        for (plit in plita) {
            if (isTouching && touchY > plit.x && touchY < plit.x + 200f && touchX > plit.y && touchX < plit.y + 200f) {
                if (delay == 5) {
                    movePlita.add(plita.indexOf(plit))
                    delay = 0
                    counter++
                    isTouching = false
                }
                delay++
            }
        }
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {

        val action = event.actionMasked
        val index = event.actionIndex

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                pointerId = event.getPointerId(index)
                touchX = event.getX(index)
                touchY = event.getY(index)
                isTouching = true
            }

            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until event.pointerCount) {
                    val id = event.getPointerId(i)
                    if (id == pointerId) {
                        touchX = event.getX(i)
                        touchY = event.getY(i)
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                isTouching = false
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val id = event.getPointerId(index)
                if (id == pointerId) {
                    isTouching = false
                }
            }
        }

        return true
        return false
    }
}
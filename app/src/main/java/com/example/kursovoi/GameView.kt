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
    private val chek = listOf(Color.GREEN, Color.RED, Color.YELLOW, Color.BLUE, Color.CYAN, Color.GRAY);private var chekmas = mutableListOf<Int>(); private val plita = mutableListOf<Plita>();   ; private val movePlita = mutableListOf<Int>() ; private var delay = 5 ; private var counter = 0 // не сбрасываемые переменные
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 50f
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
    } ; private val borderPaint = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 0f
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
        for (i in 0..5) for (plit in plita) if (plit.cvet.color == chek[i]) chekmas.add(plita.indexOf(plit))
        for (i in 0 until chekmas.size){
            for (j in 0 until chekmas.size){
                 //plita.removeAt(j)
            }
        }
        //plita.removeAll(chekmas)
    }
    private fun draw() {
        val holder = holder ?: return
        val canvas = holder.lockCanvas() ?: return
        canvas.drawColor(Color.BLACK)
        for (plit in plita) {
            canvas.drawRect(plit.y, plit.x, plit.y + 200f, plit.x + 200f, plit.cvet)
        }
        for (i in 0..6) {
            canvas.drawRect(95f+(210f*i),795f,95f+12f+(210f*i),795f+(210f*6), borderPaint)
            canvas.drawRect(95f,795f+(210f*i),95f+(212f*6),795f+12f+(210f*i), borderPaint)
        }
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
        for (plit in plita) {
            if (isTouching && touchY > plit.x && touchY < plit.x + 200f && touchX > plit.y && touchX < plit.y + 200f) {
                if (delay == 5) {
                    plita[plita.indexOf(plit)].cvet.strokeWidth = 50f
                    movePlita.add(plita.indexOf(plit))
                    delay = 0
                    counter++
                    isTouching = false
                }
                delay++
            }
        }
        if (counter == 2){
            plita[movePlita[0]].cvet.color = plita[movePlita[1]].cvet.color.also { plita[movePlita[1]].cvet.color = plita[movePlita[0]].cvet.color }
            plita[movePlita[0]].cvet.strokeWidth = 0f;plita[movePlita[1]].cvet.strokeWidth = 0f
            movePlita.clear()
            counter=0
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
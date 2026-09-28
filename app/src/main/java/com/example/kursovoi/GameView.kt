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
    private var light = false
    private var medium = true
    private var hard = false
    private var col = 5
    private var bestScoreHi = 0
    private var bestScore = 0
    private var bestScoreLi = 0
    private var score = 0
    private var terns = 10
    private var gameMenu = true
    private var gameOver = false
    private var obvod = mutableListOf<Int>()
    private var schet = 0
    private var schet1 = 0
    private var del = 0
    private var del2 = 0
    private var pass = false
    private val cheked1 = mutableListOf<Int>()
    private val liChek = listOf(Color.GREEN, Color.RED, Color.YELLOW)
    private val chek = listOf(Color.GREEN, Color.RED, Color.YELLOW, Color.BLUE, Color.CYAN, Color.GRAY)
    private val haChek = listOf(Color.GREEN, Color.RED, Color.YELLOW, Color.BLUE, Color.CYAN, Color.GRAY, Color.LTGRAY, Color.WHITE, Color.DKGRAY)
    private val chekbord = listOf<Int>(5,11,17,23,29,35,6,12,18,24,30)
    private val chekbb = listOf<Int>(4,10,16,22,28,34)
    private val chekbord2 = listOf<Int>(5,11,17,23,29,35,6,12,18,24,30)
    private val chekbb2 = listOf<Int>(4,10,16,22,28,34)
    private val plita = mutableListOf<Plita>()
    private val movePlita = mutableListOf<Int>()
    private var delay = 2
    private var counter = 0
    private var strokePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 15f}
    private var blockPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 15f}
    private val adgePaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 15f}
    private val plitBorderPaint = Paint().apply {
        color = Color.MAGENTA
        style = Paint.Style.STROKE
        strokeWidth = 15f}
    private val BorderPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 15f}
    private val menuTextPaint = Paint().apply {
        color = Color.BLACK
        textSize = 60f
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD }
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 75f
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD }
    private val scorePaint = Paint().apply {
        color = Color.WHITE
        textSize = 40f
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD }
    private val gameOverPaint = Paint().apply {
        color = Color.MAGENTA
        textSize = 200f
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD }
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
            if (!gameMenu)update()
            if (gameMenu)menu()
            draw()
            try {
                Thread.sleep(16)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
    private fun menu(){
        if (isTouching && touchX > 30f  && touchX < width/3f-30f  && touchY > 1200f && touchY < 1400f ) {
            light = true; medium = false; hard = false ; isTouching = false
        }
        if (isTouching && touchX > width/3f+30f  && touchX < width/3f*2-30f  && touchY > 1200f && touchY < 1400f ) {
            light = false; medium = true; hard = false ; isTouching = false
        }
        if (isTouching && touchX > width/3f*2+30f  && touchX < width/3f*3-30f  && touchY > 1200f && touchY < 1400f ) {
            light = false; medium = false; hard = true ; isTouching = false
        }
        if (isTouching && touchX > width/3f+30f  && touchX < width/3f*2-30f  && touchY > 1500f && touchY < 1700f){
            movePlita.clear()
            plita.clear()
            gameOver = false
            firstspawn()
            isTouching=false
            gameMenu = false
            if (light){ terns=20 ; col = 10}
            if (medium){ terns=15 ; col = 5}
            if (hard){ terns=10 ; col = 2}
            counter = 0
            score=0
        }
    }
    private fun update() {
        if (gameOver && isTouching && touchX > width/3f  && touchX < width/3f*2-60f  && touchY > plita.last().x + 300f && touchY < plita.last().x + 500f){
            gameMenu = true
            isTouching=false
        }
        if (terns == 0 ) gameOver = true
        if (!gameOver)swap()
        delete()
        respawn()
        hint()
    }
    private fun hint(){
        if (isTouching && touchX > width / 2f && touchX < width/10*9f && touchY > plita.last().x + 300f && touchY < plita.last().x + 500f && col!=0) {
            col--
            isTouching = false
            for (plit in plita) {
                val cheking1 = mutableListOf<Int>()
                for (plit in plita) if (hard) if (plit.cvet.color == haChek[schet1]) cheking1.add(
                    plita.indexOf(plit)
                )
                for (plit in plita) if (medium) if (plit.cvet.color == chek[schet1]) cheking1.add(
                    plita.indexOf(plit)
                )
                for (plit in plita) if (light) if (plit.cvet.color == liChek[schet1]) cheking1.add(
                    plita.indexOf(plit)
                )
                for (k in cheking1) for (j in cheking1) for (i in cheking1) if (
                    i - j == 6 && j - k == 12 || i - j == 12 && j - k == 6 ||
                    i - j == 6 && j - k == 5 || i - j == 6 && j - k == 7 ||
                    i - j == 5 && j - k == 6 || i - j == 7 && j - k == 6 ||
                    i - j == 5 && i - k == 12 || i - j == 7 && i - k == 6) {
                    cheked1.add(i); cheked1.add(j); cheked1.add(k)
                }
                for (k in cheking1) for (j in cheking1) for (i in cheking1) if (i !in chekbord2 && j !in chekbord2 || j in chekbb2 && i in chekbord2) if (
                    i - j == 1 && j - k == 2 || i - j == 2 && j - k == 1 ||
                    i - j == 1 && j - k == 7 || i - j == 1 && j - k == -5 ||
                    i - j == 7 && j - k == 1 || i - j == -5 && j - k == 1 ||
                    i - j == -7 && i - k == 2 || i - j == 5 && i - k == 2) {
                    cheked1.add(i); cheked1.add(j); cheked1.add(k)
                }
                for (k in cheking1) for (j in cheking1) for (i in cheking1) if (i - j == 6 && j - k == 12 || i - j == 12 && j - k == 6) {
                    cheked1.add(i); cheked1.add(j); cheked1.add(k)
                }

                schet1++
                if (hard) if (schet1 == 9) schet1 = 0
                if (medium) if (schet1 == 6) schet1 = 0
                if (light) if (schet1 == 3) schet1 = 0
            }
            pass = true
        }
    }

    private fun draw() {
        val holder = holder ?: return
        val canvas = holder.lockCanvas() ?: return
        canvas.drawColor(Color.BLACK)
        if (gameMenu){
            blockPaint.color = Color.GREEN
            canvas.drawRect(30f,1200f,width/3f-30f,1400f,blockPaint)
            blockPaint.color = Color.YELLOW
            canvas.drawRect(width/3f+30f,1200f,width/3f*2-30f,1400f,blockPaint)
            blockPaint.color = Color.RED
            canvas.drawRect(width/3f*2+30f,1200f,width/3f*3-30f,1400f,blockPaint)
            if (light)canvas.drawRect(30f,1200f,width/3f-30f,1400f,strokePaint)
            if (medium)canvas.drawRect(width/3f+30f,1200f,width/3f*2-30f,1400f,strokePaint)
            if (hard)canvas.drawRect(width/3f*2+30f,1200f,width/3f*3-30f,1400f,strokePaint)
            canvas.drawRect(width/3f+30f,1500f,width/3f*2-30f,1700f,textPaint)
            canvas.drawText("СТАРТ", width/3f+90f, 1630f, menuTextPaint)
            canvas.drawText("Легкая", width/14f, 1300f, menuTextPaint)
            canvas.drawText("Средняя", width/4f+135f, 1300f, menuTextPaint)
            canvas.drawText("Тяжелая", width/3f+425f, 1300f, menuTextPaint)
            canvas.drawText("3 В РЯД", width / 2f - 375f, height / 2f - 800f, gameOverPaint)
            canvas.drawText("Лучший счет (легко): $bestScoreLi", 100f, 800f, scorePaint)
            canvas.drawText("Лучший счет (средне): $bestScore", 100f, 900f, scorePaint)
            canvas.drawText("Лучший счет (тяжело): $bestScoreHi", 100f, 1000f, scorePaint)
        }
        if (!gameMenu) {
            for (plit in plita) {
                canvas.drawRect(plit.y, plit.x, plit.y + 160f, plit.x + 160f, plit.cvet)
            }
            for (i in 0..6) {
                canvas.drawRect(45f + (160f * i), 795f, 45f + 12f + (160f * i), 795f + (160f * 6), adgePaint)
               canvas.drawRect(45f, 795f + (160f * i), 45f + (162f * 6), 795f + 12f + (160f * i), adgePaint)
            }
            if (pass && cheked1.isNotEmpty()){
                for (i in 0 ..2)canvas.drawRect(plita[cheked1[i]].y, plita[cheked1[i]].x, plita[cheked1[i]].y + 160f, plita[cheked1[i]].x + 160f, BorderPaint)
            }
            if (movePlita.isNotEmpty()) {
                canvas.drawRect(plita[movePlita[0]].y - 160f, plita[movePlita[0]].x, plita[movePlita[0]].y + 320f, plita[movePlita[0]].x + 160f, plitBorderPaint)
                canvas.drawRect(plita[movePlita[0]].y, plita[movePlita[0]].x - 160f, plita[movePlita[0]].y + 160f, plita[movePlita[0]].x + 320f, plitBorderPaint)
            }
            canvas.drawRect(0f, 0f, 10000f, 785f, adgePaint)
            canvas.drawRect(0f, 0f, 35f, 10000f, adgePaint)
            canvas.drawRect(plita.last().y + 175f, 0f, width + 1f, height + 1f, adgePaint)
            canvas.drawRect(0f, plita.last().x + 175f, width + 1f, height + 1f, adgePaint)
            if (light)canvas.drawText("Сложность: Легко", 100f, 450f, textPaint)
            if (medium)canvas.drawText("Сложность: Средне", 100f, 450f, textPaint)
            if (hard)canvas.drawText("Сложность: Тяжело", 100f, 450f, textPaint)
            canvas.drawText("Подсказка: $col", 500f, 600f, textPaint)
            canvas.drawText("Ходы: $terns", 100f, 600f, textPaint)
            canvas.drawText("Счет: $score", 100f, 725f, textPaint)
            canvas.drawRect(width/2f,plita.last().x + 300f, width/10*9f, plita.last().x + 500f,textPaint)
            canvas.drawText("Помощь", width/2f + 90f, plita.last().x + 420f, menuTextPaint)
            if (gameOver) {
                canvas.drawText("GAME!", width / 2f - 265f, height / 2f, gameOverPaint)
                if (light && bestScoreLi< score)bestScoreLi = score
                if (medium && bestScore < score)bestScore = score
                if (hard && bestScoreHi <score)bestScoreHi = score
                canvas.drawRect(width/3f,1800f,width/3f*2-60f,2000f,textPaint)
                canvas.drawText("МЕНЮ", width/3f+60f, 1930f, menuTextPaint)
            }
        }
        holder.unlockCanvasAndPost(canvas)
    }
    private fun respawn(){
        if (del == 5) {
            for (plit in plita) {
                if (plit.cvet.color == Color.BLACK && plita.indexOf(plit) !in 0..5) {
                    plit.cvet.color = plita[plita.indexOf(plit) - 6].cvet.color.also {
                        plita[plita.indexOf(plit) - 6].cvet.color = plit.cvet.color
                    }
                }
            }
            del = 0
        }
        del++
        del2++
        if (del2 == 10){
            for (plit in plita){
                del2 = 0
                if (plit.cvet.color == Color.BLACK && plita.indexOf(plit) in 0..5) {
                    if (hard) {
                        plit.cvet.color = haChek.random()
                    }
                    if (medium) {
                        plit.cvet.color = chek.random()
                    }
                    if (light){
                        plit.cvet.color = liChek.random()
                    }
                }
            }
        }
    }
    private fun delete(){
        for (plit in plita) {
            val cheked = mutableListOf<Int>()
            val cheking = mutableListOf<Int>()
            for (plit in plita) if (hard) if (plit.cvet.color == haChek[schet]) cheking.add(
                plita.indexOf(
                    plit
                )
            )
            for (plit in plita) if (medium) if (plit.cvet.color == chek[schet]) cheking.add(
                plita.indexOf(
                    plit
                )
            )
            for (plit in plita) if (light) if (plit.cvet.color == liChek[schet]) cheking.add(
                plita.indexOf(
                    plit
                )
            )
            for (k in cheking) for (j in cheking) for (i in cheking) if (i - j == 6 && j - k == 6) {
                cheked.add(i); cheked.add(j); cheked.add(k); score += cheked.size * 50
            }
            for (k in cheking) for (j in cheking) for (i in cheking) if (i !in chekbord && j !in chekbord || j in chekbb && i in chekbord) if (i - j == 1 && j - k == 1) {
                cheked.add(i); cheked.add(j); cheked.add(k); score += cheked.size * 50
            }
            for (i in cheked) plita[i].cvet.color = Color.BLACK
            schet++
            if (hard) if (schet == 9) schet = 0
            if (medium) if (schet == 6) schet = 0
            if (light) if (schet == 3) schet = 0
        }
    }
    private fun firstspawn() {
        if (light) {
            var left = 50f
            var top = 800f
            for (i in 0 until 6) {
                for (j in 0 until 6) {
                    plita.add(Plita(top, left,Paint().apply{color = listOf(Color.GREEN, Color.RED, Color.YELLOW).random();style = Paint.Style.FILL_AND_STROKE }))
                    left += 160f
                }
                top += 160f
                left -= 160f * 6
            }
        }
        if (medium){
            var left = 50f
            var top = 800f
            for (i in 0 until 6) {
                for (j in 0 until 6) {
                    plita.add(Plita(top, left,Paint().apply{ color = listOf(Color.GREEN, Color.RED, Color.YELLOW, Color.BLUE, Color.CYAN, Color.GRAY).random();style = Paint.Style.FILL_AND_STROKE }))
                    left += 160f
                }
                top += 160f
                left -= 160f * 6
            }
        }
        if (hard){
            var left = 50f
            var top = 800f
            for (i in 0 until 6) {
                for (j in 0 until 6) {
                    plita.add(Plita(top, left,Paint().apply{color = listOf(Color.GREEN, Color.RED, Color.YELLOW, Color.BLUE, Color.CYAN, Color.GRAY, Color.LTGRAY, Color.DKGRAY, Color.WHITE).random();style = Paint.Style.FILL_AND_STROKE }))
                    left += 160f
                }
                top += 160f
                left -= 160f * 6
            }
        }
    }
    private fun swap(){
        if (counter == 0 )choice()
        if (counter == 1) {
            if (touchX > plita[movePlita[0]].y - 160f && touchX < plita[movePlita[0]].y + 210f*1.5 && touchY > plita[movePlita[0]].x && touchY < plita[movePlita[0]].x + 150f) choice()
            if (touchX > plita[movePlita[0]].y && touchX < plita[movePlita[0]].y + 160f && touchY > plita[movePlita[0]].x - 160f && touchY < plita[movePlita[0]].x + 210f*1.5) choice()
        }
        if (counter == 2){
            plita[movePlita[0]].cvet.color = plita[movePlita[1]].cvet.color.also { plita[movePlita[1]].cvet.color = plita[movePlita[0]].cvet.color }
            if (movePlita[0]!=movePlita[1])terns --
            movePlita.clear()
            pass = false
            cheked1.clear()
            counter=0
        }
    }
    private fun choice(){
        for (plit in plita) {
            if (isTouching && touchY > plit.x && touchY < plit.x + 160f && touchX > plit.y && touchX < plit.y + 160f) {
                if (delay == 2) {
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
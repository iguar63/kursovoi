package com.example.kursovoi

import android.Manifest
import android.R
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.example.kursovoi.Bullet
import com.example.kursovoi.Enemy
import com.example.kursovoi.Player
import kotlin.random.Random

class GameView(context: Context, attrs: AttributeSet?) :
    SurfaceView(context, attrs),
    Runnable {

    // ============================================================
    // ИГРОВЫЕ ОБЪЕКТЫ (НЕ ТРОГАТЬ)
    // ============================================================

    private val player = Player(
        x = 0f,
        y = 0f,
        width = 80f,
        height = 100f
    )

    private val enemies = mutableListOf<Enemy>()
    private val bullets = mutableListOf<Bullet>()

    // ============================================================
    // ПАРАМЕТРЫ ИГРЫ (МОЖНО МЕНЯТЬ)
    // ============================================================

    private var score = 0
    private var gameOver = false
    private var enemySpawnCounter = 0
    private val ENEMY_SPAWN_DELAY = 30

    // ============================================================
    // КИСТИ ДЛЯ РИСОВАНИЯ (РАСКОММЕНТИРОВАТЬ В ЗАДАНИИ 3)
    // ============================================================


    private val playerPaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
        isAntiAlias = true
    }



    private val enemyPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        isAntiAlias = true
    }



    private val bulletPaint = Paint().apply {
        color = Color.YELLOW
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val contur = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
    }
    var lefta = FloatArray(6){0.0f}
    var topa = FloatArray(6){0.0f}

    var left = 100f
    var top = 800f
    var size = 200f // Важный момент: для квадрата ширина и высота должны быть равны


    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 50f
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
    }



    private val gameOverPaint = Paint().apply {
        color = Color.RED
        textSize = 80f
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
    }


    // ============================================================
    // УПРАВЛЕНИЕ КАСАНИЯМИ (НЕ ТРОГАТЬ)
    // ============================================================

    private var pointerId = -1
    private var isTouching = false
    private var touchX = 0f
    private var touchY = 0f

    // ============================================================
    // ПОТОК ДЛЯ ИГРОВОГО ЦИКЛА (РАСКОММЕНТИРОВАТЬ В ЗАДАНИИ 2)
    // ============================================================

    private var thread: Thread? = null
    private var isRunning = false

    // ============================================================
    // ИНИЦИАЛИЗАЦИЯ (РАСКОММЕНТИРОВАТЬ В ЗАДАНИИ 4)
    // ============================================================

    init {
        for (i in 0 until 6){
            topa[i] = top
            lefta[i] = left
            top+=210
            left+=210
        }
        post {
            player.x = width / 2f
            player.y = height - 200f
        }

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

    // ============================================================
    // МЕТОДЫ ЖИЗНЕННОГО ЦИКЛА (РАСКОММЕНТИРОВАТЬ В ЗАДАНИИ 5)
    // ============================================================

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

    // ============================================================
    // ИГРОВОЙ ЦИКЛ (РАСКОММЕНТИРОВАТЬ В ЗАДАНИЯХ 6-8)
    // ============================================================

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

        if (gameOver) return

        // Движение корабля
        if (isTouching) {
            val dx = touchX - player.x
            val dy = touchY - player.y
            val distance = Math.hypot(dx.toDouble(), dy.toDouble()).toFloat()
            if (distance > 5f) {
                val speed = 20f
                player.x += (dx / distance) * speed
                player.y += (dy / distance) * speed
            }
        }

        // Границы корабля
        val halfWidth = player.width / 2f
        val halfHeight = player.height / 2f
        if (player.x - halfWidth < 0) player.x = halfWidth
        if (player.x + halfWidth > width) player.x = width - halfWidth
        if (player.y - halfHeight < 0) player.y = halfHeight
        if (player.y + halfHeight > height) player.y = height - halfHeight

        // Пули
        val bulletsToRemove = mutableListOf<Bullet>()
        for (bullet in bullets) {
            bullet.y -= 20f
            if (bullet.y < 0) {
                bulletsToRemove.add(bullet)
            }
        }
        bullets.removeAll(bulletsToRemove)

        // Враги
        val enemiesToRemove = mutableListOf<Enemy>()
        for (enemy in enemies) {
            enemy.y += enemy.speed
            if (enemy.y - enemy.size / 2 > height) {
                enemiesToRemove.add(enemy)
                gameOver = true
            }
        }
        enemies.removeAll(enemiesToRemove)

        // Столкновения
        val bulletsHit = mutableListOf<Bullet>()
        val enemiesHit = mutableListOf<Enemy>()
        for (bullet in bullets) {
            for (enemy in enemies) {
                val distance = Math.hypot(
                    (bullet.x - enemy.x).toDouble(),
                    (bullet.y - enemy.y).toDouble()
                ).toFloat()
                if (distance < 10f + enemy.size / 2) {
                    bulletsHit.add(bullet)
                    enemiesHit.add(enemy)
                    score++
                    vibrate()
                }
            }
        }
        bullets.removeAll(bulletsHit)
        enemies.removeAll(enemiesHit)

        // Создание врагов
        enemySpawnCounter++
        if (enemySpawnCounter >= ENEMY_SPAWN_DELAY) {
            spawnEnemy()
            enemySpawnCounter = 0
        }

    }

    private fun draw() {

        val holder = holder ?: return
        val canvas = holder.lockCanvas() ?: return

        canvas.drawColor(Color.BLACK)

        // Корабль
        val halfWidth = player.width / 2f
        val halfHeight = player.height / 2f
        canvas.drawRoundRect(
            player.x - halfWidth,
            player.y - halfHeight,
            player.x + halfWidth,
            player.y + halfHeight,
            20f, 20f,
            playerPaint
        )
        val cockpitPaint = Paint().apply {
            color = Color.CYAN
            style = Paint.Style.FILL
        }
        canvas.drawCircle(player.x, player.y - halfHeight * 0.3f, 15f, cockpitPaint)

        // Враги
        for (enemy in enemies) {
            canvas.drawCircle(enemy.x, enemy.y, enemy.size / 2, enemyPaint)
        }
        for (j in 0 until 6) {
            for (i in 0 until 6) {
                canvas.drawRect(lefta[i], topa[j], lefta[i] + size, topa[j] + size, contur)
            }
        }
        // Пули
        for (bullet in bullets) {
            canvas.drawCircle(bullet.x, bullet.y, 10f, bulletPaint)
        }

        // Интерфейс
        canvas.drawText("Счёт: $score", 30f, 80f, textPaint)

        // Game Over
        if (gameOver) {
            canvas.drawText("GAME OVER", width / 2f - 200f, height / 2f, gameOverPaint)
        }

        holder.unlockCanvasAndPost(canvas)

    }

    // ============================================================
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ (РАСКОММЕНТИРОВАТЬ В ЗАДАНИИ 9)
    // ============================================================


    private fun spawnEnemy() {
        val x = Random.nextFloat() * (width - 100f) + 50f
        val y = -50f
        val size = 60f
        val speed = Random.nextFloat() * 5f + 3f
        enemies.add(Enemy(x, y, size, speed))
    }



    private fun vibrate() {
        val vibrator = ContextCompat.getSystemService(context, Vibrator::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator?.vibrate(50)
        }
    }



    private fun shoot() {
        if (!gameOver) {
            bullets.add(Bullet(player.x, player.y))
        }
    }


    // ============================================================
    // ОБРАБОТКА КАСАНИЙ (РАСКОММЕНТИРОВАТЬ В ЗАДАНИИ 10)
    // ============================================================

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

            MotionEvent.ACTION_POINTER_DOWN -> {
                shoot()
                vibrate()
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
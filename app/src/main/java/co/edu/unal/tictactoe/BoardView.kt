package co.edu.unal.tictactoe

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View


class BoardView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    companion object {
        const val GRID_WIDTH: Float = 6F
    }

    private var mHumanBitmap: Bitmap? = null
    private var mComputerBitmap: Bitmap? = null
    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        strokeWidth = GRID_WIDTH
    }

    var mGame: TicTacToeGame? = null

    val boardCellWidth: Int
        get() {
            return width / 3
        }

    val boardCellHeight: Int
        get() {
            return height / 3
        }

    init {
        mHumanBitmap = BitmapFactory.decodeResource(resources, R.drawable.x)
        mComputerBitmap = BitmapFactory.decodeResource(resources, R.drawable.o)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Determine the width and height of the View
        val boardWidth = width.toFloat()
        val boardHeight = height.toFloat()

        val cellWidth = boardWidth / 3
        canvas?.let {
            // Draw the two vertical board lines
            it.drawLine(cellWidth, 0F, cellWidth, boardHeight, mPaint)
            it.drawLine(cellWidth * 2, 0F, cellWidth * 2, boardHeight, mPaint)

            // Draw the two horizontal board lines
            it.drawLine(0F, cellWidth, boardWidth, cellWidth, mPaint)
            it.drawLine(0F, cellWidth * 2, boardWidth, cellWidth * 2, mPaint)

            for (i in 0 until TicTacToeGame.BOARD_SIZE) {
                val col = i % 3
                val row = i / 3

                val left = (cellWidth * col + (GRID_WIDTH * (col - 1))).toInt()
                val top = (cellWidth * row + (GRID_WIDTH * (col - 1))).toInt()
                val right = left + cellWidth.toInt()
                val bottom = top + cellWidth.toInt()

                mGame?.let { game ->
                    val correctBitmap = when (game.getBoardOccupant(i)) {
                        TicTacToeGame.HUMAN_PLAYER -> mHumanBitmap
                        TicTacToeGame.COMPUTER_PLAYER -> mComputerBitmap
                        else -> null
                    }
                    correctBitmap?.let { bitmap ->
                        it.drawBitmap(bitmap, null, Rect(left, top, right, bottom), null)
                    }
                }
            }
        }
    }
}
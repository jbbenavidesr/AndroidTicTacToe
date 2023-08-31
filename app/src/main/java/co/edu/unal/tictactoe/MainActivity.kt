package co.edu.unal.tictactoe

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private var mGameOver = false
    private val mGame = TicTacToeGame()
    private lateinit var mBoardButtons: List<Button>
    private lateinit var mInfoTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.mGame.clearBoard()

        this.mBoardButtons = listOf<Button>(
            findViewById<Button>(R.id.one),
            findViewById<Button>(R.id.two),
            findViewById<Button>(R.id.three),
            findViewById<Button>(R.id.four),
            findViewById<Button>(R.id.five),
            findViewById<Button>(R.id.six),
            findViewById<Button>(R.id.seven),
            findViewById<Button>(R.id.eight),
            findViewById<Button>(R.id.nine),
        )

        this.mInfoTextView = findViewById<TextView>(R.id.information)

        this.startNewGame()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menu.add("New Game")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startNewGame()
        return true
    }

    private fun startNewGame() {
        this.mGame.clearBoard()
        this.mGameOver = false

        for ((i, button) in this.mBoardButtons.withIndex()) {
            button.text = ""
            button.isEnabled = true
            button.setOnClickListener {
                if (button.isEnabled and !this.mGameOver) {
                    this.setMove(TicTacToeGame.HUMAN_PLAYER, i)
                    this.mGameOver = true
                    var winner = this.mGame.checkForWinner()
                    if (winner == 0) {
                        mInfoTextView.text = getString(R.string.turn_computer)
                        var move = this.mGame.getComputerMove()
                        this.setMove(TicTacToeGame.COMPUTER_PLAYER, move)
                        winner = this.mGame.checkForWinner()
                    }

                    when (winner) {
                        0 -> {
                            mInfoTextView.text = getString(R.string.turn_human)
                            this.mGameOver = false
                        }
                        1 -> mInfoTextView.text = getString(R.string.result_tie)
                        2 -> mInfoTextView.text = getString(R.string.result_human_wins)
                        else -> mInfoTextView.text = getString(R.string.result_computer_wins)
                    }
                }
            }
        }

        this.mInfoTextView.text = getString(R.string.first_human)

    }

    private fun setMove(player: Char, location: Int) {
        this.mGame.setMove(player, location)
        this.mBoardButtons[location].isEnabled = false
        this.mBoardButtons[location].text = player.toString()
        if (player == TicTacToeGame.HUMAN_PLAYER)
            this.mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0))
        else
            this.mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0))
    }
}


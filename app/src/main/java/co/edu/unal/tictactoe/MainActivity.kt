package co.edu.unal.tictactoe

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment

class MainActivity : AppCompatActivity(),
    SelectDifficultyDialogFragment.SelectDifficultyDialogListener {

    private var mGameOver = false
    private val mGame = TicTacToeGame()
    private lateinit var mBoardButtons: List<Button>
    private lateinit var mInfoTextView: TextView
    private lateinit var mDifficultyTextView: TextView
    private lateinit var mToolbar: Toolbar

    private var mSelectDifficultyDialog = SelectDifficultyDialogFragment()

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
        this.mDifficultyTextView = findViewById<TextView>(R.id.tv_difficulty)
        this.mToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(mToolbar)

        mSelectDifficultyDialog.show(supportFragmentManager, "difficulty_select")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.new_game -> {
                mSelectDifficultyDialog.show(supportFragmentManager, "difficulty_select")
                true
            }
            R.id.quit -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    override fun onDifficultySelected(dialog: DialogFragment, id: Int) {
        mGame.mDfficultyLevel = when(id) {
            0 -> TicTacToeGame.DifficultyLevel.Easy
            1 -> TicTacToeGame.DifficultyLevel.Hard
            2 -> TicTacToeGame.DifficultyLevel.Expert
            else -> mGame.mDfficultyLevel
        }

        mDifficultyTextView.text = "Difficulty: ${mGame.mDfficultyLevel}"
        startNewGame()
        dialog.dismiss()
    }
}

class SelectDifficultyDialogFragment : DialogFragment() {
    private lateinit var listener: SelectDifficultyDialogListener

    interface SelectDifficultyDialogListener {
        fun onDifficultySelected(dialog: DialogFragment, id: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as SelectDifficultyDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement SelectDifficultyDialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setTitle("Select difficulty")
                .setItems(
                    arrayOf("Easy", "Hard", "Expert")
                ) { _, which -> listener.onDifficultySelected(this, which)}

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }
}

package co.edu.unal.tictactoe

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment

class MainActivity : AppCompatActivity(),
    SelectDifficultyDialogFragment.SelectDifficultyDialogListener,
    OnTouchListener {

    private var mGameOver = false
    private val mGame = TicTacToeGame()
    private lateinit var mInfoTextView: TextView
    private lateinit var mDifficultyTextView: TextView
    private lateinit var mToolbar: Toolbar
    private lateinit var mBoardView: BoardView

    private var mSelectDifficultyDialog = SelectDifficultyDialogFragment()

    private lateinit var mHumanMediaPlayer: MediaPlayer
    private lateinit var mComputerMediaPlayer: MediaPlayer

    private var mHumanWins = 0
    private var mComputerWins = 0
    private var mTies = 0

    private lateinit var mHumanWinsTextView: TextView
    private lateinit var mComputerWinsTextView: TextView
    private lateinit var mTiesTextView: TextView

    private lateinit var mPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE)

        mHumanWins = mPrefs.getInt("mHumanWins", 0)
        mComputerWins = mPrefs.getInt("mComputerWins", 0)
        mTies = mPrefs.getInt("mTies", 0)

        this.mGame.clearBoard()

        this.mBoardView = findViewById<BoardView>(R.id.board)
        this.mBoardView.mGame = mGame
        this.mBoardView.setOnTouchListener(this)

        this.mInfoTextView = findViewById<TextView>(R.id.information)
        this.mDifficultyTextView = findViewById<TextView>(R.id.tv_difficulty)
        this.mToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(mToolbar)

        mHumanWinsTextView = findViewById<TextView>(R.id.tv_human_wins)
        mComputerWinsTextView = findViewById<TextView>(R.id.tv_computer_wins)
        mTiesTextView = findViewById<TextView>(R.id.tv_ties)

        mHumanWinsTextView.text = "Human: $mHumanWins"
        mComputerWinsTextView.text = "Computer: $mComputerWins"
        mTiesTextView.text = "Ties: $mTies"

        if(savedInstanceState == null){
            mSelectDifficultyDialog.show(supportFragmentManager, "difficulty_select")
        }
    }

    override fun onResume() {
        super.onResume()

        mHumanMediaPlayer = MediaPlayer.create(applicationContext, R.raw.human_turn)
        mComputerMediaPlayer = MediaPlayer.create(applicationContext, R.raw.computer_turn)
    }

    override fun onPause() {
        super.onPause()

        mHumanMediaPlayer.release()
        mComputerMediaPlayer.release()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_game -> {
                mSelectDifficultyDialog.show(supportFragmentManager, "difficulty_select")
                true
            }

            R.id.reset_scores -> {
                mComputerWins = 0
                mHumanWins = 0
                mTies = 0

                mHumanWinsTextView.text = "Human: $mHumanWins"
                mComputerWinsTextView.text = "Computer: $mComputerWins"
                mTiesTextView.text = "Ties: $mTies"
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStop() {
        super.onStop()

        val ed = mPrefs.edit()
        ed.putInt("mComputerWins", mComputerWins)
        ed.putInt("mHumanWins", mHumanWins)
        ed.putInt("mTies", mTies)
        ed.commit()
    }

    private fun startNewGame() {
        this.mGame.clearBoard()
        this.mGameOver = false
        this.mBoardView.invalidate()
        this.mInfoTextView.text = getString(R.string.first_human)

    }

    private fun setMove(player: Char, location: Int): Boolean {
        if (mGame.setMove(player, location)) {
            if (player == TicTacToeGame.HUMAN_PLAYER) {
                mHumanMediaPlayer.start()
            } else {
                mComputerMediaPlayer.start()
            }

            mBoardView.invalidate()
            return true
        }
        return false
    }

    override fun onDifficultySelected(dialog: DialogFragment, id: Int) {
        mGame.mDifficultyLevel = when (id) {
            0 -> TicTacToeGame.DifficultyLevel.Easy
            1 -> TicTacToeGame.DifficultyLevel.Hard
            2 -> TicTacToeGame.DifficultyLevel.Expert
            else -> mGame.mDifficultyLevel
        }

        mDifficultyTextView.text = "Difficulty: ${mGame.mDifficultyLevel}"
        startNewGame()
        dialog.dismiss()
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        event?.let {
            // Determine the cell that was touched
            val col = it.x.toInt() / mBoardView.boardCellWidth
            val row = it.y.toInt() / mBoardView.boardCellHeight
            val pos = row * 3 + col

            if (!this.mGameOver and setMove(TicTacToeGame.HUMAN_PLAYER, pos)) {
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

                    1 -> {
                        mInfoTextView.text = getString(R.string.result_tie)
                        mTies++
                    }
                    2 -> {
                        mInfoTextView.text = getString(R.string.result_human_wins)
                        mHumanWins++
                    }
                    else -> {
                        mInfoTextView.text = getString(R.string.result_computer_wins)
                        mComputerWins++
                    }
                }

                mHumanWinsTextView.text = "Human: $mHumanWins"
                mComputerWinsTextView.text = "Computer: $mComputerWins"
                mTiesTextView.text = "Ties: $mTies"
            }
        }

        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putCharArray("board", mGame.boardState)
        outState.putBoolean("mGameOver", mGameOver)
        outState.putCharSequence("info", mInfoTextView.text)
        outState.putSerializable("difficulty", mGame.mDifficultyLevel)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        mGame.boardState = savedInstanceState.getCharArray("board")!!
        mGameOver = savedInstanceState.getBoolean("mGameOver")
        mInfoTextView.text = savedInstanceState.getCharSequence("info")
        mGame.mDifficultyLevel = savedInstanceState.getSerializable("difficulty") as TicTacToeGame.DifficultyLevel
        mDifficultyTextView.text = "Difficulty: ${mGame.mDifficultyLevel}"
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
                ) { _, which -> listener.onDifficultySelected(this, which) }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

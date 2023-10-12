package co.edu.unal.tictactoe

import kotlin.random.Random

class TicTacToeGame {

    companion object {
        const val BOARD_SIZE = 9
        const val HUMAN_PLAYER = 'X'
        const val COMPUTER_PLAYER = 'O'
        const val OPEN_SPOT = ' '
    }

    public enum class DifficultyLevel {
        Easy, Hard, Expert
    }

    var mDifficultyLevel = DifficultyLevel.Expert

    private val mBoard = CharArray(BOARD_SIZE) { OPEN_SPOT }

    var boardState: CharArray
        get() {
            return mBoard
        }
        set(value) {
            for (i in 0 until BOARD_SIZE) {
                mBoard[i] = value[i]
            }
        }

    /** Clear the board of all X's and O's by setting all spots to OPEN_SPOT. */
    fun clearBoard() {
        for (i in mBoard.indices) {
            mBoard[i] = OPEN_SPOT
        }
    }

    fun getBoardOccupant(i: Int): Char {
        return mBoard[i]
    }

    /** Set the given player at the given location on the game board.
     * The location must be available, or the board will not be changed.
     *
     * @param player - The HUMAN_PLAYER or COMPUTER_PLAYER
     * @param location - The location (0-8) to place the move
     */
    public fun setMove(player: Char, location: Int): Boolean {
        if ((mBoard[location] == OPEN_SPOT) and (checkForWinner() == 0)) {
            mBoard[location] = player
            return true
        }
        return false
    }

    /**
     * Check for a winner and return a status value indicating who has won.
     * @return Return 0 if no winner or tie yet, 1 if it's a tie, 2 if X won,
     * or 3 if O won.
     */
    public fun checkForWinner(): Int {
        // Check horizontal wins
        for (i in 0..6 step 3) {
            if (mBoard[i] == HUMAN_PLAYER &&
                mBoard[i + 1] == HUMAN_PLAYER &&
                mBoard[i + 2] == HUMAN_PLAYER
            )
                return 2

            if (mBoard[i] == COMPUTER_PLAYER &&
                mBoard[i + 1] == COMPUTER_PLAYER &&
                mBoard[i + 2] == COMPUTER_PLAYER
            )
                return 3
        }


        // Check vertical wins
        for (i in 0..2) {
            if (mBoard[i] == HUMAN_PLAYER &&
                mBoard[i + 3] == HUMAN_PLAYER &&
                mBoard[i + 6] == HUMAN_PLAYER
            )
                return 2
            if (mBoard[i] == COMPUTER_PLAYER &&
                mBoard[i + 3] == COMPUTER_PLAYER &&
                mBoard[i + 6] == COMPUTER_PLAYER
            )
                return 3
        }

        // Check for diagonal wins
        if ((mBoard[0] == HUMAN_PLAYER &&
                    mBoard[4] == HUMAN_PLAYER &&
                    mBoard[8] == HUMAN_PLAYER)
            ||
            (mBoard[2] == HUMAN_PLAYER &&
                    mBoard[4] == HUMAN_PLAYER &&
                    mBoard[6] == HUMAN_PLAYER)
        )
            return 2
        if ((mBoard[0] == COMPUTER_PLAYER &&
                    mBoard[4] == COMPUTER_PLAYER &&
                    mBoard[8] == COMPUTER_PLAYER)
            ||
            (mBoard[2] == COMPUTER_PLAYER &&
                    mBoard[4] == COMPUTER_PLAYER &&
                    mBoard[6] == COMPUTER_PLAYER)
        )
            return 3

        // Check for tie
        for (i in 0 until BOARD_SIZE) {
            // If we find a number, then no one has won yet
            if (
                mBoard[i] != HUMAN_PLAYER &&
                mBoard[i] != COMPUTER_PLAYER
            )
                return 0
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1

    }

    /**
     * Return the best move for the computer to make. You must call setMove()
     * to actually make the computer move to that location.
     *
     * @return The best move for the computer to make (0-8).
     */
    public fun getComputerMove(): Int {
        var move = -1

        when (this.mDifficultyLevel) {
            DifficultyLevel.Easy -> move = getRandomMove()
            DifficultyLevel.Hard -> {
                move = getWinningMove()
                if (move == -1) {
                    move = getRandomMove()
                }
            }

            DifficultyLevel.Expert -> {
                move = getWinningMove()
                if (move == -1) {
                    move = getBlockingMove()
                }
                if (move == -1) {
                    move = getRandomMove()
                }
            }
        }

        return move
    }

    private fun getWinningMove(): Int {

        for (i in 0 until BOARD_SIZE) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                var current = mBoard[i]
                mBoard[i] = COMPUTER_PLAYER
                if (checkForWinner() == 3) {
                    return i
                } else {
                    mBoard[i] = current
                }
            }
        }

        return -1
    }

    private fun getBlockingMove(): Int {
        for (i in 0 until BOARD_SIZE) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                var current = mBoard[i]
                mBoard[i] = HUMAN_PLAYER
                if (checkForWinner() == 2) {
                    mBoard[i] = COMPUTER_PLAYER
                    return i
                } else {
                    mBoard[i] = current
                }
            }
        }
        return -1
    }

    private fun getRandomMove(): Int {
        var move = -1

        // Generate random move
        do {
            move = Random.nextInt(BOARD_SIZE)
        } while (mBoard[move] == HUMAN_PLAYER || mBoard[move] == COMPUTER_PLAYER)

        mBoard[move] = COMPUTER_PLAYER
        return move
    }


}
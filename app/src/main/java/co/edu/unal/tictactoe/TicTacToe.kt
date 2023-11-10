package co.edu.unal.tictactoe

import kotlin.math.abs

typealias ErrorHandler = (Exception) -> Unit



interface Renderer {
    fun render(gameState: GameState)
}

interface Player {
    val mark: Mark

    fun makeMove(gameState:GameState): GameState {
        if (mark == gameState.currentMark) {
            return getMove(gameState)?.let { move ->
                move.afterState
            } ?: throw InvalidMove("No more possible moves")
        } else {
            throw InvalidMove("It's the other player's turn")
        }
    }

    fun getMove(gameState: GameState): Move?
}

class GameState (
    val grid: Grid, val startingMark: Mark = Mark.CROSS
) {

    companion object {
        private val WINNING_COMBINATIONS = listOf(
            Triple(0, 1, 2),
            Triple(3, 4, 5),
            Triple(6, 7, 8),
            Triple(0, 3, 6),
            Triple(1, 4, 7),
            Triple(2, 5, 8),
            Triple(0, 4, 8),
            Triple(2, 4, 6)
        )
    }
    init {
        validateGameState()
    }

    val currentMark: Mark
        get() = if (grid.xCount == grid.oCount) startingMark else startingMark.other

    val gameNotStarted: Boolean
        get() = grid.emptyCount == Grid.GRID_SIZE

    val gameOver: Boolean
        get() = winner != null || tie

    val tie: Boolean
        get() = winner == null && grid.emptyCount == 0

    val winner: Mark?
        get() {

            for (combination in WINNING_COMBINATIONS) {
                val (a, b , c) = combination
                if (grid.cells[a] == grid.cells[b] && grid.cells[b] == grid.cells[c] && grid.cells[a] != null ) {
                    return grid.cells[a]
                }
            }

            return null
        }

    val winningCells: Triple<Int, Int, Int>?
        get() {
            for (combination in WINNING_COMBINATIONS) {
                val (a, b , c) = combination
                if (grid.cells[a] == grid.cells[b] && grid.cells[b] == grid.cells[c] && grid.cells[a] != null ) {
                    return combination
                }
            }

            return null
        }

    val possibleMoves: List<Move>
        get() = grid.cells.indices.filter { grid.cells[it] == null}.map { makeMoveTo(it) }


    fun makeMoveTo(index: Int): Move {
        if (grid.cells[index] != null) {
            throw InvalidMove("Cell is not empty")
        }

        return Move(
            mark = currentMark,
            cellIndex = index,
            beforeState = this,
            afterState = GameState(
                Grid(
                    grid.cells.copyOf().apply { set(index, currentMark) }
                ),
                startingMark
            )
        )
    }

    fun evaluateScore(mark: Mark): Int {
        if (gameOver) {
            return when {
                tie -> 0
                winner == mark -> 1
                else -> -1
            }
        } else {
            throw UnknownGameScore("Game is not over yet")
        }
    }

    fun makeRandomMove(): Move? {
        return try {
            possibleMoves.random()
        } catch (e: NoSuchElementException) {
            null
        }
    }


    private fun validateGameState() {
        validateNumberOfMarks()
        validateStartingMark()
        validateWinner()
    }

    private fun validateNumberOfMarks() {
        if(abs(grid.xCount - grid.oCount) > 1) {
            throw InvalidGameState("Wrong number of Xs and Os")
        }
    }

    private fun validateStartingMark() {
        if (
            (grid.xCount > grid.oCount && startingMark != Mark.CROSS)
            || (grid.oCount > grid.xCount && startingMark != Mark.NAUGHT)
            ) {
            throw InvalidGameState("Wrong starting mark")
        }
    }

    private fun validateWinner() {
        when (winner) {
            Mark.CROSS -> {
                if (startingMark == Mark.CROSS) {
                    if (grid.xCount <= grid.oCount) {
                        throw InvalidGameState("Wrong number of Xs")
                    }
                } else {
                    if (grid.xCount != grid.oCount) {
                        throw InvalidGameState("Wrong number of Xs")
                    }
                }
            }
            Mark.NAUGHT -> {
                if (startingMark == Mark.NAUGHT) {
                    if (grid.oCount <= grid.xCount) {
                        throw InvalidGameState("Wrong number of Os")
                    }
                } else {
                    if (grid.oCount != grid.xCount) {
                        throw InvalidGameState("Wrong number of Os")
                    }
                }
            }
            else -> {}
        }
    }

}

enum class Mark(val symbol: String) {
    CROSS("X"),
    NAUGHT("O");

    val other: Mark
        get() = if (this == NAUGHT) CROSS else NAUGHT
}

class Grid(val cells: Array<Mark?> = arrayOfNulls(GRID_SIZE) ) {

    companion object {
        const val GRID_SIZE = 9
    }

    init {
        validateGrid()
    }

    val xCount: Int
        get() = cells.count { it == Mark.CROSS}

    val oCount: Int
        get() = cells.count { it == Mark.NAUGHT}

    val emptyCount: Int
        get() = cells.count { it == null}


    fun cellsToString(): String {
        return this.cells.joinToString("") { it?.symbol ?: " " }
    }
    private fun validateGrid() {
        if (cells.size != GRID_SIZE) {
            throw Exception("Must contain $GRID_SIZE cells")
        }
    }
}

data class Move (
    val mark: Mark,
    val cellIndex: Int,
    val beforeState: GameState,
    val afterState: GameState,
)

class InvalidMove(message: String) : Exception(message)
class UnknownGameScore(message: String) : Exception(message)

class InvalidGameState(message: String) : Exception(message)
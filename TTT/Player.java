package TTT;
import java.util.*;

public class Player {
    private static final int MAX_DEPTH = 3;
    private static final int[] BASES = new int[]{1, 11, 111, 1111};

    /**
     * Performs a move
     *
     * @param gameState
     *            the current state of the board
     * @param deadline
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */
    public GameState play(final GameState gameState, final Deadline deadline) {
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        if (nextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(gameState, new Move());
        }

        /**
         * Here you should write your algorithms to get the best next move, i.e.
         * the best next state. This skeleton returns a random move instead.
         */

        GameState best = bestMove(nextStates, gameState.getNextPlayer());
        return best;

        // do the move with the highest score

        // Inside nextStates is our model
        //Random random = new Random();
        //return nextStates.elementAt(random.nextInt(nextStates.size()));

    }

    /**
     * Calls the minimax algorithm on each possible next move and returns the best one.
     * @param nextStates
     * @param player: the player whose turn it is in nextStates (opponent on kattis)
     * @return
     */
    private GameState bestMove(Vector<GameState> nextStates, int player) {
        int maxScore = Integer.MIN_VALUE;
        int tempMax;
        GameState bestState = null; // the state of the best move
        for (GameState nextState : nextStates) {
            tempMax = minimax(nextState, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, player);
            if (tempMax > maxScore) {
                maxScore = tempMax;
                bestState = nextState;
            }
        }
        return bestState;
    }

    /**
     * The minimax algorithm with alpha-beta pruning.
     * @param gameState: the state we want to evaluate.
     * @param player: the current player
     * @return the score of gameState
     */
    private int minimax(GameState gameState, int depth, int alpha, int beta, int player) {
        int v; // Our evaluation result
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        if(depth == 0 || nextStates.size() == 0) {
            v = evaluate(player, gameState);
        }
        else if(player == Constants.CELL_X){
            v = Integer.MIN_VALUE;
            for(GameState nextState : nextStates){
                v = Math.max(v, minimax(nextState, depth - 1, alpha, beta, Constants.CELL_O));
                alpha = Math.max(alpha, v);
                if(beta <= alpha){
                    break;
                }
            }
        }
        else{
            v = Integer.MAX_VALUE;
            for(GameState nextState : nextStates){
                v = Math.min(v, minimax(nextState, depth - 1, alpha, beta, Constants.CELL_X));
                beta = Math.min(beta, v);
                if(beta <= alpha) {
                    break;
                }
            }
        }
        return v;
    }

    // TODO:    I think the problem when increasing the depth to 1 is that this function
    // TODO:    doesn't care about the sign, it just does minimax from the perspective
    // TODO:    of the player argument. We should instead make CELL_X always positive, and O
    // TODO:    always negative.
    private int evaluate(int player, GameState gameState) {
        int score = 0;
        int opponent;
        if(player == Constants.CELL_X)
            opponent = Constants.CELL_O;
        else
            opponent = Constants.CELL_X;
        int[] pInLine = new int[GameState.BOARD_SIZE];
        int[] oInLine = new int[GameState.BOARD_SIZE];
        // Idea for scoring function. Count the pieces in open lines of victory.
        // I.e for all lines on the board with only the player's pieces on it, count
        // them and make that the score. Also subtract the same score for the other
        // player.
        // Alternative idea: do the same thing but set the score to the highest
        // of the row/column/diagonal values. This way the AI will be incentivized to
        // make the unbroken line as long as possible, with the highest utility 4, which
        // is winning the game. Also, opponent should be scored in the same way and his
        // values subtracted from our player's. OR, we could simply set the score to the
        // number of pieces on the most populated and unblocked line, and just alternate
        // the sign of that value for each player.

        // We have described two approaches above. One is an approach that wants to
        // maximize the number of pieces in an unblocked path. This has the drawback
        // that the AI will not be incentivized to "finish" a line instead of beginning
        // a new one.

        // On the other hand, we have a function that only cares about maximizing it's
        // longest unbroken line. This is good because the AI tries to win all the time,
        // but it's bad because sometimes a good strategy means putting pieces in places
        // where multiple lines are created so that the opponent can't block them all.

        // NEW IDEA: count number of n-in-lines by ecah player and value them like:
        // 1 in row = 1 point
        // 2 in row = 11 points
        // 3 in row = 111 points
        // 4 in row = 1111 points

        // do rows
        int pInRow;
        int oInRow;
        int rowScore;
        for(int i = 0; i < GameState.BOARD_SIZE; i++){
            pInRow = 0;
            oInRow = 0;
            for(int j = 0; j < GameState.BOARD_SIZE; j++){
                if(gameState.at(i, j) == player)
                    pInRow++;
                else if (gameState.at(i, j) == opponent)
                    oInRow++;

            }
            rowScore = gamma(pInRow, oInRow);
            // if someone has pieces in the row and is also the only one who does
            if(rowScore > 0){
                pInLine[rowScore - 1]++;
            }else if(rowScore < 0){
                oInLine[-rowScore - 1]++;
            }
        }

        // do columns
        int pInCol;
        int oInCol;
        int colScore;
        for(int j = 0; j < GameState.BOARD_SIZE; j++){
            pInCol = 0;
            oInCol = 0;
            for(int i = 0; i < GameState.BOARD_SIZE; i++){
                if(gameState.at(i, j) == player)
                    pInCol++;
                else if(gameState.at(i, j) == opponent)
                    oInCol++;
            }
            colScore = gamma(pInCol, oInCol);
            if(colScore > 0)
                pInLine[colScore - 1]++;
            else if(colScore < 0)
                oInLine[-colScore - 1]++;
        }

        // do diags
        int pPiecesD1 = 0;
        int oPiecesD1 = 0;
        int pPiecesD2 = 0;
        int oPiecesD2 = 0;
        for(int i = 0; i < GameState.BOARD_SIZE; i++){
            // first diagonal
            if(gameState.at(i, i) == player)
                pPiecesD1++;
            else if(gameState.at(i, i) == opponent)
                oPiecesD1++;
            // second diagonal
            if(gameState.at(i, GameState.BOARD_SIZE - 1 - i) == player) {
                pPiecesD2++;
            }else if(gameState.at(i, GameState.BOARD_SIZE - 1 - i) == opponent) {
                oPiecesD2++;
            }
        }
        int diag1Score = gamma(pPiecesD1, oPiecesD1);
        int diag2Score = gamma(pPiecesD2, oPiecesD2);

        if(diag1Score > 0)
            pInLine[diag1Score - 1]++;
        else if(diag1Score < 0)
            oInLine[-diag1Score - 1]++;

        if(diag2Score > 0)
            pInLine[diag2Score - 1]++;
        else if(diag2Score < 0)
            oInLine[-diag2Score - 1]++;

        // do something with pInLine and oInLine.

        // since there are 10 rows of victory, we'll make each 1-in-line worth
        // 1, each two in line worth 10 + 1 and each 3 in line worth 11 * 10 + 1
        // and each 4 in line worth (11 * 10 + 1) * 10 + 1.
        int pScore = 0;
        int oScore = 0;
        for(int i = 0; i < pInLine.length; i++){
            pScore += pInLine[i] * BASES[i];
            oScore += oInLine[i] * BASES[i];
        }
        // X is always max
        return (player == Constants.CELL_X) ? pScore - oScore : oScore - pScore;
    }

    /**
     * Returns a if b = 0, b if a = 0, else 0.
     * @param a
     * @param b
     */
    private int gamma(int a, int b) {
        int res = 0;
        if (b == 0) {
            res = a;
        } else if (a == 0) {
            res = -b;
        }
        return res;
    }
}

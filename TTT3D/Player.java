package TTT3D;

import java.util.*;

public class Player {
    private static final int MAX_DEPTH = 2;
    private static final int[] BASES = new int[]{1, (1) * 76 + 1, ((1) * 76 + 1) * 76 + 1, (((1) * 76 + 1) * 76 + 1) * 76 + 1};

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
     * @param player: the player who is deciding which state in nextStates is best.
     * @return
     */
    private GameState bestMove(Vector<GameState> nextStates, int player) {
        int maxScore = Integer.MIN_VALUE;
        int minScore = Integer.MAX_VALUE;
        int tempMax, tempMin;
        GameState bestState = null; // the state of the best move
        if(player == Constants.CELL_X) {// Player is max and wants to maximize the score
            for (GameState nextState : nextStates) {
                // TODO: do these become negative for X? fix it.
                tempMax = minimax(nextState, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, player); // TODO: try changing this to other player
                if (tempMax > maxScore) {
                    maxScore = tempMax;
                    bestState = nextState;
                }
            }
        }
        else if(player == Constants.CELL_O) { // Player is min and wants to minimize the score
            for(GameState nextState : nextStates) {
                tempMin = minimax(nextState, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, player);
                if (tempMin < minScore) {
                    minScore = tempMin;
                    bestState = nextState;
                }
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
            v = evaluate(gameState);
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

    //
    private int evaluate(GameState gameState) {
        int score = 0;
        int opponent;

        int[] pInLines = new int[GameState.BOARD_SIZE];
        int[] oInLines = new int[GameState.BOARD_SIZE];

        int pInLine;
        int oInLine;
        // for each layer
        for(int k = 0; k < GameState.BOARD_SIZE; k++){
            // for each row
            for(int i = 0; i < GameState.BOARD_SIZE; i++){
                pInLine = 0;
                oInLine = 0;
                for(int j = 0; j < GameState.BOARD_SIZE; j++){
                    if(gameState.at(i, j, k) == Constants.CELL_X)
                        pInLine++;
                    else if(gameState.at(i, j, k) == Constants.CELL_O)
                        oInLine++;
                }
                // if someone has n-in-line, add to pInLines or oInLines
                addInLines(gamma(pInLine, oInLine), pInLines, oInLines);
            }
            // for each column
            for(int j = 0; j < GameState.BOARD_SIZE; j++){
                pInLine = 0;
                oInLine = 0;
                for(int i = 0; i < GameState.BOARD_SIZE; i++){
                    if(gameState.at(i, j, k) == Constants.CELL_X)
                        pInLine++;
                    else if(gameState.at(i, j, k) == Constants.CELL_O)
                        oInLine++;
                }
                // if someone has n-in-line, add to pInLines or oInLines
                addInLines(gamma(pInLine, oInLine), pInLines, oInLines);
            }
        }

        // for all straight lines from top layer to bottom layer (iterating over layers)
        // for each row
        for(int i = 0; i < GameState.BOARD_SIZE; i++){
            // for each column
            for(int j = 0; j < GameState.BOARD_SIZE; j++){
                pInLine = 0;
                oInLine = 0;
                // for each layer
                for(int k = 0; k < GameState.BOARD_SIZE; k++){
                    if(gameState.at(i, j, k) == Constants.CELL_X)
                        pInLine++;
                    else if(gameState.at(i, j, k) == Constants.CELL_O)
                        oInLine++;
                }
                addInLines(gamma(pInLine, oInLine), pInLines, oInLines);
            }
        }

        // do diagonal rows
        int pDiag1;
        int oDiag1;
        int pDiag2;
        int oDiag2;
        // hold i fixed
        for(int i = 0; i < GameState.BOARD_SIZE; i++){
            pDiag1 = 0;
            oDiag1 = 0;
            pDiag2 = 0;
            oDiag2 = 0;
            for(int j = 0; j < GameState.BOARD_SIZE; j++){
                if(gameState.at(i, j, j) == Constants.CELL_X)
                    pDiag1++;
                else if(gameState.at(i, j, j) == Constants.CELL_O)
                    oDiag1++;
                if(gameState.at(i, j, GameState.BOARD_SIZE - j - 1) == Constants.CELL_X)
                    pDiag2++;
                else if(gameState.at(i, j, GameState.BOARD_SIZE - j - 1) == Constants.CELL_O)
                    oDiag2++;
            }
            addInLines(gamma(pDiag1, oDiag1), pInLines, oInLines);
            addInLines(gamma(pDiag2, oDiag2), pInLines, oInLines);
        }

        // do main diagonals
        int[] pInLineMainDiags = new int[GameState.BOARD_SIZE]; // {diag1, diag2, diag3, diag4}
        int[] oInLineMainDiags = new int[GameState.BOARD_SIZE]; // {diag1, diag2, diag3, diag4}
        for(int i = 0; i < GameState.BOARD_SIZE; i++){
            if(gameState.at(i, i, i) == Constants.CELL_X)
                pInLineMainDiags[0]++;
            else if(gameState.at(i, i, i) == Constants.CELL_O)
                oInLineMainDiags[0]++;

            if(gameState.at(4 - i - 1, i, i) == Constants.CELL_X)
                pInLineMainDiags[1]++;
            else if(gameState.at(4 - i - 1, i, i) == Constants.CELL_O)
                oInLineMainDiags[1]++;

            if(gameState.at(i, 4 - i - 1, i) == Constants.CELL_X)
                pInLineMainDiags[2]++;
            else if(gameState.at(i, 4 - i - 1, i) == Constants.CELL_O)
                oInLineMainDiags[2]++;

            if(gameState.at(4 - i - 1, 4 - i - 1, i) == Constants.CELL_X)
                pInLineMainDiags[3]++;
            else if(gameState.at(4 - i - 1, 4 - i - 1, i) == Constants.CELL_O)
                oInLineMainDiags[3]++;
        }
        for(int i = 0; i < GameState.BOARD_SIZE; i++){
            addInLines(gamma(pInLineMainDiags[i], oInLineMainDiags[i]), pInLines, oInLines);
        }


        // do something with pInLine and oInLine.

        // since there are 76 rows of victory, we'll make each 1-in-line worth
        // 1, each two in line worth 76 + 1 and each 3 in line worth (76 + 1) * 76 + 1
        // and each 4 in line worth ((76 + 1) * 76 + 1) * 76 + 1.
        int pScore = 0;
        int oScore = 0;
        for(int i = 0; i < pInLines.length; i++){
            pScore += pInLines[i] * BASES[i];
            oScore += oInLines[i] * BASES[i];
        }
        // X is always max
        return pScore - oScore;
    }

    /**
     * If row Score is not zero, increment the element of that score in the appropriate list.
     * @param rowScore
     * @param pInLines
     * @param oInLines
     */
    private void addInLines(int rowScore, int[] pInLines, int[] oInLines) {
        if(rowScore > 0)
            pInLines[rowScore - 1]++;
        else if(rowScore < 0)
            oInLines[-rowScore - 1]++;
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

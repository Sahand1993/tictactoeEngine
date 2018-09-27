package TTT3D;

import java.util.*;

public class Player {
    public static final int[][] LINES = {
            {0, 1, 2, 3}, // row 1
            {4, 5, 6, 7}, // row 2
            {8, 9, 10, 11}, // row 3
            {12, 13, 14, 15}, // row 4
            {16, 17, 18, 19}, // row 5
            {20, 21, 22, 23}, // row 6
            {24, 25, 26, 27}, // row 7
            {28, 29, 30, 31}, // row 8
            {32, 33, 34, 35}, // row 9
            {36, 37, 38, 39}, // row 10
            {40, 41, 42, 43}, // row 11
            {44, 45, 46, 47}, // row 12
            {48, 49, 50, 51}, // row 13
            {52, 53, 54, 55}, // row 14
            {56, 57, 58, 59}, // row 15
            {60, 61, 62, 63}, // row 16
            {0, 4, 8, 12}, // col 1
            {1, 5, 9, 13}, // col 2
            {2, 6, 10, 14}, // col 3
            {3, 7, 11, 15}, // col 4
            {16, 20, 24, 28}, // col 5
            {17, 21, 25, 29}, // col 6
            {18, 22, 26, 30}, // col 7
            {19, 23, 27, 31}, // col 8
            {32, 36, 40, 44}, // col 9
            {33, 37, 41, 45}, // col 10
            {34, 38, 42, 46}, // col 11
            {35, 39, 43, 47}, // col 12
            {48, 52, 56, 60}, // col 13
            {49, 53, 57, 61}, // col 14
            {50, 54, 58, 62}, // col 15
            {51, 55, 59, 63}, // col 16
            // vertical rows (through the screen)
            {0, 16, 32, 48}, // vertical 1
            {1, 17, 33, 49}, // vertical 2
            {2, 18, 34, 50}, // vertical 3
            {3, 19, 35, 51}, // vertical 4
            {4, 20, 36, 52}, // vertical 5
            {5, 21, 37, 53}, // vertical 6
            {6, 22, 38, 54}, // vertical 7
            {7, 23, 39, 55}, // vertical 8
            {8, 24, 40, 56}, // vertical 9
            {9, 25, 41, 57}, // vertical 10
            {10, 26, 42, 58}, // vertical 11
            {11, 27, 43, 59}, // vertical 12
            {12, 28, 44, 60}, // vertical 13
            {13, 29, 45, 61}, // vertical 14
            {14, 30, 46, 62}, // vertical 15
            {15, 31, 47, 63}, // vertical 16
            // layer diags
            {0, 5, 10, 15}, // layer 0
            {3, 6, 9, 12},
            {16, 21, 26, 31}, // layer 1
            {19, 22, 25, 28},
            {32, 37, 42, 47}, // layer 2
            {35, 38, 41, 44},
            {48, 53, 58, 63}, // layer 3
            {51, 54, 57, 60},
            // layer diags that are cross-layer for us
            {0, 20, 40, 60}, // from top down
            {1, 21, 41, 61},
            {2, 22, 42, 62},
            {3, 23, 43, 63},
            {12, 24, 36, 48}, // from bottom up
            {13, 25, 37, 49},
            {14, 26, 38, 50},
            {15, 27, 39, 51},
            {0, 17, 34, 51}, // left to right
            {4, 21, 38, 55},
            {8, 25, 42, 59},
            {12, 29, 46, 63},
            {3, 18, 33, 48}, // right to left
            {7, 22, 37, 52},
            {11, 26, 41, 56},
            {15, 30, 45, 60},
            // main diags
            {0, 21, 42, 63},
            {3, 22, 41, 60},
            {12, 25, 38, 51},
            {15, 26, 37, 48},
    };
    private int depth;
    private int currDepth;
    //private static int pow = 6;
    //private static final int[] BASES_SPECIAL = new int[]{(int)Math.pow(1, pow), (int)Math.pow(2, pow), (int)Math.pow(3, pow), (int)Math.pow(4, pow)};
    private static final int[] BASES = new int[]{1, (1) * 76 + 1, ((1) * 76 + 1) * 76 + 1, Integer.MAX_VALUE};
    private static final int[] OPP_EXTRA = new int[]{1, 1, (((1) * 76 + 1) * 76 + 1) * 76, 1}; // TODO: check what the optimal values are here

    /**
     * here you can get scores[n][x] where n is the n in n-in-line and x is the number of n-in-lines
     */
    //private static final int[][] scores = new int[][]{{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, {77, 77 * 2, 77 * 3, 77 * 4, 77 * 5, 77 * 6, 77 * 7, 77 * 8, 77 * 9, 77 * 10}, {(77 * 10 + 1), (77 * 10 + 1) * 2, (77 * 10 + 1) * 3, (77 * 10 + 1) * 4, (77 * 10 + 1) * 5, (77 * 10 + 1) * 6, (77 * 10 + 1) * 7, (77 * 10 + 1) * 8, (77 * 10 + 1) * 9, (77 * 10 + 1) * 10}, {(77 * 10 + 1) * 10 + 1, ((77 * 10 + 1) * 10 + 1) * 2, ((77 * 10 + 1) * 10 + 1) * 3, ((77 * 10 + 1) * 10 + 1) * 4, ((77 * 10 + 1) * 10 + 1) * 5, ((77 * 10 + 1) * 10 + 1) * 6, ((77 * 10 + 1) * 10 + 1) * 7, ((77 * 10 + 1) * 10 + 1) * 8, ((77 * 10 + 1) * 10 + 1) * 9, ((77 * 10 + 1) * 10 + 1) * 10}};
    private static GameState bestState;
    public int totalTime = 0;
    public int sortingTime = 0;

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
        long start = System.nanoTime();
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        if (nextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(gameState, new Move());
        }
        System.err.println(nextStates.size());
        /**
         * Here you should write your algorithms to get the best next move, i.e.
         * the best next state. This skeleton returns a random move instead.
         */

        depth = 2;
        minimax(gameState, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, gameState.getNextPlayer());
        long end = System.nanoTime();
        System.err.printf("move time: %f\n", (double) (end - start) / 1000000);
        totalTime += end - start;
        return bestState;
    }

    /**
     * basic minimax with pruning
     * @param gameState
     * @param depth
     * @param alpha
     * @param beta
     * @param player
     */
    private int oldminimax(GameState gameState, int depth, int alpha, int beta, int player) {
        int v; // Our evaluation result
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);
        System.err.printf("number of possible moves: %d\n", nextStates.size());

        if(depth == 0 || nextStates.size() == 0) {
            v = evaluate(gameState);
        }

        else if(player == Constants.CELL_X){
            v = Integer.MIN_VALUE;
            for(GameState nextState : nextStates){
                v = Math.max(v, minimax(nextState, depth - 1, alpha, beta, Constants.CELL_O));
                if(v > alpha){
                    alpha = v;
                    if(depth == currDepth){
                        bestState = nextState;
                    }

                }
                if(beta <= alpha){
                    break;
                }
            }
        }
        else{
            v = Integer.MAX_VALUE;
            for(GameState nextState : nextStates){
                v = Math.min(v, minimax(nextState, depth - 1, alpha, beta, Constants.CELL_X));
                if(v < beta){
                    beta = v;
                    if(depth == currDepth){
                        bestState = nextState;
                    }
                }
                if(beta <= alpha) {
                    break;
                }
            }
        }
        return v;
    }

    /**
     * Calls the minimax algorithm on each possible next move and returns the best one.
     * @param gameState
     * @param player: the player who is deciding which state in nextStates is best.
     * @return
     */
/*
    private GameState bestMove(GameState gameState, int player) {
        int maxScore;
        int minScore;
        GameState bestState = null; // the state of the best move
        if(player == Constants.CELL_X) {// Player is max and wants to maximize the score
            maxScore = minimax(gameState, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, player);
        }
        else if(player == Constants.CELL_O) { // Player is min and wants to minimize the score
            minScore = minimax(gameState, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, player);
        }
        return bestState;
    }
*/

    /**
     * The minimax algorithm with alpha-beta pruning.
     * @param gameState: the state we want to evaluate.
     * @param player: the current player
     * @return [the score of best gameState, Move]
     */
    private int minimax(GameState gameState, int depth, int alpha, int beta, int player) {
        int v; // Our evaluation result
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        if(depth == 0 || nextStates.size() == 0) {
            v = evaluateLines(gameState);
        }

        else if(player == Constants.CELL_X){
            // if it's the beginning of the game, only 1 depth is necessary
            if(depth == currDepth) {
                long start = System.nanoTime();
                // Order nextStates according to evaluate(nextState)
                IndexNScore[] indexNScoreArr = new IndexNScore[nextStates.size()];
                for (int i = 0; i < nextStates.size(); i++) {
                    indexNScoreArr[i] = new IndexNScore(i, evaluate(nextStates.get(i)));
                }
                Arrays.sort(indexNScoreArr);
                long end = System.nanoTime();
                sortingTime += end - start;
                v = Integer.MIN_VALUE;
                int i;
                GameState nextState;
                // perform minimax on sorted nextStates
                for (IndexNScore indexScore : indexNScoreArr) {
                    i = indexScore.getIndex();
                    nextState = nextStates.get(i);
                    v = Math.max(v, minimax(nextState, depth - 1, alpha, beta, Constants.CELL_O));
                    if (v > alpha) {
                        alpha = v;
                        if(this.depth == depth) {
                            bestState = nextState;
                        }
                    }
                    if (beta <= alpha) {
                        break;
                    }
                }
            }else{
                v = Integer.MIN_VALUE;
                for(GameState nextState : nextStates){
                    v = Math.max(v, minimax(nextState, depth - 1, alpha, beta, Constants.CELL_O));
                    if(v > alpha){
                        alpha = v;
                        if(this.depth == depth){
                            bestState = nextState;
                        }
                    }
                    if(beta <= alpha){
                        break;
                    }
                }
            }
        }
        else{
            v = Integer.MAX_VALUE;
            for(GameState nextState : nextStates){
                v = Math.min(v, minimax(nextState, depth - 1, alpha, beta, Constants.CELL_X));
                if(v < beta){
                    beta = v;
                    if(this.depth == depth){
                        bestState = nextState;
                    }
                }
                if(beta <= alpha) {
                    break;
                }
            }
        }
        return v;
    }

    public int evaluateLines(GameState gameState){
        int[] pInLines = new int[GameState.BOARD_SIZE];
        int[] oInLines = new int[GameState.BOARD_SIZE];
        int pInLine, oInLine;

        // for each line
        for(int[] line : LINES){
            pInLine = 0;
            oInLine = 0;
            for(int i : line){
                if(gameState.at(i) == Constants.CELL_X)
                    pInLine++;
                else if(gameState.at(i) == Constants.CELL_O)
                    oInLine++;
            }
            addInLines(gamma(pInLine, oInLine), pInLines, oInLines);
        }

        int pScore = 0;
        int oScore = 0;
        for(int i = 0; i < pInLines.length; i++){
            pScore += pInLines[i] * BASES[i];
            oScore += oInLines[i] * BASES[i] + oInLines[i] * OPP_EXTRA[i];
        }
        // X is always max
        return pScore - oScore;
    }

    //
    public int evaluate(GameState gameState) {
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
            oScore += oInLines[i] * BASES[i] + oInLines[i] * OPP_EXTRA[i];
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
        else if(rowScore < 0) {
            oInLines[-rowScore - 1]++;
        }
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

    private class IndexNScore implements Comparable<IndexNScore> {
        int index;
        int score;

        public IndexNScore(int index, int score){
            this.index = index;
            this.score = score;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public int compareTo(IndexNScore o) {
            return this.score - o.score;
        }
    }
}

package TTT3D;

import java.util.*;

public class Player {
    private int depth;
    private int currDepth;
    private static final int[] BASES = new int[]{1, (1) * 76 + 1, ((1) * 76 + 1) * 76 + 1, Integer.MAX_VALUE};
    private static final int[] OPP_EXTRA = new int[]{0, 0, (((1) * 76 + 1) * 76 + 1) * 76, 0};

    /**
     * here you can get scores[n][x] where n is the n in n-in-line and x is the number of n-in-lines
     */
    //private static final int[][] scores = new int[][]{{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, {77, 77 * 2, 77 * 3, 77 * 4, 77 * 5, 77 * 6, 77 * 7, 77 * 8, 77 * 9, 77 * 10}, {(77 * 10 + 1), (77 * 10 + 1) * 2, (77 * 10 + 1) * 3, (77 * 10 + 1) * 4, (77 * 10 + 1) * 5, (77 * 10 + 1) * 6, (77 * 10 + 1) * 7, (77 * 10 + 1) * 8, (77 * 10 + 1) * 9, (77 * 10 + 1) * 10}, {(77 * 10 + 1) * 10 + 1, ((77 * 10 + 1) * 10 + 1) * 2, ((77 * 10 + 1) * 10 + 1) * 3, ((77 * 10 + 1) * 10 + 1) * 4, ((77 * 10 + 1) * 10 + 1) * 5, ((77 * 10 + 1) * 10 + 1) * 6, ((77 * 10 + 1) * 10 + 1) * 7, ((77 * 10 + 1) * 10 + 1) * 8, ((77 * 10 + 1) * 10 + 1) * 9, ((77 * 10 + 1) * 10 + 1) * 10}};
    private static GameState bestState;
    public int totalTime = 0;
    public int sortingTime = 0;

    public static void main(String[] args){

    }

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

        depth = 4;
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
            v = evaluate(gameState);
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

    //
    public int evaluate(GameState gameState) {
        int score = 0;
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

package TTT;
import java.util.*;

public class Player {
    private static int depth = 1;
    private static GameState bestState;
    public int totalTime = 0;
    private static final int[] BASES = new int[]{1, 11, 111, Integer.MAX_VALUE};
    private static final int[] OPP_EXTRA = new int[]{1, 1, (11+1)*(111+1), 1}; // TODO: check what the optimal values are here
    private static final int[][] LINES = {
            {0,1,2,3},      // Row 1
            {4,5,6,7},      // Row 2
            {8,9,10,11},    // Row 3
            {12,13,14,15},  // Row 4

            {0, 4, 8, 12},  // col 1
            {1, 5, 9, 13},  // col 2
            {2, 6, 10, 14}, // col 3
            {3, 7, 11, 15}, // col 4

            {0, 5, 10, 15}, // Diag 1
            {3, 6, 9, 12}   // Diag 2
    };

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
            tempMax = minimax(nextState, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, player);
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
            v = evaluateLines(gameState);
        }
        else if(player == Constants.CELL_X){
            // if it's the beginning of the game, only 1 depth is necessary
            if(this.depth == depth) {
                // Order nextStates according to evaluate(nextState)
                IndexNScore[] indexNScoreArr = new IndexNScore[nextStates.size()];
                for (int i = 0; i < nextStates.size(); i++) {
                    indexNScoreArr[i] = new IndexNScore(i, evaluate(nextStates.get(i)));
                }
                Arrays.sort(indexNScoreArr);
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

    // TODO:    I think the problem when increasing the depth to 1 is that this function
    // TODO:    doesn't care about the sign, it just does minimax from the perspective
    // TODO:    of the player argument. We should instead make CELL_X always positive, and O
    // TODO:    always negative.
    private int evaluate(GameState gameState) {
        int[] pInLines = new int[GameState.BOARD_SIZE];
        int[] oInLines = new int[GameState.BOARD_SIZE];

        int pInLine;
        int oInLine;

        for(int i = 0; i < GameState.BOARD_SIZE; i++){
            pInLine = 0;
            oInLine = 0;
            for(int j = 0; j < GameState.BOARD_SIZE; j++){
                if(gameState.at(i, j) == Constants.CELL_X)
                    pInLine++;
                else if(gameState.at(i, j) == Constants.CELL_O)
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
                if(gameState.at(i, j) == Constants.CELL_X)
                    pInLine++;
                else if(gameState.at(i, j) == Constants.CELL_O)
                    oInLine++;
            }
            // if someone has n-in-line, add to pInLines or oInLines
            addInLines(gamma(pInLine, oInLine), pInLines, oInLines);
        }


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



        // do something with pInLine and oInLine.

        // since there are 10 rows of victory, we'll make each 1-in-line worth
        // 1, each two in line worth 10 + 1 and each 3 in line worth 11 * 10 + 1
        // and each 4 in line worth (11 * 10 + 1) * 10 + 1.
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

    private void addInLines(int rowScore, int[] pInLines, int[] oInLines) {
        if(rowScore > 0)
            pInLines[rowScore - 1]++;
        else if(rowScore < 0) {
            oInLines[-rowScore - 1]++;
        }
    }
}

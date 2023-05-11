package ataxx;

import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

// Final Project Part A.2 Ataxx AI Player (A group project)

/** A Player that computes its own moves. */
class AIPlayer extends Player {

    
    /** A new AIPlayer for GAME that will play MYCOLOR.
     *  SEED is used to initialize a random-number generator,
	 *  increase the value of SEED would make the AIPlayer move automatically.
     *  Identical seeds produce identical behaviour. */
    AIPlayer(Game game, PieceState myColor, long seed) {
        super(game, myColor);
    }
    private Move maxMove = null;

    @Override
    boolean isAuto() {
        return true;
    }

    @Override
    String getAtaxxMove() {
        Move move = findMove();
        getAtaxxGame().reportMove(move, getMyState());
        return move.toString();
    }


    private Move _lastFoundMove;
    private int staticScore(Board board, int winningValue) {
        PieceState winner = board.getWinner();
        if (winner != null) {
            return switch (winner) {
                case RED -> winningValue;
                case BLUE -> -winningValue;
                default -> 0;
            };
        }
        if (board.nextMove() == PieceState.RED) {
            return board.getColorNums(PieceState.RED) - board.getColorNums(PieceState.BLUE);
        } else {
            return board.getColorNums(PieceState.BLUE) - board.getColorNums(PieceState.RED);
        }
    }

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _foundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _foundMove. If the game is over
     *  on BOARD, does not set _foundMove. */
    private int minMax(Board board, int depth, boolean saveMove, int sense,
                       int alpha, int beta) {
        /* We use WINNING_VALUE + depth as the winning value so as to favor
         * wins that happen sooner rather than later (depth is larger the
         * fewer moves have been made. */
        if (depth == 0 || board.getWinner() != null) {
            return staticScore(board,10000);
        }
        Move best;
        best = null;
        int bestScore = 0;
        ArrayList<Move> allPossibleMoves = new ArrayList<>();
        if (sense == 1) {
            if (board.moveLegal(Move.pass())) {
                allPossibleMoves.add(Move.pass());
            } else {
                bestScore = Integer.MIN_VALUE;
                allPossibleMoves = possibleMoves(board, board.nextMove());
                for (Move possible : allPossibleMoves) {
                    Board copy = new Board(board);
                    copy.createMove(possible);
                    int response = minMax(copy, depth - 1, false,
                            -1, alpha, beta);
                    if (response > bestScore) {
                        best = possible;
                        bestScore = response;
                        alpha = max(alpha, bestScore);
                        if (alpha >= beta) {
                            break;
                        }
                    }
                }
            }
        } else if (sense == -1) {
            if (board.moveLegal(Move.pass())) {
                allPossibleMoves.add(Move.pass());
            } else {
                bestScore = Integer.MAX_VALUE;
                allPossibleMoves = possibleMoves(board, board.nextMove());
                for (Move possible : allPossibleMoves) {
                    Board copy = new Board(board);
                    copy.createMove(possible);
                    int response = minMax(copy, depth - 1, false,
                            1, alpha, beta);
                    if (response < bestScore) {
                        bestScore = response;
                        best = possible;
                        beta = min(beta, bestScore);
                        if (alpha >= beta) {
                            break;
                        }
                    }
                }
            }
        }
        if (saveMove) {
            _lastFoundMove = best;
        }
        return bestScore;
    }
    private Move findMoveHelper(Board simulationBoard,int depth){

        if (!simulationBoard.couldMove(getMyState().opposite())&&simulationBoard.getColorNums(getMyState())+(49-simulationBoard.getColorNums(getMyState())-simulationBoard.getColorNums(getMyState().opposite()))>=simulationBoard.getColorNums(getMyState().opposite())){
            List<Move> moveList = possibleMoves(simulationBoard,getMyState());
            for(Move move:moveList){
                if (move.isClone()){return move;}
            }
        }

        Object[] objects = evaluateMyMove(simulationBoard,depth,0,Integer.MIN_VALUE,Integer.MAX_VALUE,true);
        return (Move) objects[0];
//        List<Move> goodMoves = (List<Move>) objects[0];
//        return goodMoves.get(new Random().nextInt(goodMoves.size()));
//        moves.sort(new Comparator<Move>() {
//            @Override
//            public int compare(Move o1, Move o2) {
//                if ((o1.isClone()&&o2.isClone())||(o1.isJump()&&o2.isJump())){return 0;}
//                if (o1.isJump()){
//                    return 1;
//                }
//                if (o2.isJump()){
//                    return -1;
//                }
//                return 0;
//            }
//        });
//        return moves.get(0);
    }



    private Object[] evaluateMyMove(Board board,int depth,int turn,int ar,int bt,boolean next){
        if (board.getWinner()!=null||depth<=0){
            if (board.getWinner()!=null&&board.getWinner()==getMyState()){
                return new Object[]{null,100000,true};
            }
            if (board.getWinner()!=null&&board.getWinner()!=getMyState()){
                return new Object[]{null,-100000,false};
            }
            if (board.getWinner()!=null&&board.getWinner()!=PieceState.RED&&board.getWinner()!=PieceState.BLUE){
                return new Object[]{null,0,false};
            }
            return new Object[]{null,board.getColorNums(getMyState())-board.getColorNums(getMyState()==PieceState.RED?PieceState.BLUE:PieceState.RED),false};
        }
        List<Move> possibleMoveList = possibleMoves(board,board.nextMove());
        List<Move> nextMoves = null;
        if (next){
            nextMoves = new ArrayList<>();
        }
        int best = turn==0?Integer.MIN_VALUE:Integer.MAX_VALUE;
        Move nextMove = null;
        boolean flag = false;
        for(Move move1:possibleMoveList){
            Board t = new Board(board);
            flag = true;
            if (turn == 0){
                t.createMove(move1);
                if (!t.couldMove(t.nextMove())&&t.getColorNums(getMyState())+(49-t.getColorNums(getMyState())-t.getColorNums(getMyState().opposite()))>=t.getColorNums(getMyState().opposite())){
                    return new Object[]{move1,100000};
                }
                int tMax = (Integer) evaluateMyMove(t,depth-1,1,ar,bt,false)[1];
                if (tMax>best){
                    if (next){
                        nextMoves.clear();
                        nextMoves.add(move1);
                    }
                    best = tMax;
                    ar = Math.max(ar,best);
                    nextMove = move1;
                }
                else if (tMax == best&&next){
                    nextMoves.add(move1);
                }
                if (ar>=bt){
                    return new Object[]{next?nextMove:nextMove,best};
                }
            }
            else {
                t.createMove(move1);
                if (!t.couldMove(t.nextMove())&&t.getColorNums(getMyState())+(49-t.getColorNums(getMyState())-t.getColorNums(getMyState().opposite()))<t.getColorNums(getMyState().opposite())){
                    return new Object[]{move1,-100000};
                }
                int tMin = (Integer) evaluateMyMove(t,depth-1,0,ar,bt,false)[1];
                if (tMin<best){
                    best = tMin;
                    bt = Math.min(bt,best);
                }
                if (ar>=bt){
                    return new Object[]{nextMove,best};
                }
            }
        }
        return new Object[]{nextMove,flag?best:0};
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(getAtaxxBoard());
        lastFoundMove = null;

        // Here we just have the simple AI to randomly move.
        // However, it does not meet with the requirements of Part A.2.
        // Therefore, the following codes should be modified
        // in order to meet with the requirements of Part A.2.
        // You can create add your own method and put your method here.
//        ArrayList<Move> listOfMoves =
//                possibleMoves(b, b.nextMove());
//        int moveArrayLength = listOfMoves.size();
//        int randomIndex = (int) (Math.random() * moveArrayLength);
//        for(int i = 0; i < moveArrayLength; i++){
//            if (i == randomIndex){
//                b.createMove(listOfMoves.get(i));
//                lastFoundMove = listOfMoves.get(i);
//            }
//        }

        if (getMyState()==PieceState.BLUE&&false){
            minMax(b,4,true,-1,Integer.MIN_VALUE,Integer.MAX_VALUE);
            lastFoundMove = _lastFoundMove;
        }
        else {
//            minMax(b,4,true,-1,Integer.MIN_VALUE,Integer.MAX_VALUE);
            lastFoundMove = findMoveHelper(b,4);
        }


        // Please do not change the codes below
        if (lastFoundMove == null) {
            lastFoundMove = Move.pass();
        }
        return lastFoundMove;
    }


    /** The move found by the last call to the findMove method above. */
    private Move lastFoundMove;


    /** Return all possible moves for a color.
     * @param board the current board.
     * @param myColor the specified color.
     * @return an ArrayList of all possible moves for the specified color. */
    private ArrayList<Move> possibleMoves(Board board, PieceState myColor) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        for (char row = '7'; row >= '1'; row--) {
            for (char col = 'a'; col <= 'g'; col++) {
                int index = Board.index(col, row);
                if (board.getContent(index) == myColor) {
                    ArrayList<Move> addMoves
                            = assistPossibleMoves(board, row, col);
                    possibleMoves.addAll(addMoves);
                }
            }
        }
        return possibleMoves;
    }

    /** Returns an Arraylist of legal moves.
     * @param board the board for testing
     * @param row the row coordinate of the center
     * @param col the col coordinate of the center */
    private ArrayList<Move>
        assistPossibleMoves(Board board, char row, char col) {
        ArrayList<Move> assistPossibleMoves = new ArrayList<>();
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                if (i != 0 || j != 0) {
                    char row2 = (char) (row + j);
                    char col2 = (char) (col + i);
                    Move currMove = Move.move(col, row, col2, row2);
                    if (board.moveLegal(currMove)) {
                        assistPossibleMoves.add(currMove);
                    }
                }
            }
        }
        return assistPossibleMoves;
    }
}

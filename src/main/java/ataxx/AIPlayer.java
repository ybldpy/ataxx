package ataxx;

import java.util.*;

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



    private Move findMoveHelper(Board simulationBoard,int depth){
        int max = -1;
        Move maxMove = null;
        List<Move> moveList = possibleMoves(simulationBoard,getMyState());
        for(Move move:moveList){
            int tMax = evaluateMove(new Board(simulationBoard),depth,move,0);
            if (tMax>max){
                max = tMax;
                maxMove = move;
            }
        }
        return maxMove;

    }



    private int evaluateMove(Board board,int depth,Move move,int turn){
        if (depth<=0){return board.getColorNums(board.nextMove());}
        int max = board.getColorNums(getMyState());
        board.createMove(move);
        List<Move> moves = possibleMoves(board,board.nextMove());
        for(Move move1:moves){
            Board t = new Board(board);
            if (turn%2==0){
                max = Math.max(max,evaluateMove(t,depth-1,move1,turn+1));
            }
            else {
                max = Math.min(max,evaluateMove(t,depth-1,move1,turn+1));
            }
        }
        return max;
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

        Move maxMove = findMoveHelper(getAtaxxBoard(),3);
        lastFoundMove = maxMove;
        getAtaxxBoard().createMove(maxMove);


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

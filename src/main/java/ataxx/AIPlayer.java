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
        int max = simulationBoard.getColorNums(getMyState());
        Move maxMove = null;
        List<Move> moves = new ArrayList<>();
        List<Move> moveList = possibleMoves(simulationBoard,getMyState());
        Map<Move,Integer> minMove = new HashMap<>();
        for(Move move:moveList){
            minMove.put(move,simulationBoard.getColorNums(getMyState()==PieceState.RED?PieceState.BLUE:PieceState.RED));
            int tMax = evaluateMyMove(new Board(simulationBoard),depth,move,0,move,minMove);
            if (tMax>max){
                max = tMax;
                moves.clear();
                moves.add(move);
            }
            else if (tMax==max){
                moves.add(move);
            }
        }

        int min = Integer.MIN_VALUE;
        List<Move> finalMovesList = new ArrayList<>();
        for(Move move:moves){
            if (minMove.get(move)>min){
                min = minMove.get(move);
                finalMovesList.clear();
                finalMovesList.add(move);
            }
            else if (minMove.get(move)==min){
                finalMovesList.add(move);
            }
        }

        return finalMovesList.get(new Random().nextInt(finalMovesList.size()));
//        int min = simulationBoard.getColorNums(getMyState()==PieceState.RED?PieceState.BLUE:PieceState.RED);
//        Move finalMove = moves.get(new Random().nextInt(moves.size()));
//        for(Move move:moves){
//            int tMin = evaluateEnemyMove(new Board(simulationBoard),depth,move,0);
//            if (tMin<min){
//                min = tMin;
//                finalMove = move;
//            }
//        }





    }

//    private int evaluateEnemyMove(Board board,int depth,Move move,int turn){
//        if (depth<=0){return board.getColorNums(getMyState()==PieceState.RED?PieceState.BLUE:PieceState.RED);}
//        int min = board.getColorNums(getMyState()==PieceState.RED?PieceState.BLUE:PieceState.RED);
//        board.createMove(move);
//        List<Move> moves = possibleMoves(board,board.nextMove());
//        for(Move move1:moves){
//            Board t = new Board(board);
//            if (turn%2==0){
//                min = Math.min(min,evaluateMyMove(t,depth,move1,turn+1));
//            }
//            else {
//                min = Math.min(min,evaluateMyMove(t,depth-1,move1,turn+1));
//            }
//        }
//        return min;
//    }



    private int evaluateMyMove(Board board,int depth,Move move,int turn,Move initMove,Map<Move,Integer> minMove){
        if (depth<=0){return board.getColorNums(getMyState());}
        int max = board.getColorNums(getMyState());
        board.createMove(move);
        minMove.put(initMove,Math.min(minMove.get(initMove),Math.min(minMove.get(initMove),board.getColorNums(getMyState())-board.getColorNums(getMyState()==PieceState.RED?PieceState.BLUE:PieceState.RED))));
        List<Move> moves = possibleMoves(board,board.nextMove());
        for(Move move1:moves){
            Board t = new Board(board);
            if (turn%2==0){
                max = Math.max(max,evaluateMyMove(t,depth-1,move1,turn+1,initMove,minMove));
            }
            else {
                max = Math.min(max,evaluateMyMove(t,depth,move1,turn+1,initMove,minMove));
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

        Move maxMove = findMoveHelper(getAtaxxBoard(),2);
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

package ataxx;

// Optional Task: The GUI for the Ataxx Game

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayDeque;
import java.util.Queue;



class GUI extends JPanel implements View, CommandSource, Reporter,MouseListener{


    private volatile Board board;
    private JFrame jFrame;
    private static final int WIDTH =  800;
    private static final int HEIGHT = 700;
    private static final Object MONITOR = new Object();

    private static final int CELL_SIZE = 65;
    private static final int ROW = 7;
    private static final int COL = 7;
    private static final int ITEM_SIZE = 30;
    private static final  int MIDDLE = (CELL_SIZE-ITEM_SIZE)/2;
    private int[] fromLocation;
    private volatile String command;
    private static final int BOARD_OFFSET_HEIGHT = (HEIGHT-100-ROW*CELL_SIZE)/2;
    private static final int BOARD_OFFSET_WIDTH = 80;
    private static final int MAX = 10;
    private Object lock = new Object();
    private Font red = new Font("red",Font.BOLD,20);
    private Font blue = new Font("blue",Font.BOLD,20);
    private String error;
    private String message;
    private PieceState winner;

    // Complete the codes here
    GUI(String ataxx) {
        jFrame = new JFrame();
        jFrame.setTitle(ataxx);
        jFrame.setSize(WIDTH,HEIGHT);
        jFrame.setLayout(new BorderLayout());
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.addMouseListener(this);
        fromLocation = new int[]{-1, -1};
        jFrame.add(this,BorderLayout.CENTER);
        this.setLayout(null);
        JButton passBtn = new JButton("Pass");
        passBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                command = "-";
                fromLocation[0] = -1;
                synchronized (MONITOR){
                    MONITOR.notifyAll();
                }
            }
        });
        jFrame.add(passBtn,BorderLayout.SOUTH);
        JMenuBar jMenuBar = new JMenuBar();
        JMenu jMenu = new JMenu("Menu");
        jMenuBar.add(jMenu);
        JMenuItem jMenuItem = new JMenuItem("new game");
        jMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                command = "new";
                fromLocation[0] = -1;
                message = null;
                synchronized (MONITOR){
                    MONITOR.notifyAll();
                }
            }
        });
        jMenu.add(jMenuItem);
        jFrame.setJMenuBar(jMenuBar);
        jFrame.setVisible(true);
    }

    // Add some codes here

    // These methods could be modified
	
    @Override
    public void update(Board board) {
        this.board = board;
        fromLocation[0] = -1;
        synchronized (lock){
            try {
                jFrame.repaint();
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        synchronized (lock){
            if (board==null){lock.notifyAll();return;}
            super.paintComponent(g);
            Color color = g.getColor();
            Font originalFont = g.getFont();
            int middleX = (ROW*CELL_SIZE)/2+BOARD_OFFSET_WIDTH;
            int middleY = BOARD_OFFSET_HEIGHT-35;
            g.setColor(Color.RED);
            g.setFont(red);
            g.drawString(String.format("%d",board.getColorNums(PieceState.RED)),middleX-35,middleY);
            Font small = new Font("s",Font.BOLD,12);
            g.setFont(small);
            g.drawString("click right mouse button to set block",BOARD_OFFSET_WIDTH+ROW*CELL_SIZE+15,BOARD_OFFSET_HEIGHT);
            g.setFont(red);
            g.setColor(Color.BLUE);
            g.drawString(String.format("%d",board.getColorNums(PieceState.BLUE)),middleX+35,middleY);
            g.setColor(color);
            if (message!=null){
                g.drawString(message,BOARD_OFFSET_WIDTH+(COL*CELL_SIZE)+15,BOARD_OFFSET_HEIGHT+(CELL_SIZE*ROW)/2);
            }
            g.setFont(originalFont);
            g.setColor(board.nextMove()==PieceState.RED?Color.RED:Color.BLUE);
            g.fillOval(BOARD_OFFSET_WIDTH+MIDDLE,BOARD_OFFSET_HEIGHT-CELL_SIZE+MIDDLE,ITEM_SIZE,ITEM_SIZE);
            g.setColor(color);
            for(int i=0;i<ROW;i++){
                for(int u=0;u<COL;u++){
                    PieceState pieceState = board.getContent((char) (u+'a'),(char) ((ROW-i-1)+'1'));
                    if (pieceState==PieceState.BLOCKED){
                        g.setColor(Color.ORANGE);
                        g.fillRect(BOARD_OFFSET_WIDTH+u*CELL_SIZE,BOARD_OFFSET_HEIGHT+i*CELL_SIZE,CELL_SIZE,CELL_SIZE);
                    }
                    else if (pieceState == PieceState.EMPTY){
                        g.drawRect(BOARD_OFFSET_WIDTH+u*CELL_SIZE,BOARD_OFFSET_HEIGHT+i*CELL_SIZE,CELL_SIZE,CELL_SIZE);
                    }
                    else if (pieceState == PieceState.BLUE){
                        if (fromLocation[0]!=u||fromLocation[1]!=i){
                            g.drawRect(BOARD_OFFSET_WIDTH+u*CELL_SIZE,BOARD_OFFSET_HEIGHT+i*CELL_SIZE,CELL_SIZE,CELL_SIZE);
                        }
                        else {
                            g.setColor(Color.GRAY);
                            g.fillRect(BOARD_OFFSET_WIDTH+u*CELL_SIZE,BOARD_OFFSET_HEIGHT+i*CELL_SIZE,CELL_SIZE,CELL_SIZE);
                        }
                        g.setColor(Color.BLUE);
                        g.fillOval(BOARD_OFFSET_WIDTH+u*CELL_SIZE+MIDDLE,BOARD_OFFSET_HEIGHT+i*CELL_SIZE+MIDDLE,ITEM_SIZE,ITEM_SIZE);
                    }
                    else {
                        if (fromLocation[0]!=u||fromLocation[1]!=i){
                            g.drawRect(BOARD_OFFSET_WIDTH+u*CELL_SIZE,BOARD_OFFSET_HEIGHT+i*CELL_SIZE,CELL_SIZE,CELL_SIZE);
                        }
                        else {
                            g.setColor(Color.GRAY);
                            g.fillRect(BOARD_OFFSET_WIDTH+u*CELL_SIZE,BOARD_OFFSET_HEIGHT+i*CELL_SIZE,CELL_SIZE,CELL_SIZE);
                        }
                        g.setColor(Color.RED);
                        g.fillOval(BOARD_OFFSET_WIDTH+u*CELL_SIZE+MIDDLE,BOARD_OFFSET_HEIGHT+i*CELL_SIZE+MIDDLE,ITEM_SIZE,ITEM_SIZE);
                    }
                    g.setColor(color);
                }
            }
            lock.notifyAll();
        }
    }

    @Override
    public String getCommand(String prompt) {
        if (winner!=null){winner=null;return "new";}
        try {
            synchronized (MONITOR){
                MONITOR.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return command;
    }

    @Override
    public void announceWinner(PieceState state) {
        JOptionPane.showMessageDialog(jFrame,state==PieceState.RED?"Red wins":state==PieceState.EMPTY?"draw":"Blue wins","Winner",JOptionPane.PLAIN_MESSAGE);
        winner = state;
    }

    @Override
    public void announceMove(Move move, PieceState player) {
        message = String.format("%s moves %s",player==PieceState.RED?"RED":"BLUE",move.toString(),"Winner");
    }

    @Override
    public void message(String format, Object... args) {
        JOptionPane.showMessageDialog(jFrame,String.format(format,args),"message",JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public void error(String format, Object... args) {
        JOptionPane.showMessageDialog(jFrame,String.format(format,args),"error",JOptionPane.ERROR_MESSAGE);
    }

    public void setVisible(boolean b) {
		jFrame.setVisible(b);
        super.setVisible(b);
    }

    public void pack() {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // check if click on board

        if (e.getY()<=BOARD_OFFSET_HEIGHT||e.getY()>=ROW*CELL_SIZE+BOARD_OFFSET_HEIGHT||e.getX()<=BOARD_OFFSET_WIDTH||e.getX()>=BOARD_OFFSET_WIDTH+COL*CELL_SIZE
                ||(e.getY()-BOARD_OFFSET_HEIGHT)%CELL_SIZE==0||(e.getX()-BOARD_OFFSET_WIDTH)%CELL_SIZE==0){
            return;
        }
        if (e.getButton()==MouseEvent.BUTTON2){return;}
        //Set block
        if (e.getButton()==MouseEvent.BUTTON3){
            int[] location = getLocation(e.getX()-BOARD_OFFSET_WIDTH,e.getY()-BOARD_OFFSET_HEIGHT);
            command = String.format("block %c%c",(char)(location[0]+'a'),(char)((ROW-1-location[1])+'1'));
            System.out.println(command);
            synchronized (MONITOR){
                MONITOR.notifyAll();
            }
            return;
        }

        int[] clickLocation = getLocation(e.getX()-BOARD_OFFSET_WIDTH,e.getY()-BOARD_OFFSET_HEIGHT);
        char[] boardLocations = getBoardLocation(clickLocation[0],clickLocation[1]);
        PieceState pieceState = board.getContent(boardLocations[0],boardLocations[1]);
        // cannot click block
        if (pieceState == PieceState.BLOCKED || pieceState==(board.nextMove()==PieceState.RED?PieceState.BLUE:PieceState.RED)||(fromLocation[0]<0&&pieceState==PieceState.EMPTY)){
            return;
        }
        if (fromLocation[0]>=0&&pieceState==PieceState.EMPTY&&(Math.abs(clickLocation[0]-fromLocation[0])>2||Math.abs(clickLocation[1]-fromLocation[1])>2)){
            return;
        }
        if (fromLocation[0]<0||pieceState == board.nextMove()){
            fromLocation[0] = clickLocation[0];
            fromLocation[1] = clickLocation[1];
            jFrame.repaint();
            return;
        }

        char[] fromBoardLocation = getBoardLocation(fromLocation[0],fromLocation[1]);
        command = String.format("%c%c-%c%c",fromBoardLocation[0],fromBoardLocation[1],boardLocations[0],boardLocations[1]);
        fromLocation[0] = -1;
        synchronized (MONITOR){
            MONITOR.notifyAll();
        }

    }

    /**
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return [col, row]
     */
    private static int[] getLocation(int x,int y){
        return new int[]{x/CELL_SIZE,y/CELL_SIZE};
    }
    private static char[] getBoardLocation(int col,int row){
        return new char[]{
                (char)(col+'a'),
                (char)(ROW-1-row+'1')
        };
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}

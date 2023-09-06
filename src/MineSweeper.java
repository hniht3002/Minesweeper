import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

public class MineSweeper extends JFrame {

    private class MineTile extends JButton {
        int r, c;
        public MineTile(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }
    private int tileSize = 70;
    private int numRows = 8, numCols = 8;

    private int boardWidth = numCols * tileSize;
    private int boardHeight = numCols * tileSize;

    private int mineCount = 10;

    private Random random = new Random();
    MineTile[][] board = new MineTile[numRows][numCols];

    ArrayList<MineTile> mineLists;
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();

    JPanel boardPanel = new JPanel();

    JPanel statePanel = new JPanel();
    JButton restart = new JButton("Restart");
    JPanel restartBtn = new JPanel(new FlowLayout());
    int tilesClicked = 0;
    boolean gameOver = false;

    JLabel currentMines = new JLabel();
    MineSweeper() {
        this.setTitle("Minesweeper");
        this.setSize(boardWidth,boardHeight);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial Unicode MS", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper");
        textLabel.setOpaque(true);


        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);

        //add state and restart button
        statePanel.setLayout(new BorderLayout());
        restart.setSize(new Dimension(200, 30));
        restart.setFocusPainted(false);
        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textLabel.setText("Minesweeper");
                boardPanel.removeAll();
                gameOver = false;
                start();
            }
        });

        restartBtn.add(restart);

        statePanel.add(restartBtn, BorderLayout.CENTER);
        textPanel.add(statePanel, BorderLayout.SOUTH);


        this.add(textPanel, BorderLayout.NORTH);
        boardPanel.setLayout(new GridLayout(numRows, numCols));
        this.add(boardPanel);

        start();

    }

    void start() {
        for(int r = 0; r < numRows; r++) {
            for(int c = 0; c < numCols; c++) {
                MineTile tile = new MineTile(r,c);
                board[r][c] = tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0,0,0,0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
                //tile.setText("💣");
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {

                        if(gameOver) {
                            return;
                        }

                        MineTile tile = (MineTile) e.getSource();

                        //left click
                        if(e.getButton() == MouseEvent.BUTTON1) {
                            if(tile.getText() == ""){
                                if(mineLists.contains(tile)) {
                                    revealMines();
                                } else {
                                    checkMines(tile.r, tile.c);
                                }
                            }
                        }
                        //right click
                        else if (e.getButton() == MouseEvent.BUTTON3) {
                            if(tile.getText() == "" && tile.isEnabled()){
                                tile.setText("🚩");
                            } else if (tile.getText() == "🚩") {
                                tile.setText("");
                            }
                        }
                    }
                });
                boardPanel.add(tile);
                ;
            }
        }
        this.setVisible(true);
        setMines();
    }

    void revealMines() {
        for(int i = 0; i < mineLists.size(); i++) {
            MineTile tile = mineLists.get(i);
            tile.setText("💣");
        }

        gameOver = true;
        textLabel.setText("Game Over");
    }

    void checkMines(int r, int c) {
        if(r < 0 || r >= numRows || c < 0 || c >= numCols){
            return;
        }
        MineTile tile = board[r][c];
        if(!tile.isEnabled()) {
            return;
        }
        tile.setEnabled(false);
        tilesClicked++;
        int mineFound = 0;

        mineFound += countMine(r-1, c-1); //top left
        mineFound += countMine(r - 1, c);   //top
        mineFound += countMine(r - 1, c + 1);   //top right
        mineFound += countMine(r, c - 1); // left
        mineFound += countMine(r, c + 1); //right
        mineFound += countMine(r + 1, c- 1); //bottom left
        mineFound += countMine(r + 1, c);   //bottom
        mineFound += countMine(r + 1, c + 1); //bottom right

        if(mineFound > 0) {
            tile.setText(Integer.toString(mineFound));
        } else {
            tile.setText("");

            checkMines(r - 1, c-1);
            checkMines(r - 1, c);
            checkMines(r - 1, c + 1);
            checkMines(r, c - 1);
            checkMines(r, c + 1);
            checkMines(r + 1, c - 1);
            checkMines(r + 1, c);
            checkMines(r + 1, c + 1);
        }
        if(tilesClicked == numCols * numRows - mineLists.size()) {
            gameOver = true;
            textLabel.setText("You Win 🏆!");
            revealMines();
        }
    }

    int countMine(int r, int c) {
        if(r < 0 || r >= numRows || c < 0 || c >= numCols){
            return 0;
        }

        if(mineLists.contains(board[r][c])) {
            return 1;
        }

        return 0;
    }


    void setMines() {
        mineLists = new ArrayList<MineTile>();
        int mineLeft = mineCount;
        while (mineLeft > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);

            MineTile tile = board[r][c];
            if(!mineLists.contains(tile)) {
                mineLists.add(tile);
                mineLeft--;
            }
        }
    }
}

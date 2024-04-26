package Checkers;

//import org.reflections.Reflections;
//import org.reflections.scanners.Scanners;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.core.PFont;
import processing.event.MouseEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.checkerframework.checker.units.qual.C;

import java.awt.Font;
import java.io.*;
import java.util.*;

public class App extends PApplet {

    List < int[] > availableMoves = new ArrayList < > ();

    public static final int CELLSIZE = 96;
    public static final int PIECE_RADIUS = 33;

    public static final int PIECE_COUNT = 12;

    public static final int SIDEBAR = 0;
    public static final int BOARD_WIDTH = 8;
    public static final int[] BLACK_RGB = {
        181,
        136,
        99
    };
    public static final int[] WHITE_RGB = {
        240,
        217,
        181
    };
    public static final float[][][] coloursRGB = new float[][][] {
        // default - white & black
        {
            {
                WHITE_RGB[0], WHITE_RGB[1], WHITE_RGB[2]
            }, {
                BLACK_RGB[0],
                BLACK_RGB[1],
                BLACK_RGB[2]
            }
        },
        // green
        {
            {
                105,
                138,
                76
            }, // when on white cell
            {
                105,
                138,
                76
            } // when on black cell
        },
        // blue
        {
            {
                196,
                224,
                232
            },
            {
                170,
                210,
                221
            }
        }
    };

    public static int WIDTH = CELLSIZE * BOARD_WIDTH + SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH * CELLSIZE;

    public static final int FPS = 60;

    // STEP 2b: START
    private List < Piece > pieces;
    // STEP 2b: END

    // STEP 3a: START
    private int highlightRow = -1;
    private int highlightCol = -1;
    // STEP 3a: END

    // STEP 4a: START
    private String winner = null;
    // STEP 4a: END

    private boolean isWhitePlaying = true;

    public App() {

    }

    /**
     * Initialise the setting of the window size.
     */
    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    @Override
    public void setup() {
        frameRate(FPS);

        // Set up the data structures used for storing data in the game
        // STEP 2b: START
        this.pieces = new ArrayList < > ();

        for (int i = 0; i < PIECE_COUNT / 4; i++) {
            for (int j = (i + 1) % 2; j < BOARD_WIDTH; j += 2) {
                this.pieces.add(new Piece("w", i, j));
            }
        }

        for (int i = BOARD_WIDTH - (PIECE_COUNT / 4); i < BOARD_WIDTH; i++) {
            for (int j = (i + 1) % 2; j < BOARD_WIDTH; j += 2) {
                this.pieces.add(new Piece("b", i, j));
            }
        }
        // STEP 2b: END
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
    @Override
    public void keyPressed() {

    }

    /**
     * Receive key released signal from the keyboard.
     */
    @Override
    public void keyReleased() {

    }


    // STEP 5a: START
    /**
     * Calculate available moves for the selected piece.
     * 
     * @param piece The selected piece
     * @return A list of available moves as positions (row, col)
     */
    int[] directions;
    public List<int[]> calculateAvailableMoves(Piece piece) {
        // Clear available moves from previous calculations
        availableMoves.clear();
        
        // Check diagonal moves for regular pieces
        int[] directions;
        if (piece.isKing()) {
            directions = new int[]{-1, 1, -1, 1}; // All directions for kings
        } else if (piece.getType().equals("w")) {
            directions = new int[]{-1, 1}; // Only forward diagonal for white pieces
        } else {
            directions = new int[]{1, -1}; // Only forward diagonal for black pieces
        }
        
        for (int dir : directions) {
            int newRow = piece.getRow() + dir;
            if (!piece.isKing()) {
                // Ensure regular pieces can only move towards the opponent's side
                if ((piece.getType().equals("w") && newRow < piece.getRow()) || 
                    (piece.getType().equals("b") && newRow > piece.getRow())) {
                    continue;
                }
            }
            for (int newCol : new int[]{piece.getCol() - 1, piece.getCol() + 1}) {
                if (isValidMove(newRow, newCol)) {
                    availableMoves.add(new int[]{newRow, newCol});
                }
            }
        }
        
        // Check jumps
        for (int dir : directions) {
            int jumpRow = piece.getRow() + 2 * dir;
            for (int jumpCol : new int[]{piece.getCol() - 2, piece.getCol() + 2}) {
                int midRow = (piece.getRow() + jumpRow) / 2;
                int midCol = (piece.getCol() + jumpCol) / 2;
        
                // Ensure regular pieces can only jump towards the opponent's side
                if (!piece.isKing() && 
                    ((piece.getType().equals("w") && jumpRow < piece.getRow()) || 
                    (piece.getType().equals("b") && jumpRow > piece.getRow()))) {
                    continue;
                }
        
                if (isValidJump(piece, midRow, midCol, jumpRow, jumpCol)) {
                    availableMoves.add(new int[]{jumpRow, jumpCol});
                }
            }
        }
        
        return availableMoves;
    }
    
    
    
    /**
     * Check if a move to the specified position is valid.
     * 
     * @param row The row of the position
     * @param col The column of the position
     * @return True if the move is valid, false otherwise
     */
    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < BOARD_WIDTH && col >= 0 && col < BOARD_WIDTH;
    }

    /**
     * Check if a jump to the specified position is valid.
     * 
     * @param piece   The selected piece
     * @param midRow  The row of the jumped-over piece
     * @param midCol  The column of the jumped-over piece
     * @param jumpRow The row of the destination
     * @param jumpCol The column of the destination
     * @return True if the jump is valid, false otherwise
     */
    private boolean isValidJump(Piece piece, int midRow, int midCol, int jumpRow, int jumpCol) {
        if (!isValidMove(jumpRow, jumpCol)) {
            return false;
        }
    
        Piece jumpedPiece = getPieceAt(midRow, midCol);
        return jumpedPiece != null && !jumpedPiece.getType().equals(piece.getType());
    }

    private void checkForPromotion(Piece piece) {
        // If the piece is a white piece and reaches the last row (row 0),
        // or if it's a black piece and reaches the last row (row 7),
        // promote it to a king
        if ((piece.getType().equals("w") && piece.getRow() == 7) ||
            (piece.getType().equals("b") && piece.getRow() == 0)) {
            piece.setKing(true);
        }
    }

    /**
     * Get the piece at the specified position.
     * 
     * @param row The row of the position
     * @param col The column of the position
     * @return The piece at the specified position, or null if no piece exists
     */
    private Piece getPieceAt(int row, int col) {
        for (Piece piece: pieces) {
            if (piece.getRow() == row && piece.getCol() == col) {
                return piece;
            }
        }
        return null;
    }
    


    // STEP 4b: START
    /**
     * Identifies the winner of the game if there is one.
     */
    public void findWinner() {
        if (this.winner != null) {
            return;
        }

        boolean blackFound = false;
        boolean whiteFound = false;

        for (Piece p: pieces) {
            if (p.getType().toLowerCase().equals("b")) {
                blackFound = true;
            }

            if (p.getType().toLowerCase().equals("w")) {
                whiteFound = true;
            }
        }

        if (!(blackFound ^ whiteFound)) {
            return;
        }

        if (blackFound) {
            this.winner = "Black";
        } else {
            this.winner = "White";
        }
    }
    

 
    @Override
    public void mousePressed(MouseEvent e) {
       
    
        int boardX = e.getX() / CELLSIZE;
        int boardY = e.getY() / CELLSIZE;
    
        boolean newHighlight = false;
    
      
        if (highlightCol != -1 && highlightRow != -1) {
           
            for (int[] move : availableMoves) {
                if (move[0] == boardY && move[1] == boardX) {
                    
                    Piece clickedPiece = null;
                    for (Piece piece : pieces) {
                        if (piece.getRow() == highlightRow && piece.getCol() == highlightCol) {
                            clickedPiece = piece;
                            break;
                        }
                    }
                    if (clickedPiece != null) {
                        
                        boolean pieceAlreadyPresent = false;
                        for (Piece piece : pieces) {
                            if (piece.getRow() == boardY && piece.getCol() == boardX) {
                                pieceAlreadyPresent = true;
                                break;
                            }
                        }
                        
                        if (!pieceAlreadyPresent) {
                            
                            clickedPiece.setRow(boardY);
                            clickedPiece.setCol(boardX);
                            checkForPromotion(clickedPiece);
    
                         
                            if (Math.abs(clickedPiece.getRow() - highlightRow) == 2) {
                                
                                int jumpedRow = (clickedPiece.getRow() + highlightRow) / 2;
                                int jumpedCol = (clickedPiece.getCol() + highlightCol) / 2;
    
                                
                                Piece jumpedPiece = getPieceAt(jumpedRow, jumpedCol);
                                if (jumpedPiece != null) {
                                    pieces.remove(jumpedPiece);
                                }
                            }
    
                            
                            availableMoves.clear();
                            
                            isWhitePlaying = !isWhitePlaying;
                        }
                    }
                    
                    highlightCol = -1;
                    highlightRow = -1;
                    availableMoves.clear();
                    break; 
                }
            }
        } else {
            // Check if the clicked position contains a piece
            for (Piece piece : pieces) {
                if ((isWhitePlaying && piece.getType().equals("w")) || (!isWhitePlaying && piece.getType().equals("b"))) {
                    if (boardX == piece.getCol() && boardY == piece.getRow()) {
                        highlightCol = piece.getCol();
                        highlightRow = piece.getRow();
                        availableMoves = calculateAvailableMoves(piece);
                        newHighlight = true;
                        break;
                    }
                }
            }
    
            if (!newHighlight) {
                highlightCol = -1;
                highlightRow = -1;
                availableMoves.clear();
            }
        }
    }
    


    @Override
    public void mouseDragged(MouseEvent e) {

    }

    private void drawAvailableMoves() {
        fill(0, 0, 255, 100); // Transparent blue
        for (int[] move : availableMoves) {
            int row = move[0];
            int col = move[1];
            // Check if there is a piece at the move position
            boolean pieceAtPosition = false;
            for (Piece piece : pieces) {
                if (piece.getRow() == row && piece.getCol() == col) {
                    pieceAtPosition = true;
                    break;
                }
            }
            // If there's no piece at the move position, highlight it
            if (!pieceAtPosition) {
                rect(col * CELLSIZE, row * CELLSIZE, CELLSIZE, CELLSIZE);
            }
        }
    }

    
    

    /**
     * Draw all elements in the game by current frame.
     */
    @Override
    public void draw() {
        this.noStroke();
        background(180);
    
        PFont pressStart = createFont("Arial", 48);
        textFont(pressStart);
      

        
        for (int row = 0; row < BOARD_WIDTH; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                setFill(0, (row + col) % 2);
                rect(row * CELLSIZE, col * CELLSIZE, CELLSIZE, CELLSIZE);

            }
        }
        

        // draw highlighted cells
      
        if (highlightCol != -1 && highlightRow != -1) {
            setFill(1, (highlightRow + highlightCol) % 2);
            rect(highlightCol * CELLSIZE, highlightRow * CELLSIZE, CELLSIZE, CELLSIZE);
        }
      
       
        for (Piece piece: this.pieces) {
            piece.draw(this);
        }
       
        findWinner();

        if (this.winner != null) {
            fill(255, 0, 0);
            text("       " + "       " + this.winner + " wins!", CELLSIZE, CELLSIZE * BOARD_WIDTH / 2);

        }
         
        
        drawAvailableMoves();
        
     

    }

    /**
     * Set fill colour for cell background
     * 
     * @param colourCode   The colour to set
     * @param blackOrWhite Depending on if 0 (white) or 1 (black) then the cell may
     *                     have different shades
     */
    public void setFill(int colourCode, int blackOrWhite) {
        this.fill(coloursRGB[colourCode][blackOrWhite][0], coloursRGB[colourCode][blackOrWhite][1],
            coloursRGB[colourCode][blackOrWhite][2]);
    }

    public static void main(String[] args) {
        PApplet.main("Checkers.App");
    }

}
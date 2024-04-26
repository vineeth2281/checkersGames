package Checkers;

// STEP 2a: START
public class Piece {
  private String type;
  private int row;
  private int col;
  private boolean isKing; 

  public Piece(String type, int row, int col) {
    this.type = type;
    this.row = row;
    this.col = col;
    this.isKing = false;
  }

  public boolean isKing() {
    return isKing;
}

public void setKing(boolean isKing) {
    this.isKing = isKing;
}
  
  public String getType() {
    return this.type;
  }

  public void setRow(int row) {
    this.row = row;
}

// Setter method for column
public void setCol(int col) {
    this.col = col;
}

  public int getRow() {
    return this.row;
  }

  public int getCol() {
    return this.col;
  }

  public void move(int row, int col) {
    this.row = row;
    this.col = col;
  }
  // STEP 2a: END

  // STEP 2c: START
  public void draw(App app) {

 
    if (this.type.equals("w")) {
      // Draw outer circle (black)
      app.fill(0, 0, 0);
      app.ellipse(col * App.CELLSIZE + App.CELLSIZE / 2, row * App.CELLSIZE + App.CELLSIZE / 2,
          App.PIECE_RADIUS * 2 + 18, App.PIECE_RADIUS * 2 + 18);
      
      // Draw middle circle (white)
      app.fill(256, 256, 256);
      app.ellipse(col * App.CELLSIZE + App.CELLSIZE / 2, row * App.CELLSIZE + App.CELLSIZE / 2,
          App.PIECE_RADIUS * 2, App.PIECE_RADIUS * 2);

      if (isKing) {
          // Draw inner circle (black ring)
          app.fill(0, 0, 0);
          app.ellipse(col * App.CELLSIZE + App.CELLSIZE / 2, row * App.CELLSIZE + App.CELLSIZE / 2,
              App.PIECE_RADIUS + 13, App.PIECE_RADIUS + 13); // Increase the radius here

          // Draw inner circle (white ring)
          app.fill(256, 256, 256);
          app.ellipse(col * App.CELLSIZE + App.CELLSIZE / 2, row * App.CELLSIZE + App.CELLSIZE / 2,
              App.PIECE_RADIUS - 8, App.PIECE_RADIUS - 8); // Adjust the radius here
      }
  }



  

    if (this.type.equals("b")) {
      app.fill(256, 256, 256);
      app.ellipse(col * App.CELLSIZE + App.CELLSIZE / 2, row * App.CELLSIZE + App.CELLSIZE / 2,
          App.PIECE_RADIUS * 2 + 18, App.PIECE_RADIUS * 2 + 18);

      app.fill(0, 0, 0);
      app.ellipse(col * App.CELLSIZE + App.CELLSIZE / 2, row * App.CELLSIZE + App.CELLSIZE / 2,
          App.PIECE_RADIUS * 2, App.PIECE_RADIUS * 2);

          if (isKing) {
              // Draw small white circle inside black circle
    app.fill(256, 256, 256);
    app.ellipse(col * App.CELLSIZE + App.CELLSIZE / 2, row * App.CELLSIZE + App.CELLSIZE / 2,
                App.PIECE_RADIUS * 1.5f, App.PIECE_RADIUS * 1.5f);

    // Draw small black circle inside white circle
    app.fill(0, 0, 0);
    app.ellipse(col * App.CELLSIZE + App.CELLSIZE / 2, row * App.CELLSIZE + App.CELLSIZE / 2,
                App.PIECE_RADIUS, App.PIECE_RADIUS);
}

    }
  }
  // STEP 2c: END
}

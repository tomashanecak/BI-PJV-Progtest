package thedrake;

import java.io.PrintWriter;

public class Board implements JSONSerializable {
    private final BoardTile[][] board;
    private final int dimension;

    // Konstruktor. Vytvoří čtvercovou hrací desku zadaného rozměru, kde všechny dlaždice jsou prázdné, tedy BoardTile.EMPTY
    public Board(int dimension) {
        this.dimension = dimension;
        board = new BoardTile[dimension][dimension];
        for(int i = 0; i < dimension; i++){
            for(int j = 0; j < dimension; j++){
                board[i][j] = BoardTile.EMPTY;
            }
        }
    }

    // pretazeny konstruktor pre vytvaranie kopie (uz inicializovanej dosky)
    public Board(int dimension, BoardTile[][] board) {
        this.dimension = dimension;
        this.board = board;
    }

    // Rozměr hrací desky
    public int dimension() {
        return dimension;
    }

    // Vrací dlaždici na zvolené pozici.
    public BoardTile at(TilePos pos) {
       if(pos.equals(TilePos.OFF_BOARD))
           throw new UnsupportedOperationException();
       return board[pos.i()][pos.j()];
    }

    // Vytváří novou hrací desku s novými dlaždicemi. Všechny ostatní dlaždice zůstávají stejné
    public Board withTiles(TileAt... ats) {
        // Create deep copy of old board
        BoardTile[][] newBoard = new BoardTile[dimension][dimension];
        for (int k = 0; k < dimension; k++){
            newBoard[k] = board[k].clone();
        }

        // Replace tiles on correct indexes
        for (int i = 0; i < dimension; i++)
            for (int j = 0; j < dimension; j++)
                for (TileAt at : ats)
                    if (at.pos.equalsTo(i, j))
                        newBoard[i][j] = at.tile;

        return new Board(dimension, newBoard);
    }

    // Vytvoří instanci PositionFactory pro výrobu pozic na tomto hracím plánu
    public PositionFactory positionFactory() {
        return new PositionFactory(dimension);
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.print("{\"dimension\":" + dimension());

        writer.print(",\"tiles\":[");
        int numRows = board.length;
        int numCols = board[0].length;
        for (int col = 0; col < board[0].length; col++) {
            for (int row = 0; row < board.length; row++) {
                board[row][col].toJSON(writer);

                // add comma after every element except for the last element in the last row
                if (!(row == numRows - 1 && col == numCols - 1)) {
                    writer.print(",");
                }
            }
        }
        writer.print("]}");
    }

    public static class TileAt {
        public final BoardPos pos;
        public final BoardTile tile;

        public TileAt(BoardPos pos, BoardTile tile) {
            this.pos = pos;
            this.tile = tile;
        }
    }
}


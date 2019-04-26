package amazons;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static amazons.Utils.error;

/**
 * Represents a position on an Amazons board.  Positions are numbered
 * from 0 (lower-left corner) to 99 (upper-right corner).  Squares
 * are immutable and unique: there is precisely one square created for
 * each distinct position.  Clients create squares using the factory method
 * sq, not the constructor.  Because there is a unique Square object for each
 * position, you can freely use the cheap == operator (rather than the
 * .equals method) to compare Squares, and the program does not waste time
 * creating the same square over and over again.
 *
 * @author Shichao Han
 */
final class Square {

    /**
     * The regular expression for a square designation (e.g.,
     * a3). For convenience, it is in parentheses to make it a
     * group.  This subpattern is intended to be incorporated into
     * other pattern that contain square designations (such as
     * patterns for moves).
     */
    static final String SQ = "([a-j](?:[1-9]|10))";
    /**
     * Definitions of direction for queenMove.  DIR[k] = (dcol, drow)
     * means that to going one step from (col, row) in direction k,
     * brings us to (col + dcol, row + drow).
     */
    private static final int[][] DIR = {
            {0, 1}, {1, 1}, {1, 0}, {1, -1},
            {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}
    };
    /**
     * The cache of all created squares, by index.
     */
    private static final Square[] SQUARES =
            new Square[Board.SIZE * Board.SIZE];
    /**
     * SQUARES viewed as a List.
     */
    private static final List<Square> SQUARE_LIST = Arrays.asList(SQUARES);

    /**Possible col index.*/
    private static String _aToJ = "abcdefghij";

    static {
        for (int i = Board.SIZE * Board.SIZE - 1; i >= 0; i -= 1) {
            SQUARES[i] = new Square(i);
        }
    }

    /**
     * My index position.
     */
    private final int _index;
    /**
     * My row and column (redundant, since these are determined by _index).
     */
    private final int _row, _col;
    /**
     * My String denotation.
     */
    private final String _str;

    /**
     * Return the Square with index INDEX.
     */
    Square(int index) {
        _index = index;
        _row = index / 10;
        _col = index % 10;
        Character presentation = _aToJ.charAt(_col);
        String myRow = Integer.toString(_row + 1);
        _str = String.format(presentation + myRow);
    }

    /**
     * Return true iff COL ROW is a legal square.
     */
    static boolean exists(int col, int row) {
        return row >= 0 && col >= 0 && row < Board.SIZE && col < Board.SIZE;
    }

    /**
     * Return the (unique) Square denoting COL ROW.
     */
    static Square sq(int col, int row) {
        if (!exists(row, col)) {
            throw error("row or column out of bounds");
        }
        int myInd = col + row * 10;
        return sq(myInd);
    }

    /**
     * Return the (unique) Square denoting the position with index INDEX.
     */
    static Square sq(int index) {
        return SQUARES[index];
    }

    /**
     * Return the (unique) Square denoting the position COL ROW, where
     * COL ROW is the standard text format for a square (e.g., a4).
     */
    static Square sq(String col, String row) {
        int colIndex = _aToJ.indexOf(col);
        int rowIndex = Integer.parseInt(row) - 1;
        return sq(colIndex, rowIndex);

    }

    /**
     * Return the (unique) Square denoting the position in POSN, in the
     * standard text format for a square (e.g. a4). POSN must be a
     * valid square designation.
     */
    static Square sq(String posn) {
        assert posn.matches(SQ);
        char col = posn.charAt(0);
        String row = posn.substring(1);
        int myrow = Integer.parseInt(row) - 1;
        int mycol = _aToJ.indexOf(col);
        return sq(mycol, myrow);
    }

    /**
     * Return an iterator over all Squares.
     */
    static Iterator<Square> iterator() {
        return SQUARE_LIST.iterator();
    }

    /** Get the list of the square.
     * @return the square list.
     * */
    public static List<Square> getSquareList() {
        return SQUARE_LIST;
    }

    /**
     * Return my row position, where 0 is the bottom row.
     */
    int row() {
        return _row;
    }

    /**
     * Return my column position, where 0 is the leftmost column.
     */
    int col() {
        return _col;
    }

    /**
     * Return my index position (0-99).  0 represents square a1, and 99
     * is square j10.
     */
    int index() {
        return _index;
    }

    /**
     * Return true iff THIS - TO is a valid queen move.
     */
    boolean isQueenMove(Square to) {
        int desInd = to.index();
        int desCol = to.col();
        int desRow = to.row();
        int myCol = this.col();
        int myRow = this.row();

        if (desInd > Board.SIZE * Board.SIZE - 1
                || desInd < 0
                || desInd == this.index()) {
            return false;
        }

        int diffCol = desCol - myCol;
        int diffRow = desRow - myRow;
        if (diffCol == 0 || diffRow == 0) {
            return true;
        } else {
            if (Math.abs(diffRow) == Math.abs(diffCol)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return the Square that is STEPS>0 squares away from me in direction
     * DIR, or null if there is no such square.
     * DIR = 0 for north, 1 for northeast, 2 for east, etc., up to 7 for west.
     * If DIR has another value, return null. Thus, unless the result
     * is null the resulting square is a queen move away rom me.
     */
    Square queenMove(int dir, int steps) {
        int vcol = DIR[dir][0];
        int vrow = DIR[dir][1];
        int dcol = vcol * steps;
        int drow = vrow * steps;
        int nextCol = col() + dcol;
        int nextRow = row() + drow;
        if (!exists(nextCol, nextRow)) {
            return null;
        }
        return Square.sq(nextCol, nextRow);
    }

    /**
     * Return the direction (an int as defined in the documentation
     * for queenMove) of the queen move THIS-TO.
     */
    int direction(Square to) {
        int drow = to.row() - row();
        int dcol = to.col() - col();
        if (drow != 0) {
            drow = drow / Math.abs(drow);
        }
        if (dcol != 0) {
            dcol = dcol / Math.abs(dcol);
        }

        int result = -1;
        for (int i = 0; i <= 7; i++) {
            if (DIR[i][0] == dcol && DIR[i][1] == drow) {
                result = i;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return _str;
    }

    /**Count the steps to the destination square.
     * @param to destination square.
     * @return the number of steps count.
     * */
    int countSteps(Square to) {

        int result = 0;
        int drow = to.row() - row();
        int dcol = to.col() - col();
        int dirCount = Math.abs(DIR[direction(to)][0])
                + Math.abs(DIR[direction(to)][1]);
        result = (Math.abs(drow) + Math.abs(dcol)) / dirCount;
        return result;
    }

}


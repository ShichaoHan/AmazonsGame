package amazons;

import org.junit.Test;
import ucb.junit.textui;

import java.util.Iterator;

import static amazons.Piece.*;
import static org.junit.Assert.*;

/**
 * The suite of all JUnit tests for the amazons package.
 *
 * @author Shichao Han
 */
public class UnitTest {

    static final String INIT_BOARD_STATE =
            "   - - - B - - B - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   B - - - - - - - - B\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   W - - - - - - - - W\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - W - - W - - -\n";
    static final String SMILE =
            "   - - - - - - - - - -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - S - S - - S - S -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - W - - - - W - -\n"
                    + "   - - - W W W W - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n";

    /**
     * Run the JUnit tests in this package. Add xxxTest.class entries to
     * the arguments of runClasses to run other JUnit tests.
     */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /**
     * Tests basic correctness of put and get on the initialized board.
     */
    @Test
    public void testBasicPutGet() {
        Board b = new Board();
        b.put(BLACK, Square.sq(3, 5));
        assertEquals(b.get(3, 5), BLACK);
        b.put(WHITE, Square.sq(9, 9));
        assertEquals(b.get(9, 9), WHITE);
        b.put(EMPTY, Square.sq(3, 5));
        assertEquals(b.get(3, 5), EMPTY);
    }

    /**
     * Tests proper identification of legal/illegal queen moves.
     */
    @Test
    public void testIsQueenMove() {
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(1, 5)));
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(2, 7)));
        assertFalse(Square.sq(0, 0).isQueenMove(Square.sq(5, 1)));
        assertTrue(Square.sq(1, 1).isQueenMove(Square.sq(9, 9)));
        assertTrue(Square.sq(2, 7).isQueenMove(Square.sq(8, 7)));
        assertTrue(Square.sq(3, 0).isQueenMove(Square.sq(3, 4)));
        assertTrue(Square.sq(7, 9).isQueenMove(Square.sq(0, 2)));
    }

    /**
     * Tests toString for initial board state and a smiling board state. :)
     */
    @Test
    public void testToString() {
        Board b = new Board();
        assertEquals(INIT_BOARD_STATE, b.toString());
        makeSmile(b);
        assertEquals(SMILE, b.toString());
    }

    private void makeSmile(Board b) {
        b.put(EMPTY, Square.sq(0, 3));
        b.put(EMPTY, Square.sq(0, 6));
        b.put(EMPTY, Square.sq(9, 3));
        b.put(EMPTY, Square.sq(9, 6));
        b.put(EMPTY, Square.sq(3, 0));
        b.put(EMPTY, Square.sq(3, 9));
        b.put(EMPTY, Square.sq(6, 0));
        b.put(EMPTY, Square.sq(6, 9));
        for (int col = 1; col < 4; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(2, 7));
        for (int col = 6; col < 9; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(7, 7));
        for (int lip = 3; lip < 7; lip += 1) {
            b.put(WHITE, Square.sq(lip, 2));
        }
        b.put(WHITE, Square.sq(2, 3));
        b.put(WHITE, Square.sq(7, 3));
    }

    @Test
    public void testDirection() {
        Square sq0 = new Square(0);
        Square sq99 = new Square(99);
        Square sq10 = new Square(10);
        Square sq88 = new Square(88);
        assertEquals(1, sq0.direction(sq99));
        assertEquals(0, sq0.direction(sq10));
        assertEquals(5, sq99.direction(sq88));
    }

    @Test
    public void testCountSteps() {
        Square sq0 = new Square(0);
        Square sq99 = new Square(99);
        Square sq10 = new Square(10);
        Square sq88 = new Square(88);
        assertEquals(1, sq0.countSteps(sq10));
        assertEquals(9, sq0.countSteps(sq99));
    }

    @Test
    public void testIsUnblockedMove() {
        Board tester = new Board();
        assertTrue(tester.isUnblockedMove(Square.sq("d1"),
                Square.sq("e1"), Square.sq("f1")));
        assertFalse(tester.isUnblockedMove(Square.sq("a4"), Square.sq("a8"),
                Square.sq("a10")));
        System.out.println(tester);
        assertTrue(tester.isUnblockedMove(Square.sq("a1"),
                Square.sq("a5"), Square.sq("a4")));

    }

    @Test
    public void testMakeMove() {
        Board tester1 = new Board();
        System.out.println(tester1);
        tester1.makeMove(Square.sq("d1"), Square.sq("e1"),
                Square.sq("f1"));
        assertEquals(SPEAR, tester1.get(5, 0));
        assertEquals(WHITE, tester1.get(4, 0));
        assertEquals(EMPTY, tester1.get(3, 0));
        assertEquals(1, tester1.numMoves());
        System.out.println(tester1);
        tester1.makeMove(Square.sq("g10"), Square.sq("i10"), Square.sq("j10"));
        assertEquals(SPEAR, tester1.get(9, 9));
        assertEquals(BLACK, tester1.get(8, 9));
        assertEquals(EMPTY, tester1.get(6, 9));
        assertEquals(2, tester1.numMoves());
    }

    @Test
    public void testUndo() {
        Board tester1 = new Board();
        tester1.makeMove(Square.sq("d1"), Square.sq("e1"),
                Square.sq("f1"));
        tester1.makeMove(Square.sq("g10"), Square.sq("i10"), Square.sq("j10"));
        tester1.undo();
        Board d = new Board();
        d.copy(tester1);
        System.out.println(d.getMyMoves());
        tester1.undo();
        System.out.println(d.getMyMoves());
        System.out.println(tester1.getMyMoves());
        System.out.println(tester1.turn());
        System.out.println(d.turn());

    }

    @Test
    public void testQueenMove() {
        Square sq0 = new Square(0);
        Square sq99 = new Square(99);
        Square sq10 = new Square(10);
        Square sq88 = new Square(88);
        Square newSq = sq0.queenMove(1, 9);
        assertEquals(9, newSq.row());
        assertEquals(9, newSq.col());
    }

    @Test
    public void testReachableMoveIterator() {
        Board c = new Board();
        Iterator<Square> tester2 = c.reachableFrom(Square.sq(0, 0),
                Square.sq(0, 0));
        while (tester2.hasNext()) {
            System.out.println(tester2.next());
        }


    }

    @Test
    public void testLegalMoveIterator() {
        Board d = new Board();
        d.put(SPEAR, 4, 8);
        d.put(SPEAR, 3, 8);
        d.put(SPEAR, 2, 8);
        d.put(SPEAR, 2, 9);
        d.put(EMPTY, 0, 6);
        d.put(EMPTY, 9, 6);
        d.put(WHITE, 6, 9);
        d.put(SPEAR, 5, 9);
        d.put(BLACK, 4, 9);
        d.put(SPEAR, 5, 8);
        d.put(BLACK, 0, 0);

        Iterator<Move> tester3 = d.legalMoves(BLACK);
        System.out.println(d);
        assertTrue(tester3.hasNext());
        System.out.println(tester3.next());

    }

    @Test
    public void testNewBoard() {
        Board myB = new Board();
        Iterator<Move> tester = myB.legalMoves(WHITE);
        int count = 0;
        while (tester.hasNext()) {
            tester.next();
            count += 1;
        }
        System.out.println(count);
        assertEquals(2176, count);
    }


    @Test
    public void testLegalMoves() {
        Board b = new Board();
        b.put(EMPTY, 3, 0);
        b.put(EMPTY, 6, 0);
        b.put(EMPTY, 0, 3);
        b.put(EMPTY, 9, 3);
        b.put(EMPTY, 9, 6);
        b.put(EMPTY, 3, 9);
        b.put(EMPTY, 6, 9);
        b.put(WHITE, 0, 7);
        b.put(WHITE, 1, 7);
        b.put(WHITE, 2, 8);
        b.put(WHITE, 3, 8);
        b.put(WHITE, 4, 8);
        b.put(WHITE, 3, 6);
        b.put(WHITE, 1, 5);
        b.put(WHITE, 0, 4);
        b.put(WHITE, 3, 4);
        b.put(WHITE, 4, 4);
        b.put(WHITE, 2, 3);
        Iterator<Move> iter1 = b.legalMoves(BLACK);
        Square from = Square.sq(0, 6);
        assertTrue(iter1.hasNext());
        assertEquals(Move.mv(from, Square.sq(1, 6),
                Square.sq(2, 7)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(1, 6),
                Square.sq(2, 6)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(1, 6),
                Square.sq(2, 5)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(1, 6),
                Square.sq(0, 5)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(1, 6),
                Square.sq(0, 6)), iter1.next());

        assertEquals(Move.mv(from, Square.sq(2, 6),
                Square.sq(2, 7)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(2, 6),
                Square.sq(3, 7)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(2, 6),
                Square.sq(3, 5)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(2, 6),
                Square.sq(2, 5)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(2, 6),
                Square.sq(2, 4)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(2, 6),
                Square.sq(1, 6)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(2, 6),
                Square.sq(0, 6)), iter1.next());

        assertEquals(Move.mv(from, Square.sq(0, 5),
                Square.sq(0, 6)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(0, 5),
                Square.sq(1, 6)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(0, 5),
                Square.sq(2, 7)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(0, 5),
                Square.sq(1, 4)), iter1.next());
        assertFalse(iter1.hasNext());
    }

    @Test
    public void testCopy() {
        Board b = new Board();
        b.put(EMPTY, 3, 0);
        b.put(EMPTY, 6, 0);
        b.put(EMPTY, 0, 3);
        b.put(EMPTY, 9, 3);
        b.put(EMPTY, 9, 6);
        b.put(EMPTY, 3, 9);
        b.put(EMPTY, 6, 9);
        b.put(WHITE, 0, 7);
        b.put(WHITE, 1, 7);
        b.put(WHITE, 2, 8);
        b.put(WHITE, 3, 8);
        b.put(WHITE, 4, 8);
        b.put(WHITE, 3, 6);
        b.put(WHITE, 1, 5);
        b.put(WHITE, 0, 4);
        b.put(WHITE, 3, 4);
        b.put(WHITE, 4, 4);
        b.put(WHITE, 2, 3);
        b.makeMove(Square.sq(0, 4), Square.sq
                (0, 5), Square.sq(0, 4));
        Board c = new Board();
        c.copy(b);
        System.out.println(c.getMyMoves());
        System.out.println(b.getMyMoves());
        assertEquals(c.winner(), b.winner());
        assertEquals(c.numMoves(), b.numMoves());
        c.undo();

    }


}


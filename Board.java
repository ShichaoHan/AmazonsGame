package amazons;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import static amazons.Move.mv;
import static amazons.Piece.*;


/**
 * The state of an Amazons Game.
 *
 * @author Shichao Han
 */
class Board {

    /**
     * The number of squares on a side of the board.
     */
    static final int SIZE = 10;

    /**
     * The DIRECTIONS of the board.
     */
    private static final int[][] DIRECTIONS = {
            {0, 1}, {1, 1}, {1, 0}, {1, -1},
            {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}
    };
    /**
     * An empty iterator for initialization.
     */
    private static final Iterator<Square> NO_SQUARES =
            Collections.emptyIterator();
    /**
     * A parameter that is used for alpha beta prunning.
     */
    private int _myValue;
    /**
     * Piece whose turn it is (BLACK or WHITE).
     */
    private Piece _turn;
    /**
     * Cached value of winner on this board, or EMPTY if it has not been
     * computed.
     */
    private Piece _winner;
    /**
     * Use an HashMap<String, ArrayList> to store the board.
     */
    private HashMap<Integer, Piece> _mySquareMap;
    /**
     * Count the time of the moves in the board.
     */
    private int _numMoves;

    /**
     * The stack that stores all the past moves.
     */
    private Stack<Move> _myMoves;

    /**
     * Initializes a game board with SIZE squares on a side in the
     * initial position.
     */
    Board() {
        init();
    }

    /**
     * Initializes a copy of MODEL.
     */
    Board(Board model) {
        copy(model);
    }

    /**
     * Copies MODEL into me.
     */
    void copy(Board model) {
        init();
        for (int i = 0; i <=  SIZE * SIZE - 1; i++) {
            put(model.get(Square.sq(i)), Square.sq(i));
        }


        Move[] storage = new Move[model.getMyMoves().size()];
        for (int i = storage.length - 1; i >= 0; i--) {
            Move temp = model.getMyMoves().pop();
            storage[i] = temp;
        }

        for (int i = 0; i < storage.length; i++) {
            this._myMoves.push(storage[i]);
            model._myMoves.push(storage[i]);
        }
        this._turn = model.turn();
        this._numMoves = model.numMoves();
    }

    /**
     * Clears the board to the initial position.
     */
    void init() {
        _turn = WHITE;
        _winner = EMPTY;
        _numMoves = 0;
        _mySquareMap = new HashMap<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                put(EMPTY, i, j);
            }
        }

        put(BLACK, 3, 9);
        put(BLACK, 6, 9);
        put(BLACK, 0, 6);
        put(BLACK, 9, 6);
        put(WHITE, 0, 3);
        put(WHITE, 3, 0);
        put(WHITE, 6, 0);
        put(WHITE, 9, 3);

        _myMoves = new Stack<Move>();
    }

    /**
     * Return the Piece whose move it is (WHITE or BLACK).
     */
    Piece turn() {
        return _turn;
    }

    /**
     * Return the number of moves (that have not been undone) for this
     * board.
     */
    int numMoves() {
        return _numMoves;
    }

    /**
     * Return the winner in the current position, or null if the game is
     * not yet finished.
     */
    Piece winner() {
        Iterator<Move> currentPlayer = new LegalMoveIterator(_turn);
        if (!currentPlayer.hasNext()) {
            return _turn.opponent();
        } else {
            return null;
        }


    }

    /**
     * Return the contents the square at S.
     */
    final Piece get(Square s) {
        return _mySquareMap.get(s.index());
    }

    /**
     * Return the contents of the square at (COL, ROW), where
     * 0 <= COL, ROW < 9.
     */
    final Piece get(int col, int row) {
        return _mySquareMap.get(col + row * SIZE);
    }

    /**
     * Return the contents of the square at COL ROW.
     */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /**
     * Set square S to P.
     */
    final void put(Piece p, Square s) {
        put(p, s.col(), s.row());
    }

    /**
     * Set square (COL, ROW) to P.
     */
    final void put(Piece p, int col, int row) {
        int squareIndex = row * SIZE + col;
        _mySquareMap.put(squareIndex, p);
        _winner = EMPTY;
    }

    /**
     * Set square COL ROW to P.
     */
    final void put(Piece p, char col, char row) {
        put(p, col - 'a', row - '1');
    }

    /**
     * Return true iff FROM - TO is an unblocked queen move on the current
     * board, ignoring the contents of ASEMPTY, if it is encountered.
     * For this to be true, FROM-TO must be a queen move and the
     * squares along it, other than FROM and ASEMPTY, must be
     * empty. ASEMPTY may be null, in which case it has no effect.
     */
    boolean isUnblockedMove(Square from, Square to, Square asEmpty) {
        if (!from.isQueenMove(to)) {
            return false;
        }
        int dir = from.direction(to);
        int numSteps = from.countSteps(to);
        int colInc = DIRECTIONS[dir][0];
        int rowInc = DIRECTIONS[dir][1];
        for (int i = 1; i <= numSteps; i++) {
            if (!(get(from.col() + colInc * i,
                    from.row() + rowInc * i).equals(EMPTY))) {
                if (asEmpty != null) {
                    if ((from.col() + colInc * i == asEmpty.col()
                            && from.row() + rowInc * i == asEmpty.row())) {
                        return true;
                    }
                }
                return false;
            }
        }


        return true;
    }

    /**
     * Move FROM-TO(SPEAR), assuming this is a leg.
     *
     * @return whether the FROM square is valid
     */
    boolean isLegal(Square from) {
        return get(from).equals(turn());
    }

    /**
     * Return true iff FROM-TO is a valid first part of move, ignoring
     * spear throwing.
     */
    boolean isLegal(Square from, Square to) {
        return isLegal(from) && from.isQueenMove(to)
                && get(to).equals(EMPTY);
    }

    /**
     * Return whether a move is valid.
     * @param from the square from which the move starts.
     * @param to the square to which the move towards.
     * @param spear the square where the move throws spear.
     * @param asEmpty where it treats as empty.
     */
    boolean isLegal(Square from, Square to, Square spear, Square asEmpty) {
        return isLegal(from, to) && isUnblockedMove(to, spear, from)
                && get(to).equals(EMPTY) && to.isQueenMove(spear)
                && (get(spear).equals(EMPTY) || spear.equals(asEmpty));
    }

    /**
     * Return true iff FROM-TO(SPEAR) is a legal move in the current
     * position.
     */
    boolean isLegal(Square from, Square to, Square spear) {
        return isLegal(from, to, spear, from);

    }

    /**
     * Return true iff MOVE is a legal move in the current
     * position.
     */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to(), move.spear());
    }

    /**
     * Move FROM-TO(SPEAR), assuming this is a legal move.
     */
    void makeMove(Square from, Square to, Square spear) {
        put(get(from), to);
        put(EMPTY, from);
        put(SPEAR, spear);
        Move myMove = Move.mv(from, to, spear);
        _myMoves.push(myMove);
        _turn = _turn.opponent();
        _numMoves += 1;
        _winner = winner();


    }

    /**
     * Move according to MOVE, assuming it is a legal move.
     */
    void makeMove(Move move) {
        makeMove(move.from(), move.to(), move.spear());
    }

    /**
     * Undo one move.  Has no effect on the initial board.
     */
    void undo() {
        if (!_myMoves.empty()) {
            Move lastMove = _myMoves.pop();
            Piece lastPiece = get(lastMove.to());
            put(EMPTY, lastMove.spear());
            put(EMPTY, lastMove.to());
            put(lastPiece, lastMove.from());
            _turn = _turn.opponent();
            _numMoves -= 1;
        }
    }

    /**
     * Return an Iterator over the Squares that are reachable by an
     * unblocked queen move from FROM. Does not pay attention to what
     * piece (if any) is on FROM, nor to whether the game is finished.
     * Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     * feature is useful when looking for Moves, because after moving a
     * piece, one wants to treat the Square it came from as empty for
     * purposes of spear throwing.)
     */
    Iterator<Square> reachableFrom(Square from, Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /**
     * Return an Iterator over all legal moves on the current board.
     */
    Iterator<Move> legalMoves() {
        return new LegalMoveIterator(_turn);
    }

    /**
     * Return an Iterator over all legal moves on the current board for
     * SIDE (regardless of whose turn it is).
     */
    Iterator<Move> legalMoves(Piece side) {
        return new LegalMoveIterator(side);
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = 9; i >= 0; i--) {
            result += "  ";
            for (int j = 0; j < SIZE; j++) {
                result += " ";
                result += get(j, i);
            }
            result += "\n";
        }
        return result;
    }

    /**
     * Return each board's assigned value.
     */
    public int getValue() {
        return _myValue;
    }

    /**
     * Set each board's assigned value.
     * @param input the assigned value of the board.
     */
    public void setValue(int input) {
        _myValue = input;
    }

    /**
     * Return each board's move history.
     */
    public Stack<Move> getMyMoves() {
        return _myMoves;
    }

    /**
     * An iterator used by reachableFrom.
     */
    private class ReachableFromIterator implements Iterator<Square> {

        /**
         * Starting square.
         */
        private Square _from;
        /**
         * Current direction.
         */
        private int _dir;
        /**
         * Current distance.
         */
        private int _steps;
        /**
         * Square treated as empty.
         */
        private Square _asEmpty;

        /**
         * Iterator of all squares reachable by queen move from FROM,
         * treating ASEMPTY as empty.
         */
        ReachableFromIterator(Square from, Square asEmpty) {
            _from = from;
            _dir = -1;
            _steps = 0;
            _asEmpty = asEmpty;
            toNext();
            while (_from.queenMove(_dir, _steps) == null
                    || !((get(_from.queenMove(_dir, _steps)).equals(EMPTY))
                    || _from.queenMove(_dir, _steps).equals(_asEmpty))) {
                toNext();
                if (_dir == 7) {
                    if (_from.queenMove(_dir, _steps) == null
                            || !((get(_from.queenMove(_dir, _steps)).
                            equals(EMPTY))
                            || _from.queenMove(_dir, _steps).
                            equals(_asEmpty))) {
                        _dir += 1;
                        if (_dir == 8) {
                            break;
                        }
                    }
                } else if (_dir == 8) {
                    break;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return _dir < 8;
        }

        @Override
        public Square next() {
            if (!hasNext()) {
                return null;
            }

            Square result = _from.queenMove(_dir, _steps);
            _steps += 1;
            while (hasNext() && (_from.queenMove(_dir, _steps) == null
                    || !((get(_from.queenMove(_dir, _steps)).equals(EMPTY))
                    || _from.queenMove(_dir, _steps).equals(_asEmpty)))) {
                toNext();
                if (_dir == 7) {
                    if (_from.queenMove(_dir, _steps) == null
                            || !((get(_from.queenMove(_dir, _steps)).
                            equals(EMPTY))
                            || _from.queenMove(_dir, _steps).
                            equals(_asEmpty))) {
                        _dir += 1;
                        if (_dir == 8) {
                            break;
                        }
                    }
                } else if (_dir == 8) {
                    break;
                }
            }


            return result;
        }

        /**
         * Advance _dir and _steps, so that the next valid Square is
         * _steps steps in direction _dir from _from.
         */
        private void toNext() {
            _dir = _dir + 1;
            _steps = 1;
        }
    }

    /**
     * An iterator used by legalMoves.
     */
    private class LegalMoveIterator implements Iterator<Move> {

        /**
         * Color of side whose moves we are iterating.
         */
        private Piece _fromPiece;
        /**
         * Current starting square.
         */
        private Square _start;
        /**
         * Remaining starting squares to consider.
         */
        private Iterator<Square> _startingSquares;
        /**
         * Current piece's new position.
         */
        private Square _nextSquare;
        /**
         * Remaining moves from _start to consider.
         */
        private Iterator<Square> _pieceMoves;
        /**
         * Remaining spear throws from _piece to consider.
         */
        private Iterator<Square> _spearThrows;
        /** The instance variable that counts.*/
        private int _count;

        /**
         * All legal moves for SIDE (WHITE or BLACK).
         */
        LegalMoveIterator(Piece side) {
            _startingSquares = Square.iterator();
            _spearThrows = NO_SQUARES;
            _pieceMoves = NO_SQUARES;
            _fromPiece = side;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _startingSquares.hasNext() || _spearThrows.hasNext();
        }

        @Override
        public Move next() {
            if (!hasNext()) {
                return null;
            }
            while (!(get(_start).equals(_fromPiece)
                    || _pieceMoves.hasNext())) {
                toNext();
            }
            if (_count == 0 || !_spearThrows.hasNext()) {
                _nextSquare = _pieceMoves.next();
                _spearThrows = new ReachableFromIterator(_nextSquare, _start);
            }

            Square spearPos = _spearThrows.next();
            Move result = mv(_start, _nextSquare, spearPos);
            _count += 1;
            if (!_pieceMoves.hasNext() && !_spearThrows.hasNext()) {
                toNext();
            }
            return result;
        }

        /**
         * Advance so that the next valid Move is
         * _start-_nextSquare(sp), where sp is the next value of
         * _spearThrows.
         */
        private void toNext() {
            while (_startingSquares.hasNext()) {
                _start = _startingSquares.next();
                if (get(_start).equals(_fromPiece)) {
                    break;
                }
            }
            _pieceMoves = new ReachableFromIterator(_start, _start);
            if (!_pieceMoves.hasNext()) {
                if (!_startingSquares.hasNext()) {
                    return;
                } else {
                    _start = _startingSquares.next();
                    toNext();
                }
            }
            _count = 0;
        }
    }


}

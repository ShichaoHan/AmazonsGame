package amazons;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import static amazons.Piece.BLACK;
import static amazons.Piece.WHITE;
import static amazons.Utils.iterable;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * A Player that automatically generates moves.
 *
 * @author Shichao Han
 */
class AI extends Player {

    /**
     * A position magnitude indicating a win (for white if positive, black
     * if negative).
     */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /**
     * A magnitude greater than a normal value.
     */
    private static final int INFTY = Integer.MAX_VALUE;
    /**
     * The move found by the last call to one of the ...FindMove methods
     * below.
     */
    private Move _lastFoundMove;

    /** Dividor using for maxDepth. */
    private static final int DIVID = 20;

    /**
     * A new AI with no piece or controller (intended to produce
     * a template).
     */
    AI() {
        this(null, null);
    }

    /**
     * A new AI playing PIECE under control of CONTROLLER.
     */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move move = findMove();
        if (move == null) {
            return "null";
        }
        _controller.reportMove(move);
        return move.toString();
    }

    /**
     * Return a move for me from the current position, assuming there
     * is a move.
     */
    private Move findMove() {
        Board b = new Board(board());
        if (_myPiece == WHITE) {
            findMove(b, maxDepth(b), true, 1, -INFTY, INFTY);
        } else {
            findMove(b, maxDepth(b), true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _lastFoundMove iff SAVEMOVE. The move
     * should have maximal value or have value > BETA if SENSE==1,
     * and minimal value or value < ALPHA if SENSE==-1. Searches up to
     * DEPTH levels.  Searching at level 0 simply returns a static estimate
     * of the board value and does not set _lastMoveFound.
     */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         int alpha, int beta) {
        if (depth == 0 || board.winner() != null) {
            return staticScore(board);
        }


        Board myCopy = new Board();
        myCopy.copy(board);
        Board findMax = new Board();
        findMax.setValue(-INFTY);
        Board findMin = new Board();
        findMin.setValue(INFTY);
        Board nextBoard;
        if (sense == 1) {
            for (Move m : iterable(myCopy.legalMoves(WHITE))) {
                myCopy.makeMove(m);
                nextBoard = new Board();
                nextBoard.copy(myCopy);
                myCopy.undo();
                int performance = findMove(nextBoard, depth - 1,
                        false, -1, alpha, beta);
                if (performance >= findMax.getValue()) {
                    findMax = nextBoard;
                    nextBoard.setValue(performance);
                    alpha = max(alpha, performance);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            if (saveMove) {
                _lastFoundMove = findMax.getMyMoves().pop();
            }
            return findMax.getValue();
        } else {
            for (Move m : iterable(myCopy.legalMoves(BLACK))) {
                myCopy.makeMove(m);
                nextBoard = new Board();
                nextBoard.copy(myCopy);
                myCopy.undo();
                int performance = findMove(nextBoard, depth - 1,
                        false, 1, alpha, beta);
                if (performance <= findMin.getValue()) {
                    findMin = nextBoard;
                    nextBoard.setValue(performance);
                    beta = min(beta, performance);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            if (saveMove) {
                _lastFoundMove = findMin.getMyMoves().pop();
            }
            return findMin.getValue();

        }
    }


    /**
     * Return a heuristically determined maximum search depth
     * based on characteristics of BOARD.
     */
    private int maxDepth(Board board) {
        int N = board.numMoves();
        return (N + DIVID) / DIVID;

    }


    /**
     * Return a heuristic value for BOARD.
     */
    private int staticScore(Board board) {


        Piece winner = board.winner();
        if (winner == BLACK) {
            return -WINNING_VALUE;
        } else if (winner == WHITE) {
            return WINNING_VALUE;
        } else {
            Board myBoard = new Board();
            myBoard.copy(board);
            List<Square> myPosWhite = new ArrayList<>();
            List<Square> myPosBlack = new ArrayList<>();
            for (int i = Integer.parseInt("0");
                 i <= Integer.parseInt("99"); i++) {
                if (myBoard.get(Square.sq(i)).equals(WHITE)) {
                    myPosWhite.add(Square.sq(i));
                }
                if (myBoard.get(Square.sq(i)).equals(BLACK)) {
                    myPosBlack.add(Square.sq(i));
                }
            }

            int positivePoint = 0;
            int negativePoint = 0;


            for (int i = 0; i < myPosWhite.size(); i++) {
                Iterator<Square> positive = myBoard.reachableFrom
                        (myPosWhite.get(i), myPosWhite.get(i));
                while (positive.hasNext()) {
                    positivePoint += 1;
                    positive.next();
                }
            }

            for (int i = 0; i < myPosBlack.size(); i++) {
                Iterator<Square> negative = myBoard.reachableFrom
                        (myPosBlack.get(i), myPosBlack.get(i));

                while (negative.hasNext()) {
                    negativePoint -= 1;
                    negative.next();
                }
            }


            return positivePoint + negativePoint;


        }

    }
}


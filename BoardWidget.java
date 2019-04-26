package amazons;

import ucb.gui2.Pad;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import static amazons.Piece.BLACK;
import static amazons.Piece.SPEAR;
import static amazons.Piece.WHITE;
import static amazons.Square.sq;

/**
 * A widget that displays an Amazons game.
 *
 * @author Shichao Han
 */
class BoardWidget extends Pad {

    /* Parameters controlling sizes, speeds, colors, and fonts. */

    /**
     * Colors of empty squares and grid lines.
     */
    static final Color
            SPEAR_COLOR = new Color(64, 64, 64),
            LIGHT_SQUARE_COLOR = new Color(238, 207, 161),
            DARK_SQUARE_COLOR = new Color(205, 133, 63);

    /**
     * Locations of images of white and black queens.
     */
    private static final String
            WHITE_QUEEN_IMAGE = "wq4.png",
            BLACK_QUEEN_IMAGE = "bq4.png",
            SPEAR_IMAGE = "spr.png";

    /**
     * Size parameters.
     */
    private static final int
            SQUARE_SIDE = 30,
            BOARD_SIDE = SQUARE_SIDE * 10;
    /**
     * Board being displayed.
     */
    private final Board _board = new Board();
    /**
     * Queue on which to post move commands (from mouse clicks).
     */
    private ArrayBlockingQueue<String> _commands;
    /**
     * Image of white queen.
     */
    private BufferedImage _whiteQueen;
    /**
     * Image of black queen.
     */
    private BufferedImage _blackQueen;
    /**
     * Image of spear.
     */
    private BufferedImage _spearImage;
    /**
     * True iff accepting moves from user.
     */
    private boolean _acceptingMoves;

    /**
     * A graphical representation of an Amazons board that sends commands
     * derived from mouse clicks to COMMANDS.
     */
    BoardWidget(ArrayBlockingQueue<String> commands) {
        _commands = commands;
        setMouseHandler("click", this::mouseClicked);
        setPreferredSize(BOARD_SIDE, BOARD_SIDE);

        try {
            _whiteQueen = ImageIO.read(Utils.getResource(WHITE_QUEEN_IMAGE));
            _blackQueen = ImageIO.read(Utils.getResource(BLACK_QUEEN_IMAGE));
            _spearImage = ImageIO.read(Utils.getResource(SPEAR_IMAGE));
        } catch (IOException excp) {
            System.err.println("Could not read queen images.");
            System.exit(1);
        }
        _acceptingMoves = false;
    }

    /**
     * Draw the bare board G.
     */
    private void drawGrid(Graphics2D g) {
        g.setColor(DARK_SQUARE_COLOR);
        for (int i = 0; i < (BOARD_SIDE / SQUARE_SIDE); i += 1) {
            for (int j = 0; j < (BOARD_SIDE / SQUARE_SIDE); j += 1) {
                if (i % 2 == 0) {
                    if (j % 2 != 0) {
                        g.fillRect(i * SQUARE_SIDE, j * SQUARE_SIDE,
                                SQUARE_SIDE, SQUARE_SIDE);
                    }
                } else {
                    if (j % 2 == 0) {
                        g.fillRect(i * SQUARE_SIDE, j * SQUARE_SIDE,
                                SQUARE_SIDE, SQUARE_SIDE);
                    }
                }
            }
        }
    }


    @Override
    public synchronized void paintComponent(Graphics2D g) {
        g.setColor(LIGHT_SQUARE_COLOR);
        g.fillRect(0, 0, BOARD_SIDE, BOARD_SIDE);
        drawGrid(g);
        for (int i = 0; i < 100; i++) {
            if (_board.get(Square.sq(i)).equals(BLACK)
                    || _board.get(Square.sq(i)).equals(WHITE)) {
                drawQueen(g, Square.sq(i), _board.get(Square.sq(i)));
            }
            if (_board.get(Square.sq(i)).equals(SPEAR)) {
                drawSpear(g, Square.sq(i));
            }
        }

    }

    /** Draw a spear at the input square.
     * @param g the graph
     * @param spearsq the square of the spear.
     * */
    private void drawSpear(Graphics2D g, Square spearsq) {
        g.drawImage(_spearImage, cx(spearsq.col()) + 2,
                cy(spearsq.row()) + 4, null);
    }


    /**
     * Draw a queen for side PIECE at square S on G.
     */
    private void drawQueen(Graphics2D g, Square s, Piece piece) {
        g.drawImage(piece == WHITE ? _whiteQueen : _blackQueen,
                cx(s.col()) + 2, cy(s.row()) + 4, null);
    }

    /**
     * Handle a click on S.
     */
    private void click(Square s) {
        int indicator = _countClick % 3;
        if (indicator == 0) {
            moveFrom = s;
            _countClick += 1;
            System.out.println("Click on from");
        } else if (indicator == 1) {
            moveTo = s;
            _countClick += 1;
            System.out.println("Click on to");
        } else {
            moveSpear = s;
            System.out.println("Click on spear");

            Move myMove = Move.mv(moveFrom, moveTo, moveSpear);
            if (_board.isLegal(myMove)) {
                System.out.println(moveTo + " move 2");
                _commands.add(myMove.toString());
            }
            _countClick = 0;
            System.out.println(_board);
            System.out.println(myMove);
        }

        repaint();
    }

    /**
     * Handle mouse click event E.
     */
    private synchronized void mouseClicked(String unused, MouseEvent e) {
        int xpos = e.getX(), ypos = e.getY();
        int x = xpos / SQUARE_SIDE,
                y = (BOARD_SIDE - ypos) / SQUARE_SIDE;
        if (_acceptingMoves
                && x >= 0 && x < Board.SIZE && y >= 0 && y < Board.SIZE) {
            click(sq(x, y));
        }
    }

    /**
     * Revise the displayed board according to BOARD.
     */
    synchronized void update(Board board) {
        _board.copy(board);
        repaint();
    }

    /**
     * Turn on move collection iff COLLECTING, and clear any current
     * partial selection.   When move collection is off, ignore clicks on
     * the board.
     */
    void setMoveCollection(boolean collecting) {
        _acceptingMoves = collecting;
        repaint();
    }

    /**
     * Return x-pixel coordinate of the left corners of column X
     * relative to the upper-left corner of the board.
     */
    private int cx(int x) {
        return x * SQUARE_SIDE;
    }

    /**
     * Return y-pixel coordinate of the upper corners of row Y
     * relative to the upper-left corner of the board.
     */
    private int cy(int y) {
        return (Board.SIZE - y - 1) * SQUARE_SIDE;
    }

    /**
     * Return x-pixel coordinate of the left corner of S
     * relative to the upper-left corner of the board.
     */
    private int cx(Square s) {
        return cx(s.col());
    }

    /**
     * Return y-pixel coordinate of the upper corner of S
     * relative to the upper-left corner of the board.
     */
    private int cy(Square s) {
        return cy(s.row());
    }

    /**
     * Count the click to help remember the moves.
     */
    private int _countClick = 0;

    /**
     * Storage the move's from square.
     */
    private Square moveFrom;

    /**
     * Storage the move's to square.
     */
    private Square moveTo;

    /**
     * Storage the move's spear square.
     */
    private Square moveSpear;
}

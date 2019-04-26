package amazons;

import java.util.regex.Pattern;

/**
 * A Player that takes input from a GUI.
 *
 * @author Shichao Han
 */
class GUIPlayer extends Player implements Reporter {

    /**
     * The GUI I use for input.
     */
    private GUI _gui;

    /**
     * A new GUIPlayer that takes moves and commands from GUI.
     */
    GUIPlayer(GUI gui) {
        this(null, null, gui);
    }

    /**
     * A new GUIPlayer playing PIECE under control of CONTROLLER, taking
     * moves and commands from GUI.
     */
    GUIPlayer(Piece piece, Controller controller, GUI gui) {
        super(piece, controller);
        _gui = gui;
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new GUIPlayer(piece, controller, _gui);
    }

    @Override
    String myMove() {
        while (true) {
            String line = _gui.readCommand();
            if (line == null) {
                return "quit";
            } else if (Pattern.matches(line, "\\s*[a-j]")
                    && !(Pattern.matches(line,
                    "([a-j]([1-9]|10)\\-[a-j]"
                            + "([1-9]|10)\\([a-j]([1-9]|10)\\))$"))) {
                _controller.reportError("Invalid move. "
                        + "Please try again.");
                continue;
            }
            if (Pattern.matches("seed\\s+(\\d+)$", line)
                    || Pattern.matches("dump$", line)
                    || Pattern.matches("new$", line)
                    || Pattern.matches("auto\\s*[wW][hH][iI][tT][eE]$", line)
                    || Pattern.matches("auto\\s*[bB][lL][aA][cC][kK]$", line)
                    || Pattern.matches("quit$", line)
                    || Pattern.matches("manual\\s*[wW][hH][iI][tT][eE]$", line)
                    || Pattern.matches("manual\\s*[bB][lL][aA][cC][kK]$", line)
                    || Pattern.matches("[a-j]([1-9]|10)\\s+[a-j]"
                    + "([1-9]|10)\\s+[a-j]([1-9]|10)$", line)
                    || Pattern.matches("([a-j]([1-9]|10)-[a-j]"
                    + "([1-9]|10)\\([a-j]([1-9]|10)\\))$", line)
                    || Pattern.matches("undo$", line)
            ) {
                return line;
            } else {
                _controller.reportError("Invalid move. "
                        + "Please try again.");
                continue;
            }
        }

    }

    @Override
    public void reportError(String fmt, Object... args) {
        _gui.reportError(fmt, args);
    }

    @Override
    public void reportNote(String fmt, Object... args) {
        _gui.reportNote(fmt, args);
    }

    @Override
    public void reportMove(Move unused) {
    }
}

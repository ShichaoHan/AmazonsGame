package amazons;

import java.util.regex.Pattern;

/**
 * A Player that takes input as text commands from the standard input.
 *
 * @author Shichao Han
 */
class TextPlayer extends Player {

    /**
     * A new TextPlayer with no piece or controller (intended to produce
     * a template).
     */
    TextPlayer() {
        this(null, null);
    }

    /**
     * A new TextPlayer playing PIECE under control of CONTROLLER.
     */
    private TextPlayer(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new TextPlayer(piece, controller);
    }

    @Override
    String myMove() {
        while (true) {
            String line = _controller.readLine();
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
}


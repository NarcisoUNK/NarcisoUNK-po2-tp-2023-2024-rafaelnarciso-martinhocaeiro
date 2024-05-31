package pt.ipbeja.app.model;

/**
 * Position in the board.
 * Represents a position with a line and column.
 *
 * @version 31/05/2024 (Final)
 * @authors Martinho Caeiro (23917) and Rafael Narciso (24473)
 */
public record Position(int line, int col) {

    /**
     * Returns a string representation of the position.
     *
     * @return the line and column as a string
     */
    @Override
    public String toString() {
        return line + ", " + col;
    }
}

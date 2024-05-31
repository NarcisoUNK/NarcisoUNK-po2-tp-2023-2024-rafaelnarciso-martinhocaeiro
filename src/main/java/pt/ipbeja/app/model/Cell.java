package pt.ipbeja.app.model;

/**
 * Cell class.
 * Represents a cell with an associated letter and bonus.
 *
 * @version 31/05/2024 (Final)
 * @authors Martinho Caeiro (23917) and Rafael Narciso (24473)
 */
public class Cell {
    private final char letter;

    /**
     * Constructor for Cell.
     * Initializes the cell with the given letter.
     *
     * @param letter the letter in the cell
     */
    public Cell(char letter) {
        this.letter = letter;
    }

    /**
     * Returns the letter in the cell.
     *
     * @return the letter
     */
    public char getLetter() {
        return letter;
    }

    /**
     * Returns the bonus of the cell.
     * By default, cells have no bonus, so it returns 0.
     *
     * @return 0 indicating no bonus
     */
    public int getBonus() {
        return 0;
    }
}

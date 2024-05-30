package pt.ipbeja.app.model;

/**
 * RegularCell class.
 * Represents a regular cell without a bonus.
 *
 * @version 30/05/2024
 * @authors Martinho Caeiro (23917) and Rafael Narciso (24473)
 */
public class RegularCell extends Cell {

    /**
     * Constructor for RegularCell.
     * Initializes the cell with the given letter.
     *
     * @param letter the letter in the cell
     */
    public RegularCell(char letter) {
        super(letter);
    }

    /**
     * Returns the bonus of the cell.
     * Regular cells have no bonus, so it returns 0.
     *
     * @return 0 indicating no bonus
     */
    @Override
    public int getBonus() {
        return 0; // No bonus
    }
}

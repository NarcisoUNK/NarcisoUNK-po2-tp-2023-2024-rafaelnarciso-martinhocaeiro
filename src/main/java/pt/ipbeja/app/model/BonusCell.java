package pt.ipbeja.app.model;

/**
 * BonusCell class.
 * Represents a cell with a bonus.
 *
 * @version 30/05/2024
 * @authors Martinho Caeiro (23917) and Rafael Narciso (24473)
 */
public class BonusCell extends Cell {

    private final int bonus;

    /**
     * Constructor for BonusCell.
     * Initializes the cell with the given letter and bonus.
     *
     * @param letter the letter in the cell
     * @param bonus the bonus value for the cell
     */
    public BonusCell(char letter, int bonus) {
        super(letter);
        this.bonus = bonus;
    }

    /**
     * Returns the bonus of the cell.
     *
     * @return the bonus value
     */
    @Override
    public int getBonus() {
        return bonus;
    }
}

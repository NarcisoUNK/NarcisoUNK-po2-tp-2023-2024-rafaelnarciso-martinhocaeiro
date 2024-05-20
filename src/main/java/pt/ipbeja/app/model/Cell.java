package pt.ipbeja.app.model;

/**
 * Cell in the board
 * Contains a letter, a boolean indicating if the cell is part of a word,
 * and a bonus associated with the cell.
 */
public class Cell {
    private final char letter;
    private int bonus = 0;

    public Cell(char letter) {
        this.letter = letter;
        this.bonus = bonus;
    }

    public char getLetter() {
        return letter;
    }

    public int getBonus() {
        return bonus;
    }
}

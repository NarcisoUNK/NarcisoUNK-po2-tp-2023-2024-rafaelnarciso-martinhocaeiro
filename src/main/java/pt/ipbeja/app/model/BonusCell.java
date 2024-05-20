package pt.ipbeja.app.model;

public class BonusCell extends Cell {
    private final int bonus;

    public BonusCell(char letter, int bonus) {
        super(letter);
        this.bonus = bonus;
    }

    @Override
    public int getBonus() {
        return bonus;
    }
}

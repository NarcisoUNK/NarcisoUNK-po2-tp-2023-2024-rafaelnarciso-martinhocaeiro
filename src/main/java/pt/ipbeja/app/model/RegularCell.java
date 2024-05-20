package pt.ipbeja.app.model;

public class RegularCell extends Cell {

    public RegularCell(char letter) {
        super(letter);
    }

    @Override
    public int getBonus() {
        return 0; // Sem bônus
    }
}
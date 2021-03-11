package ca.landonjw.gooeylibs.commands.shooter;

public class ShooterProjectile {

    private int row = 6;
    private final int col;

    public ShooterProjectile(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void move() {
        row--;
    }

}

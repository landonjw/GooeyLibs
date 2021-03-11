package ca.landonjw.gooeylibs.commands.shooter;

import net.minecraft.item.Item;

public abstract class ShooterEnemy {

    private int health;
    private int row = 0;
    private final int col;

    public ShooterEnemy(int health, int col) {
        this.health = health;
        this.col = col;
    }

    public abstract Item getDisplay();

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void move() {
        row++;
    }

    public int getHealth() {
        return health;
    }

    public void damage() {
        this.health--;
    }

}
package ca.landonjw.gooeylibs.commands.shooter.enemies;

import ca.landonjw.gooeylibs.commands.shooter.ShooterEnemy;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class MediumAsteroid extends ShooterEnemy {

    public MediumAsteroid(int col) {
        super(2, col);
    }

    @Override
    public Item getDisplay() {
        return Items.COAL;
    }

}

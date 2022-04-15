package ca.landonjw.gooeylibs2.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Player.class)
public class PlayerMixin {
    @Shadow public AbstractContainerMenu containerMenu;
}

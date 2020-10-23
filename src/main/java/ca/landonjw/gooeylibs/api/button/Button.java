package ca.landonjw.gooeylibs.api.button;

import ca.landonjw.gooeylibs.api.data.Subject;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface Button extends Subject<Button> {

    ItemStack getDisplay();

    void onClick(@Nonnull ButtonAction action);

}

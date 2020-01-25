package ca.landonjw.gooeylibs.inventory.api;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;

import javax.annotation.Nonnull;

/**
 * Represents an action that is being done to a {@link Button}.
 *
 * This will be created on {@link Button#onClick(EntityPlayerMP, ClickType)}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class ButtonAction {

    /** The button the action is taking place on. */
    private final Button button;
    /** The player causing the action. */
    private final EntityPlayerMP player;
    /** The type of click the player used. */
    private final ClickType clickType;

    /**
     * Constructor for the button action.
     *
     * @param button    the button the action is taking place on
     * @param player    the player causing the action
     * @param clickType the type of click the player used
     */
    public ButtonAction(@Nonnull Button button,
                        @Nonnull EntityPlayerMP player,
                        @Nonnull ClickType clickType){
        this.button = button;
        this.player = player;
        this.clickType = clickType;
    }

    /**
     * Gets the button the action is taking place on.
     *
     * @return the button the action is taking place on
     */
    public Button getButton() {
        return button;
    }

    /**
     * Gets the player causing the action.
     *
     * @return the player causing the action
     */
    public EntityPlayerMP getPlayer() {
        return player;
    }

    /**
     * Gets the type of click the player used
     *
     * @return the type of click the player used
     */
    public ClickType getClickType() {
        return clickType;
    }

}
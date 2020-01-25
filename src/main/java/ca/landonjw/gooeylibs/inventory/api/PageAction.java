package ca.landonjw.gooeylibs.inventory.api;

import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnull;

/**
 * Represents an action that is being done to a {@link Page}.
 *
 * This will be created on {@link Page#onOpen(EntityPlayerMP)} and {@link Page#onClose(EntityPlayerMP)}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class PageAction {

    /** The page the action is taking place on. */
    private final Page page;
    /** The player causing the action. */
    private final EntityPlayerMP player;
    /** The type of action taking place. */
    private final Type type;

    /**
     * Constructor for the page action.
     *
     * @param page   the page the action is taking place on
     * @param player the player causing the action
     * @param type   the type of action taking place
     */
    public PageAction(@Nonnull Page page,
                      @Nonnull EntityPlayerMP player,
                      @Nonnull Type type){
        this.page = page;
        this.player = player;
        this.type = type;
    }

    /**
     * Gets the page the action is taking place on.
     *
     * @return the page the action is taking place on
     */
    public Page getPage() {
        return page;
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
     * Gets the type of action taking place.
     *
     * @return the type of action taking place
     */
    public Type getType() {
        return type;
    }

    /**
     * Represents the types of actions that can take place on a {@link Page}.
     */
    public enum Type {
        /** Occurs when a page is being opened. */
        Open,
        /** Occurs when a page is being closed. */
        Close
    }

}

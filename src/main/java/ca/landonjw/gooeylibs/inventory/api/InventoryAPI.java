package ca.landonjw.gooeylibs.inventory.api;

import ca.landonjw.gooeylibs.inventory.implementation.BaseInventoryAPI;
import ca.landonjw.gooeylibs.inventory.internal.FuturePageListener;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;

/**
 * Inventory API designed for creating chest UIs in the Forge environment.
 *
 * <p>This API is an adaptation of Waterdude's own inventory API 'AquaAPI'. You can find
 * his version <a href="https://gitlab.com/Waterdude/aquaapi">here</a>.
 *
 * This API is has been designed to be builder oriented and separates the behaviour of
 * buttons from the {@link Page} to allow the buttons to have their own separate behaviour
 * and be used in multiple positions and/or pages. This API also includes many utilities within
 * the template builder to make it easier to design page layouts.</p>
 *
 * @author landonjw
 * @since  1.0.0
 */
public interface InventoryAPI {

    /**
     * Constructs a new {@link Button.Builder}.
     *
     * @return a new button builder
     */
    Button.Builder buttonBuilder();

    /**
     * Constructs a new {@link Template.Builder}.
     *
     * @param rows the number of rows in the template
     * @return a new template builder
     * @throws IllegalArgumentException if rows is below or equal to 0
     */
    Template.Builder templateBuilder(int rows);

    /**
     * Constructs a new {@link Page.Builder}.
     *
     * @return a new page builder
     */
    Page.Builder pageBuilder();

    /**
     * Closes the player's inventory.
     * If player is null, does nothing.
     *
     * @param player the player to close inventory for
     */
    void closePlayerInventory(@Nullable EntityPlayerMP player);

    /**
     * Registers the API.
     * Should be called during the FMLPreInitializationEvent
     */
    static void register(){
        MinecraftForge.EVENT_BUS.register(new FuturePageListener());
    }

    /**
     * Gets the instance of the API.
     *
     * @return the instance of the API
     */
    static InventoryAPI getInstance(){
        return BaseInventoryAPI.getInstance();
    }

}
package ca.landonjw.gooeylibs;

import ca.landonjw.gooeylibs.inventory.api.InventoryAPI;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * <h1>GooeyLibs</h1>
 *
 * The goal of GooeyLibs is to aid in the creation of user interfaces in the Forge environment.
 *
 * <p>As it stands, there is an inventory API that is inspired by Waterdude's AquaAPI.
 * His API can be found <a href="https://gitlab.com/Waterdude/aquaapi">here</a>. My API is intended
 * to be builder oriented for easy construction of complex pages without the need to create separate classes,
 * and attempts to restructure his page's and button's in order to separate logic and allow buttons to be reusable
 * across several locations and pages.</p>
 *
 * <p>There are additional plans to incorporate a text API in the future to assist in making interactive text
 * in Forge. If you have any questions or issues regarding this library, you can contact me at my development
 * discord channel <a href="https://discord.gg/fsNq4Jz">here</a>.</p>
 *
 * @author landonjw
 * @since  1.0.0
 */
@Mod(
        modid = GooeyLibs.MOD_ID,
        name = GooeyLibs.MOD_NAME,
        version = GooeyLibs.VERSION,
        acceptableRemoteVersions = "*",
        serverSideOnly = true
)
public class GooeyLibs {

    /** The mod ID of the library. */
    public static final String MOD_ID = "gooeylibs";
    /** The mod name of the library. */
    public static final String MOD_NAME = "GooeyLibs";
    /** The version of the library. */
    public static final String VERSION = "1.0.6";

    /**
     * Registers the inventory API.
     *
     * @param event the event called during mod startup
     */
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        InventoryAPI.register();
    }

}
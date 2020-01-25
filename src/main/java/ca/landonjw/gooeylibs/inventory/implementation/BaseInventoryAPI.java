package ca.landonjw.gooeylibs.inventory.implementation;

import ca.landonjw.gooeylibs.inventory.api.Button;
import ca.landonjw.gooeylibs.inventory.api.InventoryAPI;
import ca.landonjw.gooeylibs.inventory.api.Page;
import ca.landonjw.gooeylibs.inventory.api.Template;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;

/**
 * Base implementation of {@link InventoryAPI}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class BaseInventoryAPI implements InventoryAPI {

    /** The instance of the inventory API. */
    private static InventoryAPI instance;

    /** Constructor for the inventory API. Private so it cannot be constructed outside of {@link #getInstance()}. */
    private BaseInventoryAPI(){}

    /** {@inheritDoc} */
    @Override
    public Button.Builder buttonBuilder() {
        return new BaseButton.Builder();
    }

    /** {@inheritDoc} */
    @Override
    public Template.Builder templateBuilder(int rows) {
        return new BaseTemplate.Builder(rows);
    }

    /** {@inheritDoc} */
    @Override
    public Page.Builder pageBuilder() {
        return new BasePage.Builder();
    }

    /** {@inheritDoc} */
    @Override
    public void closePlayerInventory(@Nullable EntityPlayerMP player){
        if(player != null){
            CPacketCloseWindow pclient = new CPacketCloseWindow();
            ObfuscationReflectionHelper.setPrivateValue(
                    CPacketCloseWindow.class,
                    pclient,
                    player.openContainer.windowId,
                    0
            );
            SPacketCloseWindow pserver = new SPacketCloseWindow(player.openContainer.windowId);
            player.connection.processCloseWindow(pclient);
            player.connection.sendPacket(pserver);
        }
    }

    /**
     * Gets the instance of the inventory API.
     * If the instance has not been created yet, this will create it.
     *
     * @return the instance of the inventory API
     */
    public static InventoryAPI getInstance(){
        if(instance == null){
            instance = new BaseInventoryAPI();
        }
        return instance;
    }

}

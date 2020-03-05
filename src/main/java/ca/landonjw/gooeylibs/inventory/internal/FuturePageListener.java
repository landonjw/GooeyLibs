package ca.landonjw.gooeylibs.inventory.internal;

import ca.landonjw.gooeylibs.inventory.api.Page;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Listens for server ticks and evaluates if a {@link Page} is ready to be opened for players in {@link #futurePages}.
 *
 * <p>Pages are added to {@link #futurePages} when {@link Page#openPage(EntityPlayerMP)} is invoked and
 * a player is currently in another inventory, and this will open the page when the player leaves their
 * currently opened inventory.</p>
 *
 * @author landonjw
 * @since  1.0.0
 */
public class FuturePageListener {

    /** Pages to be opened in the future, when a player leaves their current inventory. */
    private static Map<UUID, Page> futurePages = new HashMap<>();

    /**
     * Opens a page for a player if they are added to {@link #futurePages} and
     * they do not have an inventory open.
     *
     * @param event the event called when the server ticks
     */
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event){
        Iterator<Map.Entry<UUID, Page>> futurePagesIterator = futurePages.entrySet().iterator();

        while(futurePagesIterator.hasNext()){
            Map.Entry<UUID, Page> entry = futurePagesIterator.next();
            UUID playerUUID = entry.getKey();
            Page page = entry.getValue();

            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            EntityPlayerMP player = server.getPlayerList().getPlayerByUUID(playerUUID);

            //If the player's open containiner is their inventory, force open the page.
            if(player != null && player.openContainer == player.inventoryContainer){
                CPacketCloseWindow pclient = new CPacketCloseWindow();
                ObfuscationReflectionHelper.setPrivateValue(CPacketCloseWindow.class, pclient, player.openContainer.windowId, 0);
                SPacketCloseWindow pserver = new SPacketCloseWindow(player.openContainer.windowId);
                player.connection.processCloseWindow(pclient);
                player.connection.sendPacket(pserver);
                new Waiter(() -> page.forceOpenPage(player), 1);
                //page.forceOpenPage(player);
                futurePagesIterator.remove();
            }
        }
    }

    /**
     * Adds a page to be opened by a player when they do not have an inventory open.
     *
     * @param playerUUID the uuid of the player to add a future page for
     * @param page       the page to add
     */
    public static void addFuturePage(UUID playerUUID, Page page){
        futurePages.put(playerUUID, page);
    }

}

package ca.landonjw.gooeylibs2.api.page;

import ca.landonjw.gooeylibs2.api.data.Subject;
import ca.landonjw.gooeylibs2.api.template.Template;
import ca.landonjw.gooeylibs2.api.template.types.InventoryTemplate;
import ca.landonjw.gooeylibs2.implementation.GooeyContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface Page extends Subject<Page>, MenuProvider {

    Template getTemplate();

    default Optional<InventoryTemplate> getInventoryTemplate() {
        return Optional.empty();
    }

    net.minecraft.network.chat.Component getTitle();

    public static void open(ServerPlayer player, Page page) {
        player.openMenu(page);
    }

    @Override
    default Component getDisplayName() {
        return getTitle();
    }

    @Nullable
    @Override
    default AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new GooeyContainer((ServerPlayer) pPlayer, this);
    }

    default void onOpen(@Nonnull PageAction action) {
    }

    default void onClose(@Nonnull PageAction action) {
    }

}
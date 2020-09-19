package ca.landonjw.gooeylibs.inventory.internal;

import ca.landonjw.gooeylibs.inventory.api.Button;
import ca.landonjw.gooeylibs.inventory.api.Page;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Represents a container for a user interface that is used to display a {@link Page}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class UIContainer extends Container {

    /** The inventory to display. */
    private UIInventory inventory;
    /** The page being displayed. */
    private Page page;

    /**
     * Constructor for the container.
     *
     * @param inventory the inventory for the container
     */
    public UIContainer(UIInventory inventory){
        this.inventory = inventory;
        this.page = inventory.getPage();
        IInventory playerInventory = inventory.getPlayer().inventory;

        int numRows = inventory.getPage().getTemplate().getRows();

        //Set slots in inventory for user interface
        for (int row = 0; row < numRows; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(inventory, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }

        //Set slots in inventory for player's invneotry and hotbar
        int yAxisOffset = (numRows - 4) * 18;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 103 + row * 18 + yAxisOffset));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 161 + yAxisOffset));
        }

        windowId = 1;
    }

    /**
     * Checks if the container can be interacted with. Always returns true.
     *
     * @param playerIn the player to check
     * @return true
     */
    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    /**
     * Reopens the page when an item stack transfer is attempted with shift click.
     *
     * @param player not used
     * @param index  not used
     * @return an empty itemstack
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        page.forceOpenPage(inventory.getPlayer());
        return ItemStack.EMPTY;
    }

    /**
     * Reopens the page when a slot is clicked and if a button is clicked, invokes it's behavior.
     *
     * @param slot        the slot being clicked
     * @param dragType    not used
     * @param clickTypeIn the type of click being usd
     * @param player      the player clicking the slot
     * @return an empty itemstack
     */
    @Override
    public ItemStack slotClick(int slot, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        page.forceOpenPage(inventory.getPlayer());
        if(slot >= 0 && slot < page.getTemplate().getRows() * 9){
            Button button = page.getTemplate().getButtons()[slot / 9][slot % 9];
            if(button != null){
                button.onClick(inventory.getPlayer(), clickTypeIn, page);
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Reopens the page when an item stack merge is attempted.
     *
     * @param stack     not used
     * @param start     not used
     * @param end       not used
     * @param backwards not used
     * @return false
     */
    @Override
    protected boolean mergeItemStack(ItemStack stack, int start, int end, boolean backwards) {
        page.forceOpenPage(inventory.getPlayer());
        return false;
    }

    /**
     * Checks if a slot can be merged with. Always returns false.
     *
     * @param stack  not used
     * @param slotIn not used
     * @return false
     */
    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return false;
    }

    /**
     * Closes the inventory and invokes the attached page's close behavior.
     *
     * @param playerIn player closing the container
     */
    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        if(playerIn instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) playerIn;
            super.onContainerClosed(playerIn);
            this.inventory.closeInventory(playerIn);
            player.sendAllWindowProperties(player.inventoryContainer, player.inventory);
            player.sendContainerToPlayer(player.inventoryContainer);
            this.page.onClose(player);
        }
    }

    /**
     * Does nothing, since a stack shouldn't be placeable.
     *
     * @param slotID not used
     * @param stack  not used
     */
    @Override
    public void putStackInSlot(int slotID, ItemStack stack) {}

    /**
     * Checks if a clot can be dragged into. Always returns false.
     *
     * @param slotIn not used
     * @return false
     */
    @Override
    public boolean canDragIntoSlot(Slot slotIn) {
        return false;
    }

}

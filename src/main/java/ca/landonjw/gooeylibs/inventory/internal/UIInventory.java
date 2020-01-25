package ca.landonjw.gooeylibs.inventory.internal;

import ca.landonjw.gooeylibs.inventory.api.Page;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

/**
 * Represents an inventory for a user interface is used to display a {@link Page}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class UIInventory implements IInventory {

    /** The page being the inventory is created from. */
    private Page page;
    /** The player the inventory is for. */
    private EntityPlayerMP player;
    /** The item stacks in the inventory. */
    private NonNullList<ItemStack> inventory;

    /**
     * Constructor for the inventory.
     *
     * @param page   the page being displayed
     * @param player the player the ui inventory is being made for
     */
    public UIInventory(Page page, EntityPlayerMP player) {
        this.inventory = NonNullList.withSize(page.getTemplate().getRows() * 9, ItemStack.EMPTY);
        this.page = page;
        this.player = player;
    }

    /**
     * Gets the page the inventory is created from.
     *
     * @return the page the inventory is created from
     */
    public Page getPage() {
        return page;
    }

    /**
     * Gets the player the inventory is for
     *
     * @return the player the inventory is for
     */
    public EntityPlayerMP getPlayer() {
        return player;
    }

    /**
     * Gets the contents of the inventory.
     *
     * @return the contents of the inventory
     */
    public NonNullList<ItemStack> getInventoryContents() {
        return inventory;
    }

    /**
     * Gets the size of the inventory.
     *
     * @return the size of the inventory in item slots
     */
    @Override
    public int getSizeInventory() {
        return page.getTemplate().getRows() * 9;
    }

    /**
     * Checks if the inventory is empty. Always returns false.
     *
     * @return false
     */
    @Override
    public boolean isEmpty() {
        return false;
    }

    /**
     * Gets the item stack in a slot
     *
     * @param index the slot index to get item stack from
     * @return the itemstack in the slot
     */
    @Override
    public ItemStack getStackInSlot(int index) {
        return this.inventory.get(index);
    }

    /**
     * Reopens the page if an item stack in the inventory attempts to decrement.
     *
     * @param index not used
     * @param count not used
     * @return an empty item stack
     */
    @Override
    public ItemStack decrStackSize(int index, int count) {
        page.forceOpenPage(player);
        return ItemStack.EMPTY;
    }

    /**
     * Reopens the page if an item stack in the inventory is attempts to be removed.
     *
     * @param index not used
     * @return an empty item stack
     */
    @Override
    public ItemStack removeStackFromSlot(int index) {
        page.forceOpenPage(player);
        return ItemStack.EMPTY;
    }

    /**
     * Reopens the page if an inventory slot attempts to be modified.
     *
     * @param index not used
     * @param stack not used
     */
    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        page.forceOpenPage(player);
    }

    /**
     * Gets the stack limit for the inventory. Always returns 64.
     *
     * @return 64
     */
    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    /** Does nothing, but is required for interface implementation. */
    @Override
    public void markDirty() {}

    /**
     * Always returns false.
     *
     * @param player not used
     * @return false
     */
    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return false;
    }

    /** Does nothing, but is required for interface implementation. */
    @Override
    public void openInventory(EntityPlayer player) {}

    /** Does nothing, but is required for interface implementation. */
    @Override
    public void closeInventory(EntityPlayer player) {}

    /**
     * Always returns false.
     *
     * @param index not used
     * @param stack not used
     * @return false
     */
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;
    }

    /**
     * Always returns 0.
     *
     * @param id not used
     * @return 0
     */
    @Override
    public int getField(int id) {
        return 0;
    }

    /** Does nothing, but is required for interface implementation. */
    @Override
    public void setField(int id, int value) {}

    /**
     * Always returns 0.
     *
     * @return 0
     */
    @Override
    public int getFieldCount() {
        return 0;
    }

    /** Does nothing, but is required for interface implementation. */
    @Override
    public void clear() {}

    /**
     * Gets the name of the inventory. The name will always be {@link Page#getTitle()}.
     *
     * @return {@link Page#getTitle()}
     */
    @Override
    public String getName() {
        return page.getTitle();
    }

    /**
     * Checks if the inventory always has a custom name. Always returns true.
     *
     * @return true
     */
    @Override
    public boolean hasCustomName() {
        return true;
    }

    /**
     * Gets the display name of the inventory. The name will always be a text version of {@link Page#getTitle()}.
     *
     * @return {@link Page#getTitle()} as text
     */
    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(page.getTitle());
    }

}

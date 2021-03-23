package ca.landonjw.gooeylibs.implementation;

import ca.landonjw.gooeylibs.api.button.Button;
import ca.landonjw.gooeylibs.api.button.ButtonAction;
import ca.landonjw.gooeylibs.api.button.ButtonClick;
import ca.landonjw.gooeylibs.api.button.moveable.Movable;
import ca.landonjw.gooeylibs.api.button.moveable.MovableButtonAction;
import ca.landonjw.gooeylibs.api.page.Page;
import ca.landonjw.gooeylibs.api.page.PageAction;
import ca.landonjw.gooeylibs.api.template.Template;
import ca.landonjw.gooeylibs.api.template.TemplateType;
import ca.landonjw.gooeylibs.api.template.slot.TemplateSlot;
import ca.landonjw.gooeylibs.api.template.types.InventoryTemplate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;

public class GooeyContainer extends Container {

    private static final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
    private final EntityPlayerMP player;

    private Page page;
    public InventoryTemplate inventoryTemplate;

    private long lastClickTick;
    private boolean closing;

    /*
     *  Keeps track of a movable button that is on the cursor.
     *  If there is not a button on the cursor, this is null.
     */
    private Button cursorButton;

    public GooeyContainer(@Nonnull EntityPlayerMP player, @Nonnull Page page) {
        this.player = player;
        this.windowId = 1;

        this.page = page;
        initializePage(page);
        subscribeToPage(page);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void subscribeToPage(Page page) {
        page.subscribe(this, (update) -> {
            initializePage(update);
            refreshContainer();
        });
    }

    private void initializePage(Page page) {
        this.inventorySlots.forEach((slot) -> {
            ((TemplateSlot) slot).unsubscribe(this);
        });
        if (inventoryTemplate != null) {
            for (int i = 0; i < inventoryTemplate.getSize(); i++) {
                inventoryTemplate.getSlot(i).unsubscribe(this);
            }
        }
        this.inventorySlots = page.getTemplate().getSlots();
        this.inventoryItemStacks = page.getTemplate().getDisplayStacks();
        this.inventoryTemplate = page.getInventoryTemplate().orElse(null);
        this.inventorySlots.forEach((slot) -> {
            ((TemplateSlot) slot).subscribe(this, () -> updateSlot(slot));
        });
        if (inventoryTemplate != null) {
            for (int i = 0; i < inventoryTemplate.getSize(); i++) {
                int index = i;
                int itemSlot = i + page.getTemplate().getSize();
                inventoryTemplate.getSlot(i).subscribe(this, () -> {
                    updateSlotStack(index, getItemAtSlot(itemSlot), true);
                });
            }
        }
    }

    private void updateSlotStack(int index, ItemStack stack, boolean playerInventory) {
        if (playerInventory) {
            SPacketSetSlot setSlot = new SPacketSetSlot(windowId, this.inventorySlots.size() + index, stack);
            player.connection.sendPacket(setSlot);
        } else {
            SPacketSetSlot setSlot = new SPacketSetSlot(windowId, index, stack);
            player.connection.sendPacket(setSlot);
        }
    }

    private int getTemplateIndex(int slotIndex) {
        if (isSlotInPlayerInventory(slotIndex)) {
            return slotIndex - this.inventorySlots.size();
        } else {
            return slotIndex;
        }
    }

    private Template getTemplateFromIndex(int slotIndex) {
        if (isSlotInPlayerInventory(slotIndex)) {
            return inventoryTemplate;
        } else {
            return page.getTemplate();
        }
    }

    private boolean isSlotInPlayerInventory(int slot) {
        int templateSize = page.getTemplate().getSize();
        return slot >= templateSize && slot - templateSize < player.inventoryContainer.inventorySlots.size();
    }

    private ItemStack getItemAtSlot(int slot) {
        if (slot == -999) {
            return ItemStack.EMPTY;
        } else if (isSlotInPlayerInventory(slot)) {
            int targetSlot = slot - page.getTemplate().getSize();
            if (inventoryTemplate != null) {
                return inventoryTemplate.getSlot(targetSlot).getStack();
            } else {
                //First 0-7 slots contain player's armor, hand slots, etc. So we offset for the inventory UI slots.
                int PLAYER_INVENTORY_SLOT_OFFSET = 9;
                return player.inventoryContainer.getSlot(targetSlot + PLAYER_INVENTORY_SLOT_OFFSET).getStack();
            }
        } else {
            return page.getTemplate().getSlot(slot).getStack();
        }
    }

    private Button getButton(int slot) {
        if (slot < 0) return null;

        //Check if it's player's inventory or UI slot
        if (slot >= page.getTemplate().getSize()) {

            int targetedPlayerSlotIndex = slot - page.getTemplate().getSize();

            if (inventoryTemplate != null) {
                return inventoryTemplate.getSlot(targetedPlayerSlotIndex).getButton().orElse(null);
            } else {
                return null;
            }
        } else {
            return page.getTemplate().getSlot(slot).getButton().orElse(null);
        }
    }

    private void updateSlot(@Nonnull Slot slot) {
        SPacketSetSlot setSlot = new SPacketSetSlot(windowId, slot.getSlotIndex(), slot.getStack());
        player.connection.sendPacket(setSlot);
    }

    public void open() {
        player.closeContainer();
        player.openContainer = this;
        player.currentWindowId = windowId;

        SPacketOpenWindow openWindow;
        if (page.getTemplate().getTemplateType() == TemplateType.CRAFTING_TABLE) {
            openWindow = new SPacketOpenWindow(
                    player.currentWindowId,
                    page.getTemplate().getTemplateType().getID(),
                    new TextComponentString(page.getTitle())
            );
        } else {
            openWindow = new SPacketOpenWindow(
                    player.currentWindowId,
                    page.getTemplate().getTemplateType().getID(),
                    new TextComponentString(page.getTitle()),
                    page.getTemplate().getSize()
            );
        }
        player.connection.sendPacket(openWindow);

        updateAllContainerContents();
        page.onOpen(new PageAction(player, page));
    }

    private void patchDesyncs(int slot, ClickType clickType) {
        if (clickType == ClickType.PICKUP || clickType == ClickType.CLONE || clickType == ClickType.THROW) {
            updateSlotStack(getTemplateIndex(slot), getItemAtSlot(slot), isSlotInPlayerInventory(slot));
        } else if (clickType == ClickType.QUICK_MOVE || clickType == ClickType.PICKUP_ALL) {
            updateAllContainerContents();
        }
        lastClickTick = server.getTickCounter();
    }

    @Override
    public ItemStack slotClick(int slot, int dragType, ClickType clickType, EntityPlayer playerSP) {
        /*
         * These click types represent the user quickly picking up or moving items.
         * The click type proliferates and invokes slotClick for each stack that would be affected.
         * In order to prevent this method invoking logic every time, we track the last time
         * the click type was used. If a click of the same type has run on the same tick,
         * it will return out.
         */
        if (clickType == ClickType.QUICK_MOVE || clickType == ClickType.PICKUP_ALL || clickType == ClickType.PICKUP) {
            if (lastClickTick == server.getTickCounter()) {
                if (clickType == ClickType.PICKUP) {
                    return (cursorButton != null) ? cursorButton.getDisplay() : ItemStack.EMPTY;
                }
                return ItemStack.EMPTY;
            }
            lastClickTick = server.getTickCounter();
        }

        patchDesyncs(slot, clickType);

        Button clickedButton = getButton(slot);

        /*
         *  If the button being interacted with is moveable, or there is currently a moveable button on the cursor,
         *  send it to a separate handler.
         */
        if (clickedButton instanceof Movable || cursorButton != null) {
            return handleMoveableButton(slot, dragType, clickType);
        }

        /*
         * If this isn't here, clone clicks will result in the cursor thinking it has 64
         * of the stack when player is in creative.
         */
        if (clickType == ClickType.CLONE) {
            setPlayersCursor(ItemStack.EMPTY);
        }
        if (clickType == ClickType.QUICK_CRAFT) {
            updateSlotStack(getTemplateIndex(slot), ItemStack.EMPTY, isSlotInPlayerInventory(slot));
            return ItemStack.EMPTY;
        }

        ButtonClick buttonClickType = getButtonClickType(clickType, dragType);
        if (clickedButton != null) {
            ButtonAction action = new ButtonAction(player, buttonClickType, clickedButton, page.getTemplate(), page, slot);
            clickedButton.onClick(action);
        }
        return ItemStack.EMPTY;
    }

    private ButtonClick getButtonClickType(ClickType type, int dragType) {
        switch (type) {
            case PICKUP:
                return (dragType == 0) ? ButtonClick.LEFT_CLICK : ButtonClick.RIGHT_CLICK;
            case CLONE:
                return ButtonClick.MIDDLE_CLICK;
            case QUICK_MOVE:
                return (dragType == 0) ? ButtonClick.SHIFT_LEFT_CLICK : ButtonClick.SHIFT_RIGHT_CLICK;
            case THROW:
                return ButtonClick.THROW;
            default:
                return ButtonClick.OTHER;
        }
    }

    private ItemStack handleMoveableButton(int slot, int dragType, ClickType clickType) {
        /*
         * This prevents a desync with dragging an item.
         * Quick crafts begin and end with a click on slot -999,
         * we want to ignore those calls.
         */
        if (clickType == ClickType.QUICK_CRAFT && slot == -999) {
            return ItemStack.EMPTY;
        }

        Template template = getTemplateFromIndex(slot);
        int targetTemplateSlot = getTemplateIndex(slot);

        if (template == null) {
            if (clickType == ClickType.PICKUP && isSlotOccupied(slot)) {
                setPlayersCursor((cursorButton != null) ? cursorButton.getDisplay() : ItemStack.EMPTY);
                return getItemAtSlot(slot);
            }
            if (clickType == ClickType.QUICK_CRAFT) {
                updateSlotStack(getTemplateIndex(slot), getItemAtSlot(slot), true);
            }
            if (cursorButton == null) {
                return ItemStack.EMPTY;
            } else {
                setPlayersCursor(cursorButton.getDisplay());
                return ItemStack.EMPTY;
            }
        } else {
            Button clickedButton = getButton(slot);

            if (cursorButton == null) {
                if (slot == -999) return ItemStack.EMPTY;

                setPlayersCursor(getItemAtSlot(slot));

                if (clickedButton == null) {
                    return ItemStack.EMPTY;
                }

                ButtonClick click = getButtonClickType(clickType, dragType);
                MovableButtonAction action = new MovableButtonAction(player, click, clickedButton, template, page, targetTemplateSlot);
                clickedButton.onClick(action);
                ((Movable) clickedButton).onPickup(action);

                if (action.isCancelled()) {
                    setPlayersCursor(ItemStack.EMPTY);
                    updateSlotStack(targetTemplateSlot, clickedButton.getDisplay(), template instanceof InventoryTemplate);
                    return ItemStack.EMPTY;
                } else {
                    cursorButton = clickedButton;
                    setButton(slot, null);

                    // Clone needs to return empty ItemStack or it desyncs.
                    if (clickType == ClickType.CLONE || clickType == ClickType.QUICK_MOVE) {
                        setPlayersCursor(cursorButton.getDisplay());
                        return ItemStack.EMPTY;
                    } else {
                        return cursorButton.getDisplay();
                    }
                }
            } else {
                // This prevents a desync on double clicking when dropping
                if (clickType == ClickType.PICKUP_ALL || slot == -999) {
                    setPlayersCursor(cursorButton.getDisplay());
                    return ItemStack.EMPTY;
                }

                // Handle collision
                if (isSlotOccupied(slot)) {
                    setPlayersCursor(cursorButton.getDisplay());

                    /*
                     * When a quick move is performed, it will apply slot clicks to all identical items, causing
                     * collisions when trying to drop. Quick move wants a return type of an empty ItemStack,
                     * so this guarantees it, otherwise there will be a desync.
                     */
                    if (clickType == ClickType.QUICK_MOVE || clickType == ClickType.CLONE) {
                        return ItemStack.EMPTY;
                    } else {
                        return cursorButton.getDisplay();
                    }
                } else {
                    ButtonClick click = getButtonClickType(clickType, dragType);
                    MovableButtonAction action = new MovableButtonAction(player, click, cursorButton, template, page, targetTemplateSlot);
                    cursorButton.onClick(action);
                    ((Movable) cursorButton).onDrop(action);

                    if (action.isCancelled()) {
                        // Clone needs to return empty ItemStack or it desyncs.
                        if (clickType == ClickType.CLONE) {
                            return ItemStack.EMPTY;
                        }

                        setPlayersCursor(cursorButton.getDisplay());
                        updateSlotStack(targetTemplateSlot, ItemStack.EMPTY, template instanceof InventoryTemplate);
                        return ItemStack.EMPTY;
                    } else {
                        setButton(slot, cursorButton);
                        cursorButton = null;
                        setPlayersCursor(ItemStack.EMPTY);
                        return ItemStack.EMPTY;
                    }
                }
            }
        }
    }

    private boolean isSlotOccupied(int slot) {
        if (isSlotInPlayerInventory(slot) && inventoryTemplate == null) {
            return player.inventoryContainer.inventorySlots.get(getTemplateIndex(slot) + 9).getHasStack();
        } else {
            return getButton(slot) != null;
        }
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        if (page != this.page) {
            this.page.unsubscribe(this);
            this.page = page;

            initializePage(page);
            subscribeToPage(page);

            refreshContainer();

            page.onOpen(new PageAction(player, page));
        }
    }

    public void refreshContainer() {
        SPacketOpenWindow openWindow;
        if (page.getTemplate().getTemplateType() == TemplateType.CRAFTING_TABLE) {
            openWindow = new SPacketOpenWindow(
                    player.currentWindowId,
                    page.getTemplate().getTemplateType().getID(),
                    new TextComponentString(page.getTitle())
            );
        } else {
            openWindow = new SPacketOpenWindow(
                    player.currentWindowId,
                    page.getTemplate().getTemplateType().getID(),
                    new TextComponentString(page.getTitle()),
                    page.getTemplate().getSize()
            );
        }
        player.connection.sendPacket(openWindow);
        updateAllContainerContents();
    }

    private void updateAllContainerContents() {
        player.sendAllContents(player.openContainer, inventoryItemStacks);

        /*
         * Detects changes in the player's inventory and updates them. This is to prevent desyncs if a player
         * gets items added to their inventory while in the user interface.
         */
        player.inventoryContainer.detectAndSendChanges();
        if (inventoryTemplate != null) {
            player.sendAllContents(player.inventoryContainer, inventoryTemplate.getFullDisplay(player));
        } else {
            player.sendAllContents(player.inventoryContainer, player.inventoryContainer.inventoryItemStacks);
        }
    }

    private void setPlayersCursor(ItemStack stack) {
        SPacketSetSlot setCursorSlot = new SPacketSetSlot(-1, 0, stack);
        player.connection.sendPacket(setCursorSlot);
    }

    private void setButton(int slot, Button button) {
        if (slot < 0) return;

        //Check if it's player's inventory or UI slot
        if (slot >= page.getTemplate().getSize()) {
            if (inventoryTemplate != null) {
                int targetedPlayerSlotIndex = slot - page.getTemplate().getSize();

                inventoryTemplate.getSlot(targetedPlayerSlotIndex).setButton(button);
            }
        } else {
            page.getTemplate().getSlot(slot).setButton(button);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        if (closing) return;

        closing = true;
        page.onClose(new PageAction(player, page));
        page.unsubscribe(this);
        this.inventorySlots.forEach((slot) -> ((TemplateSlot) slot).unsubscribe(this));
        if (inventoryTemplate != null) {
            for (int i = 0; i < inventoryTemplate.getSize(); i++) {
                inventoryTemplate.getSlot(i).unsubscribe(this);
            }
        }
        MinecraftForge.EVENT_BUS.unregister(this);

        player.inventoryContainer.detectAndSendChanges();
        player.sendAllContents(player.inventoryContainer, player.inventoryContainer.inventoryItemStacks);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
    }

}

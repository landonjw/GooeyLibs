package ca.landonjw.gooeylibs2.implementation;

import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.ButtonAction;
import ca.landonjw.gooeylibs2.api.button.ButtonClick;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.moveable.Movable;
import ca.landonjw.gooeylibs2.api.button.moveable.MovableButtonAction;
import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.api.page.PageAction;
import ca.landonjw.gooeylibs2.api.template.Template;
import ca.landonjw.gooeylibs2.api.template.TemplateType;
import ca.landonjw.gooeylibs2.api.template.slot.TemplateSlot;
import ca.landonjw.gooeylibs2.api.template.types.InventoryTemplate;
import ca.landonjw.gooeylibs2.implementation.tasks.Task;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
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
        this.inventoryTemplate = page.getInventoryTemplate().orElse(null);

        bindSlots();
        bindPage();
    }

    private void bindPage() {
        page.subscribe(this, this::refresh);
    }

    public void refresh() {
        unbindSlots();
        inventoryTemplate = page.getInventoryTemplate().orElse(null);
        bindSlots();
        openWindow();
    }

    private void bindSlots() {
        inventorySlots = page.getTemplate().getSlots();
        for (int i = 0; i < inventorySlots.size(); i++) {
            int index = i;
            ((TemplateSlot) inventorySlots.get(i)).subscribe(this, () -> {
                updateSlotStack(index, getItemAtSlot(index), false);
            });
        }
        inventoryItemStacks = page.getTemplate().getDisplayStacks();

        /*
         * Add user inventory portion to the container slots and stacks.
         * Adding these slots are necessary to stop Sponge from having an aneurysm about missing slots,
         */
        if (inventoryTemplate == null) {
            // Sets the slots for the main inventory.
            for (int i = 9; i < 36; i++) {
                GooeyButton button = GooeyButton.of(player.inventory.mainInventory.get(i));
                inventorySlots.add(new TemplateSlot(button, i - 9, 0, 0));
                inventoryItemStacks.add(button.getDisplay());
            }
            // Sets the slots for the hotbar.
            for (int i = 0; i < 9; i++) {
                GooeyButton button = GooeyButton.of(player.inventory.mainInventory.get(i));
                inventorySlots.add(new TemplateSlot(button, i + 27, 0, 0));
                inventoryItemStacks.add(button.getDisplay());
            }
        } else {
            for (int i = 0; i < inventoryTemplate.getSize(); i++) {
                int index = i;
                int itemSlot = i + page.getTemplate().getSize();
                inventoryTemplate.getSlot(i).subscribe(this, () -> {
                    updateSlotStack(index, getItemAtSlot(itemSlot), true);
                });
                inventorySlots.add(inventoryTemplate.getSlot(i));
                inventoryItemStacks.add(inventoryTemplate.getSlot(i).getStack());
            }
        }
    }

    private void unbindSlots() {
        inventorySlots.forEach(slot -> {
            ((TemplateSlot) slot).unsubscribe(this);
        });
        if (inventoryTemplate != null) {
            inventoryTemplate.getSlots().forEach(slot -> {
                ((TemplateSlot) slot).unsubscribe(this);
            });
        }
    }

    private void updateSlotStack(int index, ItemStack stack, boolean playerInventory) {
        if (playerInventory) {
            SPacketSetSlot setSlot = new SPacketSetSlot(windowId, page.getTemplate().getSize() + index, stack);
            player.connection.sendPacket(setSlot);
        } else {
            SPacketSetSlot setSlot = new SPacketSetSlot(windowId, index, stack);
            player.connection.sendPacket(setSlot);
        }
    }

    private int getTemplateIndex(int slotIndex) {
        if (isSlotInPlayerInventory(slotIndex)) {
            return slotIndex - page.getTemplate().getSize();
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
        if (slot == -999 || slot >= inventorySlots.size()) {
            return ItemStack.EMPTY;
        }
        return inventorySlots.get(slot).getStack();
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

    public void open() {
        player.closeContainer();
        player.openContainer = this;
        player.currentWindowId = windowId;
        openWindow();
        page.onOpen(new PageAction(player, page));
    }

    private void openWindow() {
        SPacketOpenWindow openWindow = new SPacketOpenWindow(
                player.currentWindowId,
                page.getTemplate().getTemplateType().getID(),
                new TextComponentString(page.getTitle()),
                page.getTemplate().getTemplateType() == TemplateType.CRAFTING_TABLE ? 0 : page.getTemplate().getSize()
        );
        player.connection.sendPacket(openWindow);
        updateAllContainerContents();
    }

    private void patchDesyncs(int slot, ClickType clickType) {
        if (clickType == ClickType.PICKUP || clickType == ClickType.CLONE || clickType == ClickType.THROW) {
            updateSlotStack(getTemplateIndex(slot), getItemAtSlot(slot), isSlotInPlayerInventory(slot));
        } else if (clickType == ClickType.QUICK_MOVE || clickType == ClickType.PICKUP_ALL) {
            updateAllContainerContents();
        }
    }

    @Override
    public ItemStack slotClick(int slot, int dragType, ClickType clickType, EntityPlayer playerSP) {
        // Don't do anything if user is only clicking edge of UI.
        if (slot == -1) {
            return ItemStack.EMPTY;
        }

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
                    if (cursorButton != null) {
                        ItemStack clickedItem = getItemAtSlot(slot);
                        ItemStack cursorItem = cursorButton.getDisplay();

                        if (clickedItem.getItem() == cursorItem.getItem() && ItemStack.areItemStackTagsEqual(clickedItem, cursorItem)) {
                            ItemStack copy = getItemAtSlot(slot).copy();
                            copy.setCount(copy.getCount() + cursorButton.getDisplay().getCount());
                            return copy;
                        }
                        return cursorButton.getDisplay();
                    }
                }
                return ItemStack.EMPTY;
            }
            lastClickTick = server.getTickCounter();
        }

        if (clickType == ClickType.QUICK_CRAFT && dragType == 8) {
            /*
             * If the user middle clicks and drags, this refreshes the container at the end of the tick.
             * This is done because the click type propagates with the drag, yet does not always have a
             * termination drag type. So we track the entry drag, and prevent the rest of the clicks from
             * invoking.
             */
            Task.builder()
                    .execute(() -> {
                        updateAllContainerContents();
                        setPlayersCursor((cursorButton != null) ? cursorButton.getDisplay() : ItemStack.EMPTY);
                    })
                    .build();
            return ItemStack.EMPTY;
        }

        patchDesyncs(slot, clickType);

        Button clickedButton = getButton(slot);
        /*
         *  If the button being interacted with is moveable, or there is currently a moveable button on the cursor,
         *  send it to a separate handler.
         */
        if (clickedButton instanceof Movable || cursorButton != null) {
            return handleMovableButton(slot, dragType, clickType);
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

    private ItemStack handleMovableButton(int slot, int dragType, ClickType clickType) {
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
                if (clickType == ClickType.QUICK_CRAFT && dragType == 9) {
                    setPlayersCursor(ItemStack.EMPTY);
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
                    if (clickType == ClickType.CLONE || clickType == ClickType.QUICK_MOVE || clickType == ClickType.THROW) {
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
                    if (clickType == ClickType.QUICK_MOVE || clickType == ClickType.CLONE || clickType == ClickType.THROW) {
                        return ItemStack.EMPTY;
                    } else if (clickType == ClickType.QUICK_CRAFT) {
                        updateSlotStack(getTemplateIndex(slot), getItemAtSlot(slot), isSlotInPlayerInventory(slot));
                        return ItemStack.EMPTY;
                    } else if (clickType == ClickType.PICKUP) {
                        return getItemAtSlot(slot);
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

}

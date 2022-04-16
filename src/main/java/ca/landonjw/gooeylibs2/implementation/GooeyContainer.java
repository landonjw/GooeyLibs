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
import ca.landonjw.gooeylibs2.api.template.slot.TemplateSlot;
import ca.landonjw.gooeylibs2.api.template.types.InventoryTemplate;
import ca.landonjw.gooeylibs2.implementation.tasks.Task;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public class GooeyContainer extends AbstractContainerMenu {

    private final MinecraftServer server;
    private final ServerPlayer player;

    private Page page;
    public InventoryTemplate inventoryTemplate;

    private long lastClickTick;
    private boolean closing;

    /*
     *  Keeps track of a movable button that is on the cursor.
     *  If there is not a button on the cursor, this is null.
     */
    private Button cursorButton;

    public GooeyContainer(@Nonnull ServerPlayer player, @Nonnull Page page) {
        super(page.getTemplate().getTemplateType().getContainerType(page.getTemplate()), 1);
        this.server = player.level.getServer();
        this.player = player;

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
        broadcastFullState();
    }

    private void bindSlots() {
        List<Slot> slots = page.getTemplate().getSlots();
        for (int i = 0; i < slots.size(); i++) {
            int index = i;
            ((TemplateSlot) slots.get(i)).subscribe(this, () -> {
                updateSlotStack(index, getItemAtSlot(index), false);
            });
            addSlot(slots.get(i));
        }

        /*
         * Add user inventory portion to the container slots and stacks.
         * Adding these slots are necessary to stop Sponge from having an aneurysm about missing slots,
         */
        if (inventoryTemplate != null) {
            for (int i = 0; i < inventoryTemplate.getSize(); i++) {
                int index = i;
                int itemSlot = i + page.getTemplate().getSize();
                inventoryTemplate.getSlot(i).subscribe(this, () -> {
                    updateSlotStack(index, getItemAtSlot(itemSlot), true);
                });
                addSlot(inventoryTemplate.getSlot(i));
            }
        } else {
            // Sets the slots for the main inventory.
            for (int i = 9; i < 36; i++) {
                GooeyButton button = GooeyButton.of(player.getInventory().getItem(i));
                addSlot(new TemplateSlot(button, i - 9, 0, 0));
            }
            // Sets the slots for the hotbar.
            for (int i = 0; i < 9; i++) {
                GooeyButton button = GooeyButton.of(player.getInventory().getItem(i));
                addSlot(new TemplateSlot(button, i + 27, 0, 0));
            }
        }
    }

    private void unbindSlots() {
        slots.forEach(slot -> {
            ((TemplateSlot) slot).unsubscribe(this);
        });
        if (inventoryTemplate != null) {
            inventoryTemplate.getSlots().forEach(slot -> {
                ((TemplateSlot) slot).unsubscribe(this);
            });
        }

        slots = NonNullList.create();

    }

    private void updateSlotStack(int index, ItemStack stack, boolean playerInventory) {
//        ((TemplateSlot) getSlot(index + (playerInventory ? page.getTemplate().getSize() : 0))).set.set(stack);
//        broadcastChanges();
//        if (playerInventory) {
//            this.setRemoteSlot(page.getTemplate().getSize() + index, stack);
//        } else {
//            this.setRemoteSlot(index, stack);
//        }
//
//        if (this.synchronizer != null) {
//            this.synchronizer.sendSlotChange(this, , stack);
//        }
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
        return slot >= templateSize && slot - templateSize < player.inventoryMenu.slots.size();
    }

    private ItemStack getItemAtSlot(int slot) {
        if (slot == -999 || slot >= slots.size()) {
            return ItemStack.EMPTY;
        }
        return slots.get(slot).getItem();
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

    private void openWindow() {
        player.openMenu(new SimpleMenuProvider((id, p_39955_, p_39956_) -> {
            this.containerId = id;

            return this;
        }, page.getTitle()));

        ClientboundOpenScreenPacket openWindow = new ClientboundOpenScreenPacket(
                player.containerCounter,
                page.getTemplate().getTemplateType().getContainerType(page.getTemplate()),
                page.getTitle()
//                page.getTemplate().getTemplateType() == TemplateType.CRAFTING_TABLE ? 0 : page.getTemplate().getSize() TODO: Check this works
        );
        player.connection.send(openWindow);

        broadcastFullState();
    }

    private void patchDesyncs(int slot, ClickType clickType) {
        if (clickType == ClickType.PICKUP || clickType == ClickType.CLONE || clickType == ClickType.THROW) {
            updateSlotStack(getTemplateIndex(slot), getItemAtSlot(slot), isSlotInPlayerInventory(slot));
        }
    }

    @Override
    public void clicked(int p_150400_, int p_150401_, ClickType p_150402_, Player p_150403_) {
        clickedProxied(p_150400_, p_150401_, p_150402_, p_150403_);
        broadcastChanges();
    }

    public void clickedProxied(int slot, int dragType, ClickType clickType, Player playerSP) {
        // Don't do anything if user is only clicking edge of UI.
        if (slot == -1 || slot == -999) {
            return;
        }

        /*
         * These click types represent the user quickly picking up or moving items.
         * The click type proliferates and invokes slotClick for each stack that would be affected.
         * In order to prevent this method invoking logic every time, we track the last time
         * the click type was used. If a click of the same type has run on the same tick,
         * it will return out.
         */
        if (clickType == ClickType.QUICK_MOVE || clickType == ClickType.PICKUP_ALL || clickType == ClickType.PICKUP) {
            if (lastClickTick == server.getTickCount()) {
                if (clickType == ClickType.PICKUP) {
                    if (cursorButton != null) {
                        ItemStack clickedItem = getItemAtSlot(slot);
                        ItemStack cursorItem = cursorButton.getDisplay();

                        if (clickedItem.getItem() == cursorItem.getItem() && ItemStack.tagMatches(clickedItem, cursorItem)) {
                            ItemStack copy = getItemAtSlot(slot).copy();
                            copy.setCount(copy.getCount() + cursorButton.getDisplay().getCount());
                            broadcastChanges();
                            return;
                        }
                        return;
                    }
                }
                return;
            }
            lastClickTick = server.getTickCount();
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
                        setPlayersCursor((cursorButton != null) ? cursorButton.getDisplay() : ItemStack.EMPTY);
                        broadcastChanges();
                    })
                    .build();
            return;
        }

        patchDesyncs(slot, clickType);
        Button clickedButton = getButton(slot);
        /*
         *  If the button being interacted with is moveable, or there is currently a moveable button on the cursor,
         *  send it to a separate handler.
         */
        if (clickedButton instanceof Movable || cursorButton != null) {
            handleMovableButton(slot, dragType, clickType);
            return;
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
            return;
        }

        ButtonClick buttonClickType = getButtonClickType(clickType, dragType);
        if (clickedButton != null) {
            ButtonAction action = new ButtonAction(player, buttonClickType, clickedButton, page.getTemplate(), page, slot);
            clickedButton.onClick(action);
        }
    }

    private ButtonClick getButtonClickType(ClickType type, int dragType) {
        return switch (type) {
            case PICKUP -> (dragType == 0) ? ButtonClick.LEFT_CLICK : ButtonClick.RIGHT_CLICK;
            case CLONE -> ButtonClick.MIDDLE_CLICK;
            case QUICK_MOVE -> (dragType == 0) ? ButtonClick.SHIFT_LEFT_CLICK : ButtonClick.SHIFT_RIGHT_CLICK;
            case THROW -> ButtonClick.THROW;
            default -> ButtonClick.OTHER;
        };
    }

    private void handleMovableButton(int slot, int dragType, ClickType clickType) {
        /*
         * This prevents a desync with dragging an item.
         * Quick crafts begin and end with a click on slot -999,
         * we want to ignore those calls.
         */
        if (clickType == ClickType.QUICK_CRAFT && slot == -999) {
            return;
        }

        Template template = getTemplateFromIndex(slot);
        int targetTemplateSlot = getTemplateIndex(slot);

        if (template == null) {
            if (clickType == ClickType.PICKUP && isSlotOccupied(slot)) {
                setPlayersCursor((cursorButton != null) ? cursorButton.getDisplay() : ItemStack.EMPTY);
                return;
            }
            if (clickType == ClickType.QUICK_CRAFT) {
                updateSlotStack(getTemplateIndex(slot), getItemAtSlot(slot), true);
            }
            if (cursorButton != null) {
                setPlayersCursor(cursorButton.getDisplay());
            }
        } else {
            Button clickedButton = getButton(slot);

            if (cursorButton == null) {
                if (slot == -999) return;

                setPlayersCursor(getItemAtSlot(slot));

                if (clickedButton == null) {
                    return;
                }
                if (clickType == ClickType.QUICK_CRAFT && dragType == 9) {
                    setPlayersCursor(ItemStack.EMPTY);
                    return;
                }

                ButtonClick click = getButtonClickType(clickType, dragType);
                MovableButtonAction action = new MovableButtonAction(player, click, clickedButton, template, page, targetTemplateSlot);
                clickedButton.onClick(action);
                ((Movable) clickedButton).onPickup(action);

                if (action.isCancelled()) {
                    setPlayersCursor(ItemStack.EMPTY);
                    updateSlotStack(targetTemplateSlot, clickedButton.getDisplay(), template instanceof InventoryTemplate);
                } else {
                    cursorButton = clickedButton;
                    setButton(slot, null);

                    // Clone needs to return empty ItemStack or it desyncs.
                    if (clickType == ClickType.CLONE || clickType == ClickType.QUICK_MOVE || clickType == ClickType.THROW) {
                        setPlayersCursor(cursorButton.getDisplay());
                    }
                }
            } else {
                // This prevents a desync on double clicking when dropping
                if (clickType == ClickType.PICKUP_ALL || slot == -999) {
                    setPlayersCursor(cursorButton.getDisplay());
                    return;
                }

                // Handle collision
                if (isSlotOccupied(slot)) {
                    setPlayersCursor(cursorButton.getDisplay());

                    /*
                     * When a quick move is performed, it will apply slot clicks to all identical items, causing
                     * collisions when trying to drop. Quick move wants a return type of an empty ItemStack,
                     * so this guarantees it, otherwise there will be a desync.
                     */
                    switch (clickType) {
                        case QUICK_MOVE:
                        case CLONE:
                        case THROW:
                            break;
                        case QUICK_CRAFT:
                            updateSlotStack(getTemplateIndex(slot), getItemAtSlot(slot), isSlotInPlayerInventory(slot));
                            break;
                    }
                } else {
                    ButtonClick click = getButtonClickType(clickType, dragType);
                    MovableButtonAction action = new MovableButtonAction(player, click, cursorButton, template, page, targetTemplateSlot);
                    cursorButton.onClick(action);
                    ((Movable) cursorButton).onDrop(action);

                    if (action.isCancelled()) {
                        // Clone needs to return empty ItemStack or it desyncs.
                        if (clickType == ClickType.CLONE) {
                            return;
                        }

                        setPlayersCursor(cursorButton.getDisplay());
                        updateSlotStack(targetTemplateSlot, ItemStack.EMPTY, template instanceof InventoryTemplate);
                    } else {
                        setButton(slot, cursorButton);
                        cursorButton = null;
                        setPlayersCursor(ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    private boolean isSlotOccupied(int slot) {
        if (isSlotInPlayerInventory(slot) && inventoryTemplate == null) {
            return player.containerMenu.slots.get(getTemplateIndex(slot) + 9).hasItem();
        } else {
            return getButton(slot) != null;
        }
    }

    public Page getPage() {
        return page;
    }

    private void setPlayersCursor(ItemStack stack) {
        setCarried(stack);
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
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    @Override
    public void removed(Player playerIn) {
        if (closing) return;

        closing = true;
        page.onClose(new PageAction(player, page));
        page.unsubscribe(this);
        this.slots.forEach((slot) -> ((TemplateSlot) slot).unsubscribe(this));
        if (inventoryTemplate != null) {
            for (int i = 0; i < inventoryTemplate.getSize(); i++) {
                inventoryTemplate.getSlot(i).unsubscribe(this);
            }
        }
//        MinecraftForge.EVENT_BUS.unregister(this);
    }

}
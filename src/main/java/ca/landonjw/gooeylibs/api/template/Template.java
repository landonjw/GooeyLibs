package ca.landonjw.gooeylibs.api.template;

import ca.landonjw.gooeylibs.api.data.UpdateEmitter;
import ca.landonjw.gooeylibs.api.template.slot.TemplateSlot;
import ca.landonjw.gooeylibs.implementation.GooeyContainer;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

public abstract class Template extends UpdateEmitter<Template> {

    protected final TemplateType templateType;

    private final NonNullList<TemplateSlot> slots;
    private final NonNullList<ItemStack> displayStacks;

    protected Template(@Nonnull TemplateType templateType, @Nonnull TemplateSlot[] slots) {
        this.templateType = templateType;

        this.slots = NonNullList.create();
        this.displayStacks = NonNullList.create();

        for (TemplateSlot slot : slots) {
            this.slots.add(slot);
            this.displayStacks.add(slot.getStack());
            slot.subscribe(this, (update) -> displayStacks.set(update.getSlotIndex(), update.getStack()));
        }
    }

    public final TemplateType getTemplateType() {
        return templateType;
    }

    public final int getSize() {
        return slots.size();
    }

    public final TemplateSlot getSlot(int index) {
        return slots.get(index);
    }

    public final void updateContainer(@Nonnull GooeyContainer container) {
        container.inventorySlots = Lists.newArrayList(this.slots);
        container.inventoryItemStacks = displayStacks;
    }

}
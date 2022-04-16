package ca.landonjw.gooeylibs2.api.template;

import ca.landonjw.gooeylibs2.api.data.UpdateEmitter;
import ca.landonjw.gooeylibs2.api.template.slot.TemplateSlot;
import com.google.common.collect.Lists;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

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
            this.displayStacks.add(slot.getItem());
            slot.subscribe(this, (update) -> displayStacks.set(update.getSlotIndex(), update.getItem()));
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

    public final List<Slot> getSlots() {
        return Lists.newArrayList(slots);
    }

    public abstract Template clone();

    @Deprecated
    public final NonNullList<ItemStack> getDisplayStacks() {
        NonNullList<ItemStack> copy = NonNullList.create();
        copy.addAll(displayStacks);
        return copy;
    }

}
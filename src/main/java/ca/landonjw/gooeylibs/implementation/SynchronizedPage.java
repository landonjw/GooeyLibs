package ca.landonjw.gooeylibs.implementation;

import ca.landonjw.gooeylibs.api.button.Button;
import ca.landonjw.gooeylibs.api.data.EventEmitterBase;
import ca.landonjw.gooeylibs.api.page.IPage;
import ca.landonjw.gooeylibs.api.template.ITemplate;
import ca.landonjw.gooeylibs.api.template.chest.ChestTemplate;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class SynchronizedPage extends EventEmitterBase<IPage> implements IPage {

    private final ChestTemplate template;
    private boolean active = false;

    private final Button setInactiveButton = Button.builder()
            .display(new ItemStack(Blocks.WOOL, 1, EnumDyeColor.RED.getMetadata()))
            .title("Set Inactive")
            .onClick(() -> setActive(false))
            .build();

    private final Button setActiveButton = Button.builder()
            .display(new ItemStack(Blocks.WOOL, 1, EnumDyeColor.GREEN.getMetadata()))
            .title("Set Active")
            .onClick(() -> setActive(true))
            .build();

    public SynchronizedPage() {
        Button filler = Button.builder()
                .display(new ItemStack(Blocks.STAINED_GLASS_PANE, 1, EnumDyeColor.GRAY.getMetadata()))
                .build();

        this.template = ChestTemplate.builder(6)
                .fill(filler)
                .set(3, 4, setActiveButton)
                .build();
    }

    private void setActive(boolean state) {
        this.active = state;
        this.template.getSlot(3, 4).setButton((state) ? setInactiveButton : setActiveButton);
        this.emit(this);
    }

    @Override
    public ITemplate getTemplate() {
        return template;
    }

    @Override
    public String getTitle() {
        return active ? TextFormatting.GREEN + "Active Page!" : TextFormatting.RED + "Inactive Page!";
    }

}

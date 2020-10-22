package ca.landonjw.gooeylibs.implementation;

import ca.landonjw.gooeylibs.api.button.GooeyButton;
import ca.landonjw.gooeylibs.api.page.Page;
import ca.landonjw.gooeylibs.api.template.Template;
import ca.landonjw.gooeylibs.api.template.chest.ChestTemplate;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class SynchronizedPage extends Page {

    private boolean active = false;

    private final GooeyButton setInactiveButton = GooeyButton.builder()
            .display(new ItemStack(Blocks.WOOL, 1, EnumDyeColor.RED.getMetadata()))
            .title("Set Inactive")
            .onClick(() -> setActive(false))
            .build();

    private final GooeyButton setActiveButton = GooeyButton.builder()
            .display(new ItemStack(Blocks.WOOL, 1, EnumDyeColor.GREEN.getMetadata()))
            .title("Set Active")
            .onClick(() -> setActive(true))
            .build();

    public SynchronizedPage() {
        GooeyButton filler = GooeyButton.builder()
                .display(new ItemStack(Blocks.STAINED_GLASS_PANE, 1, EnumDyeColor.GRAY.getMetadata()))
                .build();

        Template template = ChestTemplate.builder(6)
                .fill(filler)
                .set(3, 4, setActiveButton)
                .build();
        setTemplate(template);
        setTitle(TextFormatting.RED + "Inactive Page!");
    }

    private void setActive(boolean state) {
        this.active = state;
        ((ChestTemplate) getTemplate()).getSlot(3, 4).setButton((state) ? setInactiveButton : setActiveButton);
        setTitle(active ? TextFormatting.GREEN + "Active Page!" : TextFormatting.RED + "Inactive Page!");
        update();
    }

}
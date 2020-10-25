package ca.landonjw.gooeylibs.commands;

import ca.landonjw.gooeylibs.api.button.GooeyButton;
import ca.landonjw.gooeylibs.api.data.UpdateEmitter;
import ca.landonjw.gooeylibs.api.page.Page;
import ca.landonjw.gooeylibs.api.template.Template;
import ca.landonjw.gooeylibs.api.template.types.ChestTemplate;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class SynchronizedPage extends UpdateEmitter<Page> implements Page {

    private final ChestTemplate template;
    private String title;

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

        this.template = ChestTemplate.builder(6)
                .fill(filler)
                .build();
        setActive(false);
    }

    private void setActive(boolean state) {
        template.getSlot(3, 4).setButton((state) ? setInactiveButton : setActiveButton);
        this.title = state ? TextFormatting.GREEN + "Active Page!" : TextFormatting.RED + "Inactive Page!";
        update();
    }

    @Override
    public Template getTemplate() {
        return template;
    }

    @Override
    public String getTitle() {
        return title;
    }

}
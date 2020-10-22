package ca.landonjw.gooeylibs.implementation;

import ca.landonjw.gooeylibs.api.UIManager;
import ca.landonjw.gooeylibs.api.button.GooeyButton;
import ca.landonjw.gooeylibs.api.page.GooeyPage;
import ca.landonjw.gooeylibs.api.template.chest.ChestTemplate;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class TestCommand extends CommandBase {

    @Override
    public String getName() {
        return "testinv";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/testinv";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        GooeyButton button = GooeyButton.builder()
                .display(new ItemStack(Items.DIAMOND))
                .title("Hello")
                .build();

        ChestTemplate template = ChestTemplate.builder(6)
                .fill(button)
                .build();

        GooeyPage page = GooeyPage.builder()
                .template(template)
                .title("Hello world")
                .build();

        UIManager.openUIForcefully((EntityPlayerMP) sender, page);
    }

}

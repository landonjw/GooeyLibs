package ca.landonjw.gooeylibs.commands;

import ca.landonjw.gooeylibs.api.UIManager;
import ca.landonjw.gooeylibs.api.button.GooeyButton;
import ca.landonjw.gooeylibs.api.page.GooeyPage;
import ca.landonjw.gooeylibs.api.template.types.HopperTemplate;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class Test2Command extends CommandBase {

    private final int index = 0;

    @Override
    public String getName() {
        return "testinv2";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/testinv2";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        GooeyButton button1 = GooeyButton.builder()
                .display(new ItemStack(Items.DIAMOND))
                .title("Hello")
                .build();

        GooeyButton button2 = GooeyButton.builder()
                .display(new ItemStack(Items.EMERALD))
                .title("Hello")
                .build();

        GooeyButton button3 = GooeyButton.builder()
                .display(new ItemStack(Items.GOLD_INGOT))
                .title("Hello")
                .build();

        HopperTemplate template = HopperTemplate.builder()
                .set(0, button1)
                .set(1, button2)
                .set(2, button3)
                .set(3, button1)
                .set(4, button2)
                .build();

        GooeyPage page = GooeyPage.builder()
                .template(template)
                .title("Hello world")
                .build();

        UIManager.openUIForcefully((EntityPlayerMP) sender, page);
    }

}

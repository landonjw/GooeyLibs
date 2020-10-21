package ca.landonjw.gooeylibs.implementation;

import ca.landonjw.gooeylibs.api.UIManager;
import ca.landonjw.gooeylibs.api.button.Button;
import ca.landonjw.gooeylibs.api.page.Page;
import ca.landonjw.gooeylibs.api.template.chest.ChestTemplate;
import ca.landonjw.gooeylibs.implementation.tasks.Task;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class Test2Command extends CommandBase {

    private int index = 0;

    @Override
    public String getName() {
        return "testinv2";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/testinv2";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        Button button = Button.builder()
                .display(new ItemStack(Items.DIAMOND))
                .title("Hello")
                .build();

        ChestTemplate template = ChestTemplate.builder(6)
                .fill(button)
                .build();

        Page page = Page.builder()
                .template(template)
                .title("Hello world")
                .build();

        UIManager.openUIForcefully((EntityPlayerMP) sender, page);

        Button emerald = Button.builder()
                .display(new ItemStack(Items.EMERALD))
                .build();

        Task.builder()
                .execute(() -> {
                    int invIndex = ++index % page.getTemplate().getSize();
                    page.getTemplate().getSlot(invIndex).setButton(emerald);
                })
                .infinite()
                .interval(5)
                .build();
    }

}

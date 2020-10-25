package ca.landonjw.gooeylibs.commands;

import ca.landonjw.gooeylibs.api.UIManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class RateLimitPageCommand extends CommandBase {

    @Override
    public String getName() {
        return "ratelimit";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/ratelimit";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        RateLimitPage page = new RateLimitPage();
        UIManager.openUIForcefully((EntityPlayerMP) sender, page);
    }

}

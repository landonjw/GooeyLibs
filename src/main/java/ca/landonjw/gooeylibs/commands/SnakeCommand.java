package ca.landonjw.gooeylibs.commands;

import ca.landonjw.gooeylibs.api.UIManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class SnakeCommand extends CommandBase {

    @Override
    public String getName() {
        return "snakegame";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/snakegame";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        SnakePage page = new SnakePage();
        UIManager.openUIForcefully((EntityPlayerMP) sender, page);
    }

}
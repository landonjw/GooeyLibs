package ca.landonjw.gooeylibs.implementation;

import ca.landonjw.gooeylibs.api.UIManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class SynchronizedCommand extends CommandBase {

    private final SynchronizedPage page;

    public SynchronizedCommand() {
        this.page = new SynchronizedPage();
    }

    @Override
    public String getName() {
        return "syncinv";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/syncinv";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        UIManager.openUIForcefully((EntityPlayerMP) sender, page);
    }

}

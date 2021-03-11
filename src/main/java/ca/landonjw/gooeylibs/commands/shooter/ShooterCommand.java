package ca.landonjw.gooeylibs.commands.shooter;

import ca.landonjw.gooeylibs.api.UIManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class ShooterCommand extends CommandBase {

    @Override
    public String getName() {
        return "shooter";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/shooter";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        ShooterPage page = new ShooterPage();
        UIManager.openUIForcefully((EntityPlayerMP) sender, page);
    }

}

package ca.landonjw.gooeylibs.inventory.internal;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.function.Consumer;

public class Waiter {

	private final Runnable action;
	private int ticksToWait;

	public Waiter(Runnable action, int ticksToWait) {
		this.action = action;
		this.ticksToWait = ticksToWait;
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onServerTickEvent(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.START)
			return;
		ticksToWait = ticksToWait - 1;
		if (ticksToWait <= 0) {
			action.run();
			MinecraftForge.EVENT_BUS.unregister(this);
		}
	}

}

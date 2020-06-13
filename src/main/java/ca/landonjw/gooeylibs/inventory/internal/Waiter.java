package ca.landonjw.gooeylibs.inventory.internal;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.function.Consumer;

/**
 * Invokes a runnable after a specified delay.
 *
 * @author CraftSteamG
 * @since  1.0.3
 */
public class Waiter {

	/** The runnable to be invoked at an appropriate time. */
	private final Runnable action;
	/** The number of ticks to wait until the runnable is invoked. */
	private int ticksToWait;

	/**
	 * Constructor for the waitor.
	 *
	 * @param action      the action to invoke after delay
	 * @param ticksToWait the number of ticks to wait
	 */
	public Waiter(Runnable action, int ticksToWait) {
		this.action = action;
		this.ticksToWait = ticksToWait;
		MinecraftForge.EVENT_BUS.register(this);
	}

	/**
	 * Decrements {@link #ticksToWait} and determines if it should be invoked.
	 *
	 * @param event called when the server ticks
	 */
	@SubscribeEvent
	public void onServerTickEvent(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END){
			ticksToWait--;
			if (ticksToWait <= 0) {
				action.run();
				MinecraftForge.EVENT_BUS.unregister(this);
			}
		}
	}

}

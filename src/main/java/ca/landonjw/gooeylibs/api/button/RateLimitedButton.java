package ca.landonjw.gooeylibs.api.button;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class RateLimitedButton implements IButton {

	private IButton button;

	private int limit;
	private long timeInterval;
	private TimeUnit timeUnit;

	private Queue<Instant> lastActionTimes = new LinkedList<>();

	protected RateLimitedButton(@Nonnull RateLimitedButtonBuilder builder) {
		this.button = builder.button;
		this.limit = builder.limit;
		this.timeInterval = builder.timeInterval;
		this.timeUnit = Objects.requireNonNull(builder.timeUnit);
	}

	@Override
	public ItemStack getDisplay() {
		return button.getDisplay();
	}

	@Override
	public void onClick(ButtonAction action) {
		if(isRateLimited()) return;

		if(isTopElementAboveTimeThreadhold()) lastActionTimes.remove();
		button.onClick(action);
		lastActionTimes.add(Instant.now());
	}

	private boolean isRateLimited() {
		if(lastActionTimes.size() < limit) return false;
		return !isTopElementAboveTimeThreadhold();
	}

	private boolean isTopElementAboveTimeThreadhold() {
		if(lastActionTimes.isEmpty()) return false;
		Instant earliestActionTime = lastActionTimes.peek();
		return Duration.between(earliestActionTime, Instant.now()).toMillis() > timeUnit.toMillis(timeInterval);
	}

	public RateLimitedButton clone() {
		return new RateLimitedButtonBuilder(this).build();
	}

	public RateLimitedButtonBuilder toBuilder() {
		return new RateLimitedButtonBuilder(this);
	}

	public static RateLimitedButtonBuilder builder() {
		return new RateLimitedButtonBuilder();
	}

	public static class RateLimitedButtonBuilder {

		private IButton button;
		private int limit;
		private long timeInterval;
		private TimeUnit timeUnit;

		protected RateLimitedButtonBuilder() {

		}

		protected RateLimitedButtonBuilder(RateLimitedButton button) {

		}

		public RateLimitedButtonBuilder button(IButton button) {
			this.button = button;
			return this;
		}

		public RateLimitedButtonBuilder limit(int limit) {
			this.limit = limit;
			return this;
		}

		public RateLimitedButtonBuilder interval(long time, @Nonnull TimeUnit timeUnit) {
			this.timeInterval = time;
			this.timeUnit = timeUnit;
			return this;
		}

		public RateLimitedButton build() {
			return new RateLimitedButton(this);
		}

	}

}

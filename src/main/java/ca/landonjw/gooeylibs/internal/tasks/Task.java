package ca.landonjw.gooeylibs.internal.tasks;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class Task {

	private Consumer<Task> consumer;

	private long interval;
	private long currentIteration;
	private long iterations;

	private long ticksRemaining;
	private boolean expired;

	private Task(Consumer<Task> consumer, long delay, long interval, long iterations) {
		this.consumer = consumer;
		this.interval = interval;
		this.iterations = iterations;

		if(delay > 0) {
			ticksRemaining = delay;
		}
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired() {
		expired = true;
	}

	void tick() {
		if(!expired) {
			this.ticksRemaining = Math.max(0, --ticksRemaining);

			if(ticksRemaining == 0) {
				consumer.accept(this);
				currentIteration++;

				if(interval > 0 && (currentIteration < iterations || iterations == -1)) {
					ticksRemaining = interval;
				}
				else {
					expired = true;
				}
			}
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private Consumer<Task> consumer;
		private long delay;
		private long interval;
		private long iterations = 1;

		public Builder execute(@Nonnull Runnable runnable) {
			this.consumer = (task) -> runnable.run();
			return this;
		}

		public Builder delay(long delay) {
			if(delay < 0) {
				throw new IllegalArgumentException("delay must not be below 0");
			}
			this.delay = delay;
			return this;
		}

		public Builder interval(long interval) {
			if(interval < 0) {
				throw new IllegalArgumentException("interval must not be below 0");
			}
			this.interval = interval;
			return this;
		}

		public Builder iterations(long iterations) {
			if(iterations < -1) {
				throw new IllegalArgumentException("iterations must not be below -1");
			}
			this.iterations = iterations;
			return this;
		}

		public Builder infinite() {
			return iterations(-1);
		}

		public Task build() {
			if(consumer == null) {
				throw new IllegalStateException("consumer must be set");
			}
			Task task = new Task(consumer, delay, interval, iterations);
			TaskTickListener.addTask(task);
			return task;
		}

	}

}
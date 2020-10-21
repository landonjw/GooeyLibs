package ca.landonjw.gooeylibs.api.button;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class RateLimitedButton extends AbstractButton {

    private final IButton button;
    private final int limit;
    private final long timeInterval;
    private final TimeUnit timeUnit;

    private final Queue<Instant> lastActionTimes = new LinkedList<>();

    protected RateLimitedButton(@Nonnull IButton button, int limit, long timeInterval, @Nonnull TimeUnit timeUnit) {
        super(button.getDisplay());
        this.button = button;
        this.limit = limit;
        this.timeInterval = timeInterval;
        this.timeUnit = timeUnit;
    }

    @Override
    public void onClick(@Nonnull ButtonAction action) {
        if(isRateLimited()) return;

        if(isTopElementAboveTimeThreshold()) lastActionTimes.remove();
        button.onClick(action);
        lastActionTimes.add(Instant.now());
    }

    private boolean isRateLimited() {
        if(lastActionTimes.size() < limit) return false;
        return !isTopElementAboveTimeThreshold();
    }

    private boolean isTopElementAboveTimeThreshold() {
        if(lastActionTimes.isEmpty()) return false;
        Instant earliestActionTime = lastActionTimes.peek();
        return Duration.between(earliestActionTime, Instant.now()).toMillis() > timeUnit.toMillis(timeInterval);
    }

    public static class RateLimitedButtonBuilder {

        private IButton button;
        private int limit;
        private long timeInterval;
        private TimeUnit timeUnit;

        protected RateLimitedButtonBuilder() { }

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
            return new RateLimitedButton(button, limit, timeInterval, timeUnit);
        }

    }

}

package ca.landonjw.gooeylibs.api.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface EventEmitter<T> {

    void emit(@Nullable T value);

    void subscribe(@Nonnull Object subscriber, @Nonnull Consumer<T> consumer);

    default void subscribe(@Nonnull Object subscriber, @Nonnull Runnable runnable) {
        subscribe(subscriber, runnable::run);
    }

    void unsubscribe(@Nonnull Object subscriber);

}
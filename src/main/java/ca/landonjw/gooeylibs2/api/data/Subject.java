package ca.landonjw.gooeylibs2.api.data;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public interface Subject<T> {

    void subscribe(@Nonnull Object observer, @Nonnull Consumer<T> consumer);

    default void subscribe(@Nonnull Object observer, @Nonnull Runnable runnable) {
        this.subscribe(observer, t -> runnable.run());
    }

    void unsubscribe(@Nonnull Object observer);

}
package ca.landonjw.gooeylibs.api.data;

import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Consumer;

public class EventEmitter<T> implements Subject<T> {

    private final Map<Object, Consumer<T>> observers = Maps.newHashMap();

    @Override
    public void subscribe(@Nonnull Object subscriber, @Nonnull Consumer<T> consumer) {
        this.observers.put(subscriber, consumer);
    }

    @Override
    public void unsubscribe(@Nonnull Object subscriber) {
        this.observers.remove(subscriber);
    }

    public void emit(T value) {
        this.observers.values().forEach((observer) -> observer.accept(value));
    }

}

package ca.landonjw.gooeylibs2.api.data;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public abstract class UpdateEmitter<T> implements Subject<T> {

    private final EventEmitter<T> eventEmitter = new EventEmitter<>();

    @SuppressWarnings("unchecked")
    public UpdateEmitter() {
        try {
            T check = (T) this;
        } catch (ClassCastException e) {
            throw new IllegalStateException("bad generic given for superclass");
        }
    }

    @Override
    public void subscribe(@Nonnull Object observer, @Nonnull Consumer<T> consumer) {
        this.eventEmitter.subscribe(observer, consumer);
    }

    @Override
    public void unsubscribe(@Nonnull Object observer) {
        this.eventEmitter.unsubscribe(observer);
    }

    @SuppressWarnings("unchecked")
    public void update() {
        this.eventEmitter.emit((T) this);
    }

}

package ca.landonjw.gooeylibs.api.data;

import javax.annotation.Nullable;

public class Observable<T> extends EventEmitterBase<T> {

    private T value;

    public Observable() {}

    public Observable(T value) {
        this.value = value;
    }

    @Nullable
    public T getSnapshot() {
        return value;
    }

    @Override
    public void emit(@Nullable T value) {
        super.emit(value);
        this.value = value;
    }

    public static <T> Observable<T> of(T value) {
        return new Observable<>(value);
    }

}
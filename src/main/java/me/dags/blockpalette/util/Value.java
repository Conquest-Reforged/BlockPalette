package me.dags.blockpalette.util;

/**
 * @author dags <dags@dags.me>
 */
public class Value<T> {

    private final boolean instant;
    private Listener<T> listener;
    private T reference = null;
    private boolean dirty = false;

    private Value(T value) {
        this.reference = value;
        this.instant = false;
    }

    private Value(T value, boolean instant) {
        this.reference = value;
        this.instant = instant;
    }

    public boolean isPresent() {
        return reference != null;
    }

    public T get() {
        return reference;
    }

    public void setNullable(T value) {
        reference = value;
        dirty = true;

        if (instant) {
            markUpdated();
        }
    }

    public void set(T value) {
        if (reference != value && !reference.equals(value)) {
            this.reference = value;
            this.dirty = true;

            if (instant) {
                markUpdated();
            }
        }
    }

    public void markUpdated() {
        if (dirty && listener != null && reference != null) {
            listener.onUpdate(reference);
        }

        this.dirty = false;
    }

    public void setListener(Listener<T> listener) {
        this.listener = listener;
    }

    public interface Listener<T> {

        void onUpdate(T value);
    }

    public static <T> Value<T> of(T value) {
        return new Value<>(value);
    }

    public static <T> Value<T> instant(T value) {
        return new Value<>(value, true);
    }
}

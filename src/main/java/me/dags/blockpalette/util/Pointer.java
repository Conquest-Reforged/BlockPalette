package me.dags.blockpalette.util;

/**
 * @author dags <dags@dags.me>
 */
public class Pointer<T> {

    private final boolean instant;
    private Listener<T> listener;
    private T reference = null;
    private boolean changed = false;

    private Pointer(T value) {
        this.reference = value;
        this.instant = false;
    }

    private Pointer(T value, boolean instant) {
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
        changed = true;

        if (instant) {
            markUpdated();
        }
    }

    public void set(T value) {
        if (reference != value && !reference.equals(value)) {
            this.reference = value;
            this.changed = true;

            if (instant) {
                markUpdated();
            }
        }
    }

    public void markUpdated() {
        if (changed && listener != null && reference != null) {
            listener.onUpdate(reference);
        }

        this.changed = false;
    }

    public void setListener(Listener<T> listener) {
        this.listener = listener;
    }

    public interface Listener<T> {

        void onUpdate(T value);
    }

    public static <T> Pointer<T> of(T value) {
        return new Pointer<>(value);
    }

    public static <T> Pointer<T> instant(T value) {
        return new Pointer<>(value, true);
    }
}

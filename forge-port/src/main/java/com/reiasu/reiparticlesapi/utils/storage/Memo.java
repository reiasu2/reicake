package com.reiasu.reiparticlesapi.utils.storage;

import java.util.function.Supplier;

public final class Memo<T> {
    private final Supplier<T> supplier;
    private volatile T memo;
    private volatile boolean initialized;

    public Memo(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public Supplier<T> getSupplier() {
        return supplier;
    }

    public T get() {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    memo = supplier.get();
                    initialized = true;
                }
            }
        }
        return memo;
    }

    public Memo<T> setMemoValue(T memo) {
        this.memo = memo;
        return this;
    }

    public Memo<T> resetMemo() {
        initialized = false;
        memo = null;
        return this;
    }
}

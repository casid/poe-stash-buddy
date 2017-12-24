package org.jusecase.poe.entities;

import org.jusecase.builders.Builder;

@javax.annotation.Generated(value="jusecase-builders-generator")
public interface CurrencyBuilderMethods<T extends Currency, B extends Builder> extends Builder<T> {
    @Override
    default T build() {
        return getEntity();
    }

    T getEntity();

    default B withImageHash(String value) {
        getEntity().imageHash = value;
        return (B)this;
    }
}

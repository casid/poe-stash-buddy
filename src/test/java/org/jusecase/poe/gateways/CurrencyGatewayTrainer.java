package org.jusecase.poe.gateways;


import org.jusecase.poe.entities.Currency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CurrencyGatewayTrainer implements CurrencyGateway {
    private List<Currency> currencies = new ArrayList<>();

    @Override
    public List<Currency> getAll() {
        return currencies;
    }

    public void givenCurrency(Currency currency) {
        this.currencies.add(currency);
    }
}
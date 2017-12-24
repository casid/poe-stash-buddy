package org.jusecase.poe.gateways;

import org.jusecase.poe.entities.Currency;

import java.util.List;

public interface CurrencyGateway {
    List<Currency> getAll();
}

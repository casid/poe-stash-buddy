package org.jusecase.poe.gateways;

import org.jusecase.inject.Component;
import org.jusecase.poe.entities.Currency;
import org.jusecase.poe.plugins.ImageHashPlugin;
import org.jusecase.poe.util.PathUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class ResourceCurrencyGateway implements CurrencyGateway {
    @Inject
    private ImageHashPlugin imageHashPlugin;

    private List<Currency> currencies;

    @Override
    public List<Currency> getAll() {
        if (currencies == null) {
            currencies = loadCurrencies();
        }
        return currencies;
    }

    private List<Currency> loadCurrencies() {
        try {
            List<Currency> currencies = new ArrayList<>();
            Path directory = PathUtils.fromResource("currency/AncientOrb.png").getParent();
            Files.walk(directory, 1).filter(p -> p.toString().endsWith(".png")).forEach(path -> currencies.add(loadCurrency(path)));
            return currencies;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Currency loadCurrency(Path path) {
        try {
            Currency currency = new Currency();
            currency.imageHash = imageHashPlugin.getHash(Files.newInputStream(path));
            return currency;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

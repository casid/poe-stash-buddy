package org.jusecase.poe.gateways;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jusecase.inject.ComponentTest;
import org.jusecase.poe.entities.Currency;
import org.jusecase.poe.plugins.ImageHashPlugin;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceCurrencyGatewayTest implements ComponentTest {
    ResourceCurrencyGateway gateway;

    @BeforeEach
    void setUp() {
        givenDependency(new ImageHashPlugin());
        gateway = new ResourceCurrencyGateway();
    }

    @Test
    void currenciesAreLoadedFromResources() {
        List<Currency> currencies = gateway.getAll();

        assertThat(currencies.size()).isEqualTo(51);
        assertThat(currencies.get(0).imageHash).isEqualTo("001000100011111001101001110010100001101010110010100001111001111");
    }
}
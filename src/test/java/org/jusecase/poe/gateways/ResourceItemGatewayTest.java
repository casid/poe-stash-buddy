package org.jusecase.poe.gateways;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jusecase.inject.ComponentTest;
import org.jusecase.poe.entities.Item;
import org.jusecase.poe.entities.ItemType;
import org.jusecase.poe.plugins.ImageHashPlugin;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceItemGatewayTest implements ComponentTest {
    ResourceItemGateway gateway;

    private ImageHashPlugin imageHashPlugin;

    @BeforeEach
    void setUp() {
        givenDependency(imageHashPlugin = new ImageHashPlugin());
        gateway = new ResourceItemGateway();
    }

    @Test
    void currenciesAreLoadedFromResources() {
        List<Item> currencies = gateway.getAll();

        assertThat(currencies.size()).isEqualTo(59 + 1 + 104 + 156);
        assertThat(currencies.get(0).imageHash.features).isNotEmpty();
        assertThat(currencies.get(0).imageHash.colors).isNotEmpty();
        assertThat(currencies.get(0).type).isEqualTo(ItemType.CURRENCY);
        assertThat(currencies.get(59).type).isEqualTo(ItemType.CARD);
        assertThat(currencies.get(59 + 1).type).isEqualTo(ItemType.ESSENCE);
        assertThat(currencies.get(59 + 1 + 104).type).isEqualTo(ItemType.MAP);
    }

    @Test
    void noItemConflicts() {
        SoftAssertions s = new SoftAssertions();
        List<Item> allItems = gateway.getAll();
        for (int i = 0; i < allItems.size(); ++i) {
            Item item = allItems.get(i);
            for (int j = i + 1; j < allItems.size(); ++j) {
                Item otherItem = allItems.get(j);
                if (item.type != otherItem.type) {
                    s.assertThat(imageHashPlugin.isSimilar(item.imageHash, otherItem.imageHash)).describedAs("expecting " + item + " not to be similar to item " + otherItem).isFalse();
                }
            }
        }
        s.assertAll();
    }
}
package org.jusecase.poe.gateways;

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

        assertThat(currencies.size()).isEqualTo(51 + 1 + 104 + 139);
        assertThat(currencies.get(0).imageHash).isEqualTo("001010100111111011100001110010100101101010110000100001111001111001000100011111001101001110010100001101011110010100001111001111101000110011110001101001110010000001111011110000100100101001111");
        assertThat(currencies.get(0).type).isEqualTo(ItemType.CURRENCY);
        assertThat(currencies.get(51).type).isEqualTo(ItemType.CARD);
        assertThat(currencies.get(51 + 1).type).isEqualTo(ItemType.ESSENCE);
        assertThat(currencies.get(51 + 1 + 104).type).isEqualTo(ItemType.MAP);
    }

    @Test
    void noItemConflicts() {
        for (Item item : gateway.getAll()) {
            for (Item otherItem : gateway.getAll()) {
                if (item.type != otherItem.type) {
                    assertThat(imageHashPlugin.isSimilar(item.imageHash, otherItem.imageHash)).describedAs("expecting " + item + " not to be similar to item " + otherItem).isFalse();
                }
            }
        }
    }
}
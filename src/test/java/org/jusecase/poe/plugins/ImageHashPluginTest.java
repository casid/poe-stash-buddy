package org.jusecase.poe.plugins;

import org.assertj.core.description.Description;
import org.junit.jupiter.api.Test;
import org.jusecase.poe.entities.Hash;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jusecase.Builders.a;
import static org.jusecase.Builders.inputStream;

class ImageHashPluginTest {
    ImageHashPlugin plugin = new ImageHashPlugin();

    @Test
    void unsimilar_EngineersOrb() throws IOException {
        thenImagesAreSimilar("currency/EngineersOrb.png", "map/WolfMap.png", false);
    }

    @Test
    void unsimilar_CurrencyRerollMagic() throws IOException {
        thenImagesAreSimilar("currency/CurrencyRerollMagic.png", "essence/Rage7.png", false);
    }

    @Test
    void unsimilar_HarbingerShard() throws IOException {
        thenImagesAreSimilar("currency/HarbingerShard.png", "essence/Rage7.png", false);
    }

    @Test
    void unsimilar_CurrencyDuplicate() throws IOException {
        thenImagesAreSimilar("currency/CurrencyDuplicate.png", "essence/Torment6.png", false);
    }

    @Test
    void unsimilar_Sorrow7() throws IOException {
        thenImagesAreSimilar("essence/Sorrow7.png", "map/musicbox.png", false);
    }

    @Test
    void unsimilar_CurrencyRerollRare() throws IOException {
        thenImagesAreSimilar("currency/CurrencyRerollRare.png", "map/UndeadSiege.png", false);
    }

    @Test
    void unsimilar_ExaltedShard() throws IOException {
        thenImagesAreSimilar("currency/ExaltedShard.png", "map/CitySquare.png", false);
    }

    @Test
    void similarity_jewellers() throws IOException {
        thenImagesAreSimilar("inventory-4k-crop-jewellers.png", "currency/CurrencyRerollSocketNumbers.png", true);
    }

    @Test
    void similarity_chromatic() throws IOException {
        thenImagesAreSimilar("inventory-4k-crop-chromatic.png", "currency/CurrencyRerollSocketColours.png", true);
    }

    @Test
    void similarity_alteration() throws IOException {
        thenImagesAreSimilar("inventory-4k-crop-alteration.png", "currency/CurrencyRerollMagic.png", true);
    }

    @Test
    void similarity_card() throws IOException {
        thenImagesAreSimilar("inventory-2k-crop-card.png", "card/InventoryIcon.png", true);
    }

    @Test
    void similarity_ancient() throws IOException {
        thenImagesAreSimilar("inventory-2k-crop-ancient.png", "currency/AncientOrb.png", true);
    }

    @Test
    void similarity_alchshard() throws IOException {
        thenImagesAreSimilar("inventory-2k-crop-alchshard.png", "currency/AlchemyShard.png", true);
    }

    @Test
    void similarity_transmutation() throws IOException {
        thenImagesAreSimilar("inventory-4k-crop-transmutation.png", "currency/CurrencyUpgradeToMagic.png", true);
    }

    @Test
    void similarity_alchemy() throws IOException {
        thenImagesAreSimilar("inventory-2k-crop-alchemy.png", "currency/CurrencyUpgradeToRare.png", true);
    }

    private void thenImagesAreSimilar(String image1, String image2, boolean similar) throws IOException {
        Hash hash1 = plugin.getHash(a(inputStream().withResource(image1)));
        Hash hash2 = plugin.getHash(a(inputStream().withResource(image2)));
        assertThat(plugin.isSimilar(hash1, hash2))
                .describedAs(new Description() {
                    @Override
                    public String value() {
                        return "Actual normalized distance is f:" + plugin.getNormalizedDistance(hash1.features, hash2.features) +
                                ", c: " + plugin.getNormalizedDistance(hash1.colors, hash2.colors) +
                                " (absolute distance f:" + plugin.getDistance(hash1.features, hash2.features) +
                                " c:" + plugin.getDistance(hash1.colors, hash2.colors) + ")\n" +
                                hash1.toString() + "\n" +
                                hash2.toString();
                    }
                })
                .isEqualTo(similar);
    }
}
package org.jusecase.poe.plugins;

import org.junit.jupiter.api.Test;

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
    void unsimilar_Sorrow7() throws IOException {
        thenImagesAreSimilar("essence/Sorrow7.png", "map/musicbox.png", false);
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
    void similarity_transmutation() throws IOException {
        thenImagesAreSimilar("inventory-4k-crop-transmutation.png", "currency/CurrencyUpgradeToMagic.png", true);
    }

    @Test
    void similarity_alchemy() throws IOException {
        thenImagesAreSimilar("inventory-2k-crop-alchemy.png", "currency/CurrencyUpgradeToRare.png", true);
    }

    private void thenImagesAreSimilar(String image1, String image2, boolean similar) throws IOException {
        String hash1 = plugin.getHash(a(inputStream().withResource(image1)));
        String hash2 = plugin.getHash(a(inputStream().withResource(image2)));
        assertThat(plugin.isSimilar(hash1, hash2))
                .describedAs("Actual normalized distance is " + plugin.getNormalizedDistance(hash1, hash2) + " (absolute distance " + plugin.getDistance(hash1, hash2) + ")")
                .isEqualTo(similar);
    }
}
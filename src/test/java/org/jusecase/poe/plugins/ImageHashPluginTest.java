package org.jusecase.poe.plugins;


import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jusecase.Builders.a;
import static org.jusecase.Builders.inputStream;

@SuppressWarnings("SameParameterValue")
class ImageHashPluginTest {
    static ImageHashPlugin plugin;

    static List<String> hashes;

    static String jewellersHash;
    static String fusingHash;
    static String chromaticHash;
    static String regalHash;
    static String ancientHash;
    static String chaosShardHash;
    static String chaosHash;
    static String alterationHash;
    static String alchemyHash;
    static String cardHash;

    @BeforeAll
    static void setUp() throws IOException {
        plugin = new ImageHashPlugin();

        hashes = new ArrayList<>();
        hashes.add(jewellersHash = plugin.getHash(a(inputStream().withResource("currency/CurrencyRerollSocketNumbers.png"))));
        hashes.add(fusingHash = plugin.getHash(a(inputStream().withResource("currency/CurrencyRerollSocketLinks.png"))));
        hashes.add(chromaticHash = plugin.getHash(a(inputStream().withResource("currency/CurrencyRerollSocketColours.png"))));
        hashes.add(regalHash = plugin.getHash(a(inputStream().withResource("currency/CurrencyUpgradeMagicToRare.png"))));
        hashes.add(ancientHash = plugin.getHash(a(inputStream().withResource("currency/AncientOrb.png"))));
        hashes.add(chaosShardHash = plugin.getHash(a(inputStream().withResource("currency/ChaosShard.png"))));
        hashes.add(chaosHash = plugin.getHash(a(inputStream().withResource("currency/CurrencyRerollRare.png"))));
        hashes.add(alterationHash = plugin.getHash(a(inputStream().withResource("currency/CurrencyRerollMagic.png"))));
        hashes.add(alchemyHash = plugin.getHash(a(inputStream().withResource("currency/CurrencyUpgradeToRare.png"))));
        hashes.add(cardHash = plugin.getHash(a(inputStream().withResource("card/InventoryIcon.png"))));
    }

    @Test
    void similarity_jewellers() throws IOException {
        thenImageIsOnlySimilarTo("inventory-4k-crop-jewellers.png", jewellersHash);
    }

    @Test
    void similarity_chromatic() throws IOException {
        thenImageIsOnlySimilarTo("inventory-4k-crop-chromatic.png", chromaticHash);
    }

    @Test
    void similarity_alteration() throws IOException {
        thenImageIsOnlySimilarTo("inventory-4k-crop-alteration.png", alterationHash);
    }

    @Test
    void similarity_card() throws IOException {
        thenImageIsOnlySimilarTo("inventory-2k-crop-card.png", cardHash);
    }

    @Test
    void similarity_ancient() throws IOException {
        thenImageIsOnlySimilarTo("inventory-2k-crop-ancient.png", ancientHash);
    }

    @Test
    void similarity_alchemy() throws IOException {
        thenImageIsOnlySimilarTo("inventory-2k-crop-alchemy.png", alchemyHash);
    }

    @Test
    void similarity_nothing() throws IOException {
        thenImageIsSimilarToNoCurrency("inventory-4k-crop-nothing.png");
    }

    private void thenImageIsOnlySimilarTo(String imageResource, String expected) throws IOException {
        SoftAssertions s = new SoftAssertions();

        String imageResourceHash = plugin.getHash(a(inputStream().withResource(imageResource)));
        for (String hash : hashes) {
            s.assertThat(plugin.isSimilar(imageResourceHash, hash))
                    .describedAs("Actual normalized distance is " + plugin.getNormalizedDistance(imageResourceHash, hash) + " (absolute distance " + plugin.getDistance(imageResourceHash, hash) + ")")
                    .isEqualTo(hash.equals(expected));
        }

        s.assertAll();
    }

    private void thenImageIsSimilarToNoCurrency(String imageResource) throws IOException {
        SoftAssertions s = new SoftAssertions();

        String imageResourceHash = plugin.getHash(a(inputStream().withResource(imageResource)));
        for (String hash : hashes) {
            s.assertThat(plugin.isSimilar(imageResourceHash, hash)).isFalse();
        }

        s.assertAll();
    }
}
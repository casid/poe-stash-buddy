package org.jusecase.poe.gateways;

import org.jusecase.inject.Component;
import org.jusecase.poe.entities.Item;
import org.jusecase.poe.entities.ItemType;
import org.jusecase.poe.plugins.ImageHashPlugin;
import org.jusecase.poe.util.PathUtils;

import javax.inject.Inject;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ResourceItemGateway implements ItemGateway {
    @Inject
    private ImageHashPlugin imageHashPlugin;

    private List<Item> items;

    @Override
    public List<Item> getAll() {
        if (items == null) {
            items = loadItems();
        }
        return items;
    }

    @Override
    public Item getScrollOfWisdom() {
        return getAll().stream().filter(i -> "CurrencyIdentification.png".equals(i.image)).findFirst().orElse(null);
    }

    private List<Item> loadItems() {
        try {
            Path root = PathUtils.fromResource("icon.png").getParent();

            List<Item> items = new ArrayList<>();
            loadItems(root.resolve("currency"), ItemType.CURRENCY, items);
            loadItems(root.resolve("card"), ItemType.CARD, items);
            loadItems(root.resolve("essence"), ItemType.ESSENCE, items);
            loadItems(root.resolve("map"), ItemType.MAP, items);
            loadItems(root.resolve("fragment"), ItemType.FRAGMENT, items);

            if (items.isEmpty()) {
                throw new IllegalStateException("no item resources found!");
            }

            items.parallelStream().forEach(this::calculateImageHash);

            return items;
        } catch( FileSystemNotFoundException e ) {
            initFileSystem();
            return loadItems();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load items.", e);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void initFileSystem() {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        try {
            FileSystems.newFileSystem(Thread.currentThread().getContextClassLoader().getResource("icon.png").toURI(), env);
        } catch (Exception e) {
            throw new RuntimeException("Failed to init resource file system.", e);
        }
    }

    private void calculateImageHash(Item item) {
        try {
            Color backgroundColor = null;
            if (item.unidentified) {
                backgroundColor = new Color(42, 4, 4);
            }
            item.imageHash = imageHashPlugin.getHash(Files.newInputStream(item.path), backgroundColor);
        } catch (IOException e) {
            throw new RuntimeException("Failed to calculate perceptual hash for item " + item, e);
        }
    }

    private void loadItems(Path directory, ItemType type, List<Item> items) throws IOException {
        Files.walk(directory, 1).filter(p -> p.toString().endsWith(".png")).forEach(path -> {
            items.add(loadItem(path, type));

            if (type == ItemType.MAP) {
                Item unidentifiedMap = loadItem(path, type);
                unidentifiedMap.unidentified = true;
                items.add(unidentifiedMap);
            }
        });
    }

    private Item loadItem(Path path, ItemType type) {
        Item item = new Item();
        item.type = type;
        item.path = path;
        item.image = path.getFileName().toString();
        return item;
    }
}

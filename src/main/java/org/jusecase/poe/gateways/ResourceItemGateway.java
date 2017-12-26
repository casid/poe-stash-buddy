package org.jusecase.poe.gateways;

import org.jusecase.inject.Component;
import org.jusecase.poe.entities.Item;
import org.jusecase.poe.plugins.ImageHashPlugin;
import org.jusecase.poe.util.PathUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

    private List<Item> loadItems() {
        try {
            Path root = PathUtils.fromResource("icon.png").getParent();

            List<Item> items = new ArrayList<>();
            loadItems(root.resolve("currency"), items);
            return items;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadItems(Path directory, List<Item> items) throws IOException {
        Files.walk(directory, 1).filter(p -> p.toString().endsWith(".png")).forEach(path -> items.add(loadItem(path)));
    }

    private Item loadItem(Path path) {
        try {
            Item item = new Item();
            item.imageHash = imageHashPlugin.getHash(Files.newInputStream(path));
            return item;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

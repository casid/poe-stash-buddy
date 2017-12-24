package org.jusecase.poe.util;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtils {
    public static Path fromResource(String resource) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
        if (url == null) {
            throw new RuntimeException("Resource " + resource + " not found.");
        }

        try {
            return Paths.get(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Resource " + resource + " not found.", e);
        }
    }
}

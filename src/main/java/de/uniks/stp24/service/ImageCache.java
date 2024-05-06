package de.uniks.stp24.service;

import de.uniks.stp24.Main;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import javafx.scene.image.Image;
import java.util.Map;
import java.net.URL;

@Singleton
public class ImageCache {
    private final Map<String, Image> images = new HashMap<>();

    @Inject
    public ImageCache() {
    }

    public Image get(String path) {
        return get(path,true);
    }
    public Image get(String path, boolean background) {
        return images.computeIfAbsent(path, p -> load(p, background));
    }

    private Image load(String path, boolean background) {
        if (!path.startsWith("http://") &&
                !path.startsWith("https://") &&
                !path.startsWith("file://") &&
                !path.startsWith("data:")) {
            // maybe must be modified to path + directory
            final URL url = Main.class.getResource(path);
            if (url != null) {
                path = url.toExternalForm();
            } else {
                System.err.println("Failed to load image :" + path);
                return new Image("https://via.placeholder.com/150?text=Image+not+found",true);
            }
        }
        final String finalPath = path;
        final Image image =  new Image(path, background);
        image.errorProperty().addListener((observable, oldValue, newValue ) -> {
                if (newValue) {
                    System.err.println("Failed to load image: " + finalPath);
                    images.remove(finalPath);
                }
        });
        return image;
    }

}



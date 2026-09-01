package com.coffee.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * Handles picking a picture from disk and copying it into the app's own
 * local picture folder, so item photos (Menu, Inventory) survive even if
 * the user later moves/deletes the original file they picked. Also loads
 * pictures back into JavaFX {@link Image} objects for display.
 *
 * Pictures live under {@code ~/.cafe_management_system/item_pictures/}.
 */
public final class ImageStore {

    private static final Path STORAGE_DIR =
        Paths.get(System.getProperty("user.home"), ".cafe_management_system", "item_pictures");

    private ImageStore() {
    }

    /** Opens a file picker restricted to common image formats. Returns null if cancelled. */
    public static java.io.File chooseImageFile(Window owner) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Item Picture");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
            "Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.webp"));
        return chooser.showOpenDialog(owner);
    }

    /**
     * Copies the given picture into the app's local picture folder under a
     * fresh unique name and returns the absolute path of the stored copy.
     * Throws IOException if the copy fails (e.g. disk full, unreadable file).
     */
    public static String storeImage(java.io.File sourceFile) throws IOException {
        Files.createDirectories(STORAGE_DIR);

        String original = sourceFile.getName();
        String extension = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) extension = original.substring(dot);

        String storedName = UUID.randomUUID() + extension;
        Path destination = STORAGE_DIR.resolve(storedName);

        Files.copy(sourceFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
        return destination.toAbsolutePath().toString();
    }

    /**
     * Loads a picture from a stored path for display. Returns null if the
     * path is missing, blank, or the file can no longer be read (so callers
     * can fall back to a text icon instead of crashing).
     */
    public static Image loadImage(String path) {
        if (path == null || path.isEmpty()) return null;
        try (InputStream in = Files.newInputStream(Paths.get(path))) {
            Image image = new Image(in);
            if (image.isError()) return null;
            return image;
        } catch (IOException | RuntimeException ex) {
            return null;
        }
    }
}
package fr.univrennes.istic.l2gen.application.core.notebook;

import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.nio.file.Files;
import java.util.Base64;

import javax.imageio.ImageIO;

import fr.univrennes.istic.l2gen.application.core.config.Log;

public final class NoteBookImage implements NoteBookValue {

    private File file;
    private Image image;

    public NoteBookImage(File file) {
        this.file = file;
        try {
            this.image = ImageIO.read(file);
        } catch (Exception e) {
            Log.debug("Failed to load image", e);
        }
    }

    public File getPath() {
        return file;
    }

    public Image getImage() {
        return image;
    }

    @Override
    public void exportHTML(StringBuilder html) {
        try {
            String base64 = ImageIO.write(ImageIO.read(file), "png", new ByteArrayOutputStream())
                    ? Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()))
                    : "";
            html.append("<img src=\"data:image/png;base64,").append(base64).append("\" alt=\"Image\" />");
        } catch (Exception e) {
            Log.debug("Failed to export image to HTML", e);
        }
    }

}

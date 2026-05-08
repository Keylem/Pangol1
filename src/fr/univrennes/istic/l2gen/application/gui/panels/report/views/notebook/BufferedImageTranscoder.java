package fr.univrennes.istic.l2gen.application.gui.panels.report.views.notebook;

import java.awt.image.BufferedImage;

import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

public final class BufferedImageTranscoder extends ImageTranscoder {

    private BufferedImage result;

    public BufferedImageTranscoder() {
        super();
    }

    @Override
    public BufferedImage createImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public void writeImage(BufferedImage image, TranscoderOutput output) {
        this.result = image;
    }

    public BufferedImage getResult() {
        return result;
    }
}
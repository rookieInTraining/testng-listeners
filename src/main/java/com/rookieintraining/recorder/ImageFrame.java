package com.rookieintraining.recorder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ImageFrame {
    final String id;
    final byte[] imageBytes;

    public ImageFrame(Image image) {
        this.id = UUID.randomUUID().toString();
        this.imageBytes = convertImageToBytes(image);
    }

    private byte[] convertImageToBytes(Image image) {
        try {
            BufferedImage rgbImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_3BYTE_BGR);
            rgbImage.getGraphics().drawImage(image, 0, 0, null);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(rgbImage, "jpeg", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }
}

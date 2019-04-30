package celeste.comic_community_4_1.miscellaneous;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

public class ThumbnailConverter {

    private static final double LENGTH = 200.0;
    private static final double SQUARE_LENGTH = 400.0;

    private static BufferedImage scale(BufferedImage source, double ratio) {
        int w = (int) (source.getWidth() * ratio);
        int h = (int) (source.getHeight() * ratio);
        w = (w > 1.25 * h) ? (int) (1.25 * h) : w;
        h = (h > 1.25 * w) ? (int) (1.25 * w) : h;
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bi.createGraphics();
        double xScale = (double) w / source.getWidth();
        double yScale = (double) h / source.getHeight();
        AffineTransform at = AffineTransform.getScaleInstance(xScale, yScale);
        g2d.drawRenderedImage(source, at);
        g2d.dispose();
        return bi;
    }

    public static BufferedImage convert(BufferedImage source) {
        double ratio = 1.0, width = source.getWidth(), height = source.getHeight();
        if (width >= LENGTH && height >= LENGTH) {
            if (width > height) {
                ratio = LENGTH / height;
            } else {
                ratio = LENGTH / width;
            }
        }
        return scale(source, ratio);
    }

    public static BufferedImage convertSquare(BufferedImage source) {
        double ratio = 1.0, width = source.getWidth(), height = source.getHeight();

        if (width > height) {
            source = source.getSubimage(0, 0, (int) height, (int) height);
            width = height;
        } else {
            source = source.getSubimage(0, 0, (int) width, (int) width);
        }

        if (width > SQUARE_LENGTH) {
            ratio = SQUARE_LENGTH / width;
        }
        return scale(source, ratio);
    }

    public static String toBase64(BufferedImage source, String type) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(source, type, bos);
        byte[] data = bos.toByteArray();
        return Base64.getEncoder().encodeToString(data);
    }

    public static String[] toBase64(MultipartFile f) throws Exception {
        String type = f.getOriginalFilename().substring(f.getOriginalFilename().lastIndexOf(".") + 1);
        File convFile = new File(f.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(f.getBytes());
        fos.close();

        BufferedImage bImage = ImageIO.read(convFile);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, type, bos);
        final String base64 = Base64.getEncoder().encodeToString(bos.toByteArray());
        bos.close();
        convFile.delete();

        BufferedImage thumbnailBI = convert(bImage);
        bos = new ByteArrayOutputStream();
        ImageIO.write(thumbnailBI, type, bos);
        final String thumbnail = Base64.getEncoder().encodeToString(bos.toByteArray());
        bos.close();
        return new String[]{base64, thumbnail};
    }


}

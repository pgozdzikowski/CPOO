package pl.edu.pw.elka.cpoo.images;

import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

import javax.imageio.ImageIO;

public class MyImage {

    private Image image;
    private BufferedImage bufferedImage;

    private double exposure = 0.7;
    private int[] luminances;

    private int width;
    private int height;

    public MyImage(Image image) {
        this.image = image;
        width = image.getWidth(null);
        height = image.getHeight(null);

        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        bufferedImage.getGraphics().drawImage(image, 0, 0, null);

        initializeLuminances();
    }

    public MyImage(Image image, double exposure) {
        this(image);
        this.exposure = exposure;
    }

    private void initializeLuminances() {
        ColorConvertOp grayScaleOp = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY),
                null);
        BufferedImage imageLum = grayScaleOp.filter(bufferedImage, null);
        int[] lum = imageLum.getData().getPixels(0, 0, width, height, (int[]) null);

        luminances = new int[lum.length / 4];
        for (int i = 0; i < lum.length; i++) {
            luminances[i / 4] <<= 8;
            luminances[i / 4] |= lum[i];
        }
    }

    public int getLuminance(int pixelIndex) {
        int idx = pixelIndex / 4;
        int byt = 3 - pixelIndex % 4;

        return (luminances[idx] >> (byt * Byte.SIZE)) & 0x000000ff;
    }

    public Image getImage() {
        return image;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public double getExposure() {
        return exposure;
    }

    public void setExposure(double exposure) {
        this.exposure = exposure;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}

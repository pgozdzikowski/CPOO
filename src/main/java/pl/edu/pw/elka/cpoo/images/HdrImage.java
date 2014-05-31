package pl.edu.pw.elka.cpoo.images;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JFrame;

import pl.edu.pw.elka.cpoo.views.TabHdr;

public class HdrImage {

    private List<MyImage> images;
    float[] hdrData;

    private int width;
    private int height;

    public HdrImage(List<MyImage> images) {
        if (images.isEmpty() == true) {
            throw new IllegalArgumentException();
        }

        this.images = images;
        calculateExposures();

        MyImage img = images.get(0);
        width = img.getWidth();
        height = img.getHeight();
    }

    // TODO calculate exposure for images
    private void calculateExposures() {
        // TODO EV calculation/extraction
        // It has to be relative exposure -> Most similar to EV = 0 should be 1
        // and more dark images lower, brighter -> higher -|| nie wiem jak to
        // ogarnac, to sa wartosci z toola co liczyl grubo, my uzyjemy alg ze
        // stronki
        try {
            // From tool
            if (true) {
                images.get(0).setExposure(0.29259689966586955);
                images.get(1).setExposure(0.8081045891269992);
                images.get(2).setExposure(1.0);
                images.get(3).setExposure(6.411333121849957);
            }
            // Real
            else {
                images.get(0).setExposure(-4.72);
                images.get(1).setExposure(-1.82);
                images.get(2).setExposure(1.51);
                images.get(3).setExposure(4.09);
            }
        } catch (IndexOutOfBoundsException e) {
            // ignore test
        }
    }

    public void process() {
        createHdr();
    }

    private void createHdr() {
        hdrData = new float[width * height * 3];

        double[] relevances = new double[width * height];
        int color;
        double red, green, blue;
        boolean isPixelSet;
        for (int i = 0; i < width * height; i++) {
            isPixelSet = false;
            for (MyImage img : images) {
                double relevance = 1 - ((Math.abs(img.getLuminance(i) - 127) + 0.5) / 127);
                if (relevance > 0.05) {
                    isPixelSet = true;
                    color = img.getBufferedImage().getRGB(i % width, i / width);
                    red = (color & 0x00ff0000) >> 16;
                    green = (color & 0x0000ff00) >> 8;
                    blue = color & 0x000000ff;
                    hdrData[i * 3 + 0] += red * relevance / img.getExposure();
                    hdrData[i * 3 + 1] += green * relevance / img.getExposure();
                    hdrData[i * 3 + 2] += blue * relevance / img.getExposure();

                    relevances[i] += relevance;
                }
            }
            if (!isPixelSet) {
                MyImage img = images.get(images.size() / 2);
                color = img.getBufferedImage().getRGB(i % width, i / width);
                red = (color & 0x00ff0000) >> 16;
                green = (color & 0x0000ff00) >> 8;
                blue = color & 0x000000ff;
                hdrData[i * 3 + 0] += red / img.getExposure();
                hdrData[i * 3 + 1] += green / img.getExposure();
                hdrData[i * 3 + 2] += blue / img.getExposure();
                relevances[i] = 1;
            }
        }
        for (int i = 0; i < width * height; i++) {
            hdrData[i * 3 + 0] /= relevances[i];
            hdrData[i * 3 + 1] /= relevances[i];
            hdrData[i * 3 + 2] /= relevances[i];
        }

        System.out.println("done");
    }

    public Image getExposedImage(double exposure) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < width * height; i++) {
            int rgb = 0;

            rgb |= Math.min(255, Math.max(0, (int) (hdrData[i * 3 + 0] * exposure))) << 16;
            rgb |= Math.min(255, Math.max(0, (int) (hdrData[i * 3 + 1] * exposure))) << 8;
            rgb |= Math.min(255, Math.max(0, (int) (hdrData[i * 3 + 2] * exposure)));

            image.setRGB(i % width, i / width, rgb);
        }

        return image;
    }

    public void showRawTool() {
        JFrame a = new JFrame("RawTool");
        a.setBounds(0, 0, 500, 500);
        a.setLayout(new BorderLayout());
        a.add(new TabHdr(this), BorderLayout.CENTER);
        a.setVisible(true);
    }

    public float[] getHdrData() {
        return hdrData;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
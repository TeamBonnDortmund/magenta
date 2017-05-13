/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class Color_Transformation implements PlugInFilter {

    String modus = "";
    String farbmodell = "";

    ImagePlus[] imps = null;
    ImageProcessor[] ips = null;

    @Override
    public int setup(String arg, ImagePlus imp) {
        if (showDialog()) {
            return DOES_ALL;
        }

        return DONE;

    }

    @Override
    public void run(ImageProcessor ip) {
        IJ.showStatus("Hello");
        int breite = ip.getWidth();
        int höhe = ip.getHeight();
        int pixel = 0;
        int neuerPixel = 0;
        int R = 0;
        int G = 0;
        int B = 0;
        int Y = 0;
        int U = 0;
        int V = 0;

        if (modus.equals("Transformation ohne Nachbarschaft") && farbmodell.equals("RGB -> YUV")) {
            for (int spalte = 0; spalte < höhe; spalte++) {
                for (int zeile = 0; zeile < breite; zeile++) {
                    pixel = ip.get(zeile, spalte);

                    R = ((pixel & 0xff0000) >> 16);
                    G = ((pixel & 0x00ff00) >> 8);
                    B = (pixel & 0x0000ff);

                    Y = (R + (2 * G) + B) / 4;
                    U = R - G;
                    V = B - G;

                    neuerPixel = Y;
                    neuerPixel = (neuerPixel << 8) + U;
                    neuerPixel = (neuerPixel << 8) + V;
                    ip.putPixel(zeile, spalte, neuerPixel);

                }
            }
        }

        if (modus.equals("Transformation ohne Nachbarschaft") && farbmodell.equals("YUV -> RGB")) {
            for (int spalte = 0; spalte < höhe; spalte++) {
                for (int zeile = 0; zeile < breite; zeile++) {
                    pixel = ip.get(zeile, spalte);

                    Y = ((pixel & 0xff0000) >> 16);
                    U = ((pixel & 0x00ff00) >> 8);
                    V = (pixel & 0x0000ff);

                    G = Y - ((U + V) / 4);
                    R = U+G;
                    B = V+G;

                    neuerPixel = R;
                    neuerPixel = (neuerPixel << 8) + G;
                    neuerPixel = (neuerPixel << 8) + B;
                    ip.putPixel(zeile, spalte, neuerPixel);

                }
            }
        }
    }

    /**
     * Main method for debugging.
     *
     * For debugging, it is convenient to have a method that starts ImageJ,
     * loads an image and calls the plugin, e.g. after setting breakpoints.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        final ImageJ ij = new ImageJ();

    }

    boolean showDialog() {
        String[] farbmodelle = {
            "RGB -> YUV",
            "YUV -> RGB"
        };

        String[] modi = {
            "Transformation ohne Nachbarschaft",
            "Transformation + lineare Prädiktion",
            "Transformation + lineare Prädiktion(Mittelwert)",
            "Transformation + nichtlineare Prädiktion"};

        GenericDialog dialog = new GenericDialog("Color transformation settings");
        dialog.addChoice("Farbmodell", farbmodelle, farbmodelle[0]);
        dialog.addChoice("Modus", modi, modi[0]);

        dialog.showDialog();

        // Der Okay Button wurde ausgelöst
        if (dialog.wasOKed()) {
            farbmodell = dialog.getNextChoice();
            modus = dialog.getNextChoice();
            return true;

        }
        return true;

    }
}

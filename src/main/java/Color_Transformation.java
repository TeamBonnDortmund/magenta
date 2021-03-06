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
import ij.process.ImageProcessor;

public class Color_Transformation implements PlugInFilter {

    String modus = "";
    String farbmodell = "";

    ImagePlus[] imps = null;
    ImageProcessor[] ips = null;

    int addval = 1 << 8;
    int addval2 = 1 << 7;
    int addval3 = (1 << 8) - 1;

    @Override
    public int setup(String arg, ImagePlus imp) {
        if (showDialog()) {
            return DOES_ALL;
        }
        return DONE;
    }

    @Override
    public void run(ImageProcessor ip) {
        int breite = ip.getWidth();
        int hoehe = ip.getHeight();
        int pixel = 0;
        int neuerPixel = 0;
        int R, G, B;
        int Y, U, V;

        if (modus.equals("Transformation ohne Nachbarschaft") && farbmodell.equals("RGB -> YUV")) {
            for (int spalte = 0; spalte < hoehe; spalte++) {
                for (int zeile = 0; zeile < breite; zeile++) {
                    pixel = ip.get(zeile, spalte);

                    R = ((pixel & 0xff0000) >> 16);
                    G = ((pixel & 0x00ff00) >> 8);
                    B = (pixel & 0x0000ff);

                    U = ModuloRange(B - G, -addval2, addval2 - 1, addval);
                    V = ModuloRange(R - G, -addval2, addval2 - 1, addval);
                    Y = G + ((U + V) / 4);

                    neuerPixel = Y;
                    neuerPixel = (neuerPixel << 8) + (V + addval2);
                    neuerPixel = (neuerPixel << 8) + (U + addval2);
                    ip.putPixel(zeile, spalte, neuerPixel);
                }
            }
        }

        if (modus.equals("Transformation ohne Nachbarschaft") && farbmodell.equals("YUV -> RGB")) {
            for (int spalte = 0; spalte < hoehe; spalte++) {
                for (int zeile = 0; zeile < breite; zeile++) {
                    pixel = ip.get(zeile, spalte);

                    Y = ((pixel & 0xff0000) >> 16);
                    U = (pixel & 0x0000ff) - addval2;
                    V = ((pixel & 0x00ff00) >> 8) - addval2;

                    G = Y - ((U + V) / 4);
                    R = ModuloRange(V + G, 0, addval - 1, addval);
                    B = ModuloRange(U + G, 0, addval - 1, addval);

                    neuerPixel = R;
                    neuerPixel = (neuerPixel << 8) + G;
                    neuerPixel = (neuerPixel << 8) + B;
                    ip.putPixel(zeile, spalte, neuerPixel);
                }
            }
        }

        if (modus.equals("Transformation + nichtlineare Praediktion") && farbmodell.equals("RGB -> YUV")) {
            int[][] pixelInput = ip.getIntArray();
            int pixelOutput;
            
            for (int spalte = 0; spalte < hoehe; spalte++) {
                for (int zeile = 0; zeile < breite; zeile++) {
                    
                    pixelOutput = ip.get(zeile, spalte);
                    
                    int a, b, c, x;

                    if (spalte > 0 && zeile > 0) {
                        x = ((pixelInput[zeile][spalte] & 0x00ff00) >> 8);
                        a = ((pixelInput[zeile][spalte-1] & 0x00ff00) >> 8);
                        b = ((pixelInput[zeile-1][spalte] & 0x00ff00) >> 8);
                        c = ((pixelInput[zeile-1][spalte-1] & 0x00ff00) >> 8);
                        
                        int[] tmp ={x,a,b,c};
                        G = x; 
                        
                        for (int i = 1; i < 4; i++) {
                            if(G < tmp[i]){
                                G = tmp[i];
                            }
                        }

                        /*
                        if (c > a && c > b) {
                            if (a < b) {
                                G = a;
                            } else {
                                G = b;
                            }
                        } else if (c < a && c < b) {
                            if (a > b) {
                                G = a;
                            } else {
                                G = b;
                            }
                        } else {
                            G = a-c+b;
                        }
                    } else {
                        G = ((pixelOutput & 0x00ff00) >> 8);
                    }
                     */
                
                    
                    R = ((pixelOutput & 0xff0000) >> 16);
                    B = (pixelOutput & 0x0000ff);
                    

                    U = ModuloRange(B - G, -addval2, addval2 - 1, addval);
                    V = ModuloRange(R - G, -addval2, addval2 - 1, addval);
                    Y = G + ((U + V) / 4);

                    neuerPixel = Y;
                    neuerPixel = (neuerPixel << 8) + (V + addval2);
                    neuerPixel = (neuerPixel << 8) + (U + addval2);
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
            "Transformation + lineare Praediktion",
            "Transformation + lineare Praediktion(Mittelwert)",
            "Transformation + nichtlineare Praediktion"};

        GenericDialog dialog = new GenericDialog("Color transformation settings");
        dialog.addChoice("Farbmodell", farbmodelle, farbmodelle[0]);
        dialog.addChoice("Modus", modi, modi[0]);

        dialog.showDialog();

        // Der Okay Button wurde ausgeloest
        if (dialog.wasOKed()) {
            farbmodell = dialog.getNextChoice();
            modus = dialog.getNextChoice();
            return true;
        }
        return true;
    }

    public static int ModuloRange(int e, int l, int u, int r) {
        if (e > u) {
            e = e - r;
        } else if (e < l) {
            e = e + r;
        }
        return e;
    }
}

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

public class Color_Transformation implements PlugInFilter 
{
	
	@Override
	public int setup(String arg, ImagePlus imp) 
        {
            showDialog();
            
            return DONE;
		
	}

	@Override
	public void run(ImageProcessor ip) 
        {
	}

	/**
	 * Main method for debugging.
	 *
	 * For debugging, it is convenient to have a method that starts ImageJ, loads
	 * an image and calls the plugin, e.g. after setting breakpoints.
	 *
	 * @param args unused
	 */
	public static void main(String[] args) 
        {
		final ImageJ ij = new ImageJ();
                
	}
        
        void showDialog(){
            String[] items = {"Normal", "Entropie"};
            
            GenericDialog dialog = new GenericDialog("Color transformation settings");
            dialog.addChoice("Transformation mode", items, items[0]);
            dialog.showDialog();
            
        }
}

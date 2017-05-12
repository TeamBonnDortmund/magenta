import ij.*;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.gui.GenericDialog;

/* Tilo Strutz
 * 05.10.2016
 * 
*/

public class entropy_ implements PlugInFilter {
	static String title="Entropy";
    private ImagePlus  img;             // Original image

	public int setup(String arg, ImagePlus img) {
        this.img = img;
        if (arg.equals("about"))
        {showAbout(); return DONE;}
		//return DOES_ALL;
		return DOES_8G+DOES_16+DOES_RGB;
	}

  
	public void run(ImageProcessor ip)
	{
		int w = ip.getWidth();
		int h = ip.getHeight();
		int x, y;
		int size, histSize;
		int off = 0, maxVal = 0, val, r, g, b;
		float fVal;
		double p, dVal, entropy = 0;
		GenericDialog gd = new GenericDialog("Entropy");
		int bitDepth = img.getBitDepth();
		
		
		if (bitDepth == 8  || bitDepth == 16)
		{
			/* get offset and maximum value	*/
			for (x = 0; x < w; x++)
			{
				for (y = 0; y < h; y++)
				{
					val = ip.getPixel( x, y);
					if (off > val) off = val;
					if (maxVal < val) maxVal = val;
				}
			}
		}
		else if (bitDepth == 24) /* RGB image*/
		{
			/* get offset and maximum value	*/
			for (x = 0; x < w; x++)
			{
				for (y = 0; y < h; y++)
				{
					val = ip.getPixel( x, y);
					r = ((val & 0xff0000) >> 16);    //R 
					g = ((val & 0x00ff00) >> 8);     //G 
					b =  (val & 0x0000ff);         //B 
					if (off > r) off = r;
					if (off > g) off = g;
					if (off > b) off = b;
					if (maxVal < r) maxVal = r;
					if (maxVal < g) maxVal = g;
					if (maxVal < b) maxVal = b;
				}
			}
		}
		histSize = maxVal - off + 1;
		
		 gd.addMessage("minVal is: " + off );
		 gd.addMessage("maxVal is: " + maxVal );
		
		final long H[] = new long[ histSize];

		size = w * h;
		for (x = 0; x < histSize; x++) H[x] = 0; /* reset histogram	*/
		if (bitDepth == 8  || bitDepth == 16)
		{
			/* determine histogram	*/
			for (x = 0; x < w; x++)
			{
				for (y = 0; y < h; y++)
				{
					val = (int)ip.getPixel( x, y);
					H[val-off]++;
				}
			}
			/* compute entropy	*/
			for (x = 0; x < histSize; x++)
			{
				if ( H[x] != 0)
				{
					p = (double)H[x] / size;
					entropy -= p * java.lang.Math.log(p) / java.lang.Math.log(2.);
				}			
			}
			double scaledVal = java.lang.Math.round(entropy*1000);
			gd.addMessage("Entropy(0) is: " + scaledVal/1000 + " bit/symbol");
		}
		else  if (bitDepth == 24) /* RGB image*/
		{
			final long Hg[] = new long[ histSize];
			final long Hb[] = new long[ histSize];
			double entG=0, entB = 0;
			/* determine histograms	*/
			for (x = 0; x < w; x++)
			{
				for (y = 0; y < h; y++)
				{
					val = ip.getPixel( x, y);
					r = ((val & 0xff0000) >> 16);    //R 
					g =  (val & 0x00ff00) >> 8;     //G 
					b =  (val & 0x0000ff);         //B 
					H[r-off]++;
					Hg[g-off]++;
					Hb[b-off]++;
				}
			}
			/* compute entropy	*/
			
			for (x = 0; x < histSize; x++)
			{
				if ( H[x] != 0)
				{
					p = (double)H[x] / size;
					entropy -= p * java.lang.Math.log(p) / java.lang.Math.log(2.);
				}			
				if ( Hg[x] != 0)
				{
					p = (double)Hg[x] / size;
					entG -= p * java.lang.Math.log(p) / java.lang.Math.log(2.);
				}			
				if ( Hb[x] != 0)
				{
					p = (double)Hb[x] / size;
					entB -= p * java.lang.Math.log(p) / java.lang.Math.log(2.);
				}			
			}
			double scaledVal;
			scaledVal = java.lang.Math.round( entropy*1000);
			gd.addMessage("Entropy(0) is: " + scaledVal/1000 + " bit/symbol");
			scaledVal = java.lang.Math.round( entG*1000);
			gd.addMessage("Entropy(1) is: " + scaledVal/1000 + " bit/symbol");
			scaledVal = java.lang.Math.round( entB*1000);
			gd.addMessage("Entropy(2) is: " + scaledVal/1000 + " bit/symbol");
		}
     //gd.addStringField("is: ", entropy);
      // input of a number gd.addNumericField("entropy: ", entropy, 0);
		 gd.showDialog();
		if (gd.wasCanceled()) return;
 	}
	
  void showAbout() {
    IJ.showMessage("About Entropy...",
                   "It computes the 0-order entropy of the input image.\n"
                   );
  } 
}

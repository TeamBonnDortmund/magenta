import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ij.plugin.PlugIn;

import java.awt.Color;

/********************************************************************
* File..: CSCmod_.java
* Desc..: Color space converter without increased bit depth 
*					with use of Modulo operations
* Author: Alexander Leipnitz
* Date..: 21.07.2014
* 03.07.2014: Added Percentage of Progress
*							Splitted with / without Modulo
* 21.07.2014: Bugfix (Transformation didn't work after displaying 
*							about or error window)
* 05.03.2015: Bugfix (128 instead of 255 was added as offset in RCT
*							without modulo-operation)
*
*	This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/
*********************************************************************/

public class CSCmod_ implements  PlugInFilter {

  /*final static String version = "$Id$";*/
  String title;
  String to;
  String from;
	String mode;
  boolean separated;
  static boolean modulo = true;
  static int numOfModulo = 0;
  
  ImagePlus[] imps = null;
  ImageProcessor[] ips = null;
	
  public void run(ImageProcessor ip) 
	{
	
	int w = ip.getWidth();	// get size of input image
	int h = ip.getHeight();
	int addval   = 1 <<  8;         	
	int addval2  = 1 <<  7;
	int addval3  = (1 << 8) - 1;
	int col, row, p, r, g, b, y, u, v;
	int values[] = new int[3];
	double percent = 0;
	
  if (separated) 
	{
		if ( modulo == true) 
		{
			imps = new ImagePlus[3];
			ips = new ImageProcessor[3];
			ips[0] = new ByteProcessor(w, h);
			ips[1] = new ByteProcessor(w, h);
			ips[2] = new ByteProcessor(w, h);
			String label[] = new String[3];
			if (to.equals("RGB")) 
			{
				label[0] = " (R)";
				label[1] = " (G)";
				label[2] = " (B)";
			}
			else 
			{
				label[0] = " (Y)";
				label[1] = " (U)";
				label[2] = " (V)";
			}
			imps[0] = new ImagePlus(title + " : " + to + label[0] + " with Modulo ", ips[0]);
			imps[1] = new ImagePlus(title + " : " + to + label[1] + " with Modulo ", ips[1]);
			imps[2] = new ImagePlus(title + " : " + to + label[2] + " with Modulo ", ips[2]);
		}
		else 
		{
			imps = new ImagePlus[3];
			ips = new ImageProcessor[3];
			ips[0] = new FloatProcessor(w, h);
			ips[1] = new FloatProcessor(w, h);
			ips[2] = new FloatProcessor(w, h);
			String label[] = new String[3];
			if (to.equals("RGB")) 
			{
				label[0] = " (R)";
				label[1] = " (G)";
				label[2] = " (B)";
			}
			else 
			{
				label[0] = " (Y)";
				label[1] = " (U)";
				label[2] = " (V)";
			}
			imps[0] = new ImagePlus(title + " : " + to + label[0], ips[0]);
			imps[1] = new ImagePlus(title + " : " + to + label[1], ips[1]);
			imps[2] = new ImagePlus(title + " : " + to + label[2], ips[2]);
		}
  }

	y = 0;
	u = 0;
	v = 0;
	
	// convert
	if (modulo == true)
	{
		for (col = 0; col < w; col++) 
		{
			for (row = 0; row < h; row++) 
			{
				// get the pixel
				p = ip.getPixel( col, row);	
				r = ((p & 0xff0000) >> 16);    //R 
				g = ((p & 0x00ff00) >> 8);     //G 
				b =  (p & 0x0000ff);         //B 
			
	/*--------------------------------------------------------------------*/
	/*														forward transformation						 			*/
	/*--------------------------------------------------------------------*/  
				 
				if (from.equals("RGB")) 
				{
					
					if (to.equals("A1_1"))	// RGB to A1_1
					{
						y = g;									
						v = r - g;	
						v = ModuloRange( v, -addval2, addval2-1, addval);
						u = b - g;  
						u = ModuloRange( u, -addval2, addval2-1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
						
					if (to.equals("A1_2")) // RGB to A1_2
					{
						y = g;									
						v = g - r;	
						v = ModuloRange( v, -addval2, addval2-1, addval);
						u = b - r;  
						u = ModuloRange( u, -addval2, addval2-1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
				
					if (to.equals("A1_3")) // RGB to A1_3
					{
						y = g;									
						v = r - b;	
						v = ModuloRange( v, -addval2, addval2-1, addval);
						u = g - b;  
						u = ModuloRange( u, -addval2, addval2-1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = -u + (addval2 - 1);
					}
			 
					if (to.equals("A1_4"))  // RGB to A1_4
					{
						y = g;									
						v = r - g;	
						v = ModuloRange( v, -addval2, addval2-1, addval);
						u = b - g;  
						u = ModuloRange( u, -addval2, addval2-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2-1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
				
					if (to.equals("A1_5")) // RGB to A1_5
					{
						v = g - r;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - r;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = r + v;						
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
			 
					if (to.equals("A1_6"))  // RGB to A1_6
					{
						v = r - b;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + u;							
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
						
					if (to.equals("A1_7")) // RGB to A1_7
					{
						y = g;
						v = b - g;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = v + addval2;
					}
				
					if (to.equals("A1_8")) // RGB to A1_8
					{
						v = g - b;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + v;							
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = -v + (addval2 - 1);
					}
				
					if (to.equals("A1_9")) // RGB to A1_9
					{
						v = b - r;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - r;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = r + u;							
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
				
					if (to.equals("A1_10")) // RGB to A1_10
					{
						y = g;
						v = r - g;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
			 
					if (to.equals("A1_11"))	 // RGB to A1_11 
					{
						v = r - b;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + u;							
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
				
					if (to.equals("A1_12")) // RGB to A1_12
					{
						y = g;
						v = b - g;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = v + addval2;
					}	
				
					if (to.equals("A2_1")) // RGB to A2_1
					{
						y = r;									
						v = r - g;	
						v = ModuloRange( v, -addval2, addval2-1, addval);
						u = b - g;  
						u = ModuloRange( u, -addval2, addval2-1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
				
					// RGB to A2_2
					if (to.equals("A2_2")) 
					{
						y = r;									
						v = g - r;	
						v = ModuloRange( v, -addval2, addval2-1, addval);
						u = b - r;  
						u = ModuloRange( u, -addval2, addval2-1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}  
					
					// RGB to A2_3
					if (to.equals("A2_3")) 
					{
						y = r;									
						v = r - b;	
						v = ModuloRange( v, -addval2, addval2-1, addval);
						u = g - b;  
						u = ModuloRange( u, -addval2, addval2-1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = -u + (addval2 - 1);
					}  
					
					// RGB to A2_4
					if (to.equals("A2_4")) 
					{
						y = r;									
						v = r - g;	
						v = ModuloRange( v, -addval2, addval2-1, addval);
						u = b - g;  
						u = ModuloRange( u, -addval2, addval2-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2-1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}          
					
					// RGB to A2_5
					if (to.equals("A2_5")) 
					{
						y = r;									
						v = g - r;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - r;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}

							// RGB to A2_6
					if (to.equals("A2_6")) 
					{
						v = r - b;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + v;							
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);	  
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}      
					
					// RGB to A2_7
					if (to.equals("A2_7")) 
					{
						v = b - g;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + u;							
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = v + addval2;
					}
					
					// RGB to A2_8
					if (to.equals("A2_8")) 
					{
						v = g - b;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + u;							
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = -v + (addval2 - 1);
					} 
					
					// RGB to A2_9
					if (to.equals("A2_9")) 
					{
						y = r;
						v = b - r;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - r;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
					 
					if (to.equals("A2_10")) // RGB to A2_10
					{
						v = r - g;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + v;							
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A2_11
					if (to.equals("A2_11")) 
					{
						v = r - b;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + v;							
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A2_12
					if (to.equals("A2_12")) 
					{
						v = b - g;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + u;							
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = v + addval2;
					}
					
					// RGB to A3_1
					if (to.equals("A3_1")) 
					{
						y = b;									
						v = r - g;	
						v = ModuloRange( v, -addval2, addval2-1, addval);
						u = b - g;  
						u = ModuloRange( u, -addval2, addval2-1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A3_2
					if (to.equals("A3_2")) 
					{
						y = b;									
						v = g - r;	
						v = ModuloRange( v, -addval2, addval2-1, addval);
						u = b - r;  
						u = ModuloRange( u, -addval2, addval2-1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
					
					// RGB to A3_3
					if (to.equals("A3_3")) 
					{
						y = b;									
						v = r - b;	
						v = ModuloRange( v, -addval2, addval2-1, addval);
						u = g - b;  
						u = ModuloRange( u, -addval2, addval2-1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = -u + (addval2 - 1);
					}
					
					// RGB to A3_4
					if (to.equals("A3_4")) 
					{
						y = b;									
						v = r - g;	
						v = ModuloRange( v, -addval2, addval2-1, addval);
						u = b - g;  
						u = ModuloRange( u, -addval2, addval2-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2-1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A3_5
					if (to.equals("A3_5")) 
					{								
						v = g - r;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - r;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = r + u;						
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
					
					// RGB to A3_6
					if (to.equals("A3_6")) 
					{
						y = b;
						v = r - b;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A3_7
					if (to.equals("A3_7")) 
					{
						v = b - g;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + v;						
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = v + addval2;
					}
					
					// RGB to A3_8
					if (to.equals("A3_8")) 
					{
						y = b;
						v = g - b;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = -v + (addval2 - 1);
					}
					
					// RGB to A3_9
					if (to.equals("A3_9")) 
					{
						v = b - r;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - r;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = r + v;						
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
					
					// RGB to A3_10
					if (to.equals("A3_10")) 
					{
						v = r - g;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + u;						
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A3_11
					if (to.equals("A3_11")) 
					{
						y = b; 
						v = r - b;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);					
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A3_12
					if (to.equals("A3_12")) 
					{
						v = b - g;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + v;						
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = v + addval2;
					}
					
					// RGB to A4_1
					if (to.equals("A4_1")) 
					{
						v = r - g;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - g;
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + (v >> 1);
						y = ModuloRange( y, 0, addval-1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A4_2
					if (to.equals("A4_2")) 
					{
						v = g - r;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - r;
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = r + (v >> 1);
						y = ModuloRange( y, 0, addval-1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
					
					// RGB to A4_3
					if (to.equals("A4_3")) 
					{
						v = r - b;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - b;
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + ((v + u) >> 1);
						y = ModuloRange( y, 0, addval-1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = -u + (addval2 - 1);
					}
					
					// RGB to A4_4
					if (to.equals("A4_4")) 
					{
						v = r - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + (v >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A4_5
					if (to.equals("A4_5")) 
					{
						v = g - r;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - r;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = r + (v >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
					
					// RGB to A4_6
					if (to.equals("A4_6")) 
					{
						v = r - b;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + ((v + u) >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A4_7
					if (to.equals("A4_7")) 
					{
						v = b - g;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + (u >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = v + addval2;
					}
					
					// RGB to A4_8
					if (to.equals("A4_8")) 
					{
						v = g - b;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + ((v + u) >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = -v + (addval2 - 1);
					}
					
					// RGB to A4_9
					if (to.equals("A4_9")) 
					{
						v = b - r;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - r;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = r + (u >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
					
					// RGB to A4_10
					if (to.equals("A4_10")) 
					{
						v = r - g;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + (v >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A4_11
					if (to.equals("A4_11"))
					{
						v = r - b;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + ((v + u) >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A4_12
					if (to.equals("A4_12")) 
					{
						v = b - g;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + (u >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = v + addval2;
					}
					
					// RGB to A5_1
					if (to.equals("A5_1")) 
					{
						v = r - g;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - g;
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + (u >> 1);
						y = ModuloRange( y, 0, addval-1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A5_2
					if (to.equals("A5_2")) 
					{ 
						v = g - r;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - r;
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = r + ((u + v) >> 1);
						y = ModuloRange( y, 0, addval-1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
					
					// RGB to A5_3
					if (to.equals("A5_3")) 
					{
						v = r - b;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - b;
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + (u >> 1);
						y = ModuloRange( y, 0, addval-1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = -u + (addval2 - 1);
					}
					
					// RGB to A5_4
					if (to.equals("A5_4")) 
					{
						v = r - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + (u >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A5_5
					if (to.equals("A5_5")) 
					{
						v = g - r;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - r;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = r + ((v + u) >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
					
					// RGB to A5_6
					if (to.equals("A5_6")) 
					{
						v = r - b;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + (u >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A5_7
					if (to.equals("A5_7")) 
					{
						v = b - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + (v >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = v + addval2;
					}
					
					// RGB to A5_8
					if (to.equals("A5_8")) 
					{
						v = g - b;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + (v >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = -v + (addval2 - 1);
					}
					
					// RGB to A5_9
					if (to.equals("A5_9")) 
					{
						v = b - r;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - r;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = r + ((v + u) >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
					
					// RGB to A5_10
					if (to.equals("A5_10")) 
					{
						v = r - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + (u >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A5_11
					if (to.equals("A5_11")) 
					{
						v = r - b;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + (u >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A5_12
					if (to.equals("A5_12")) 
					{
						v = b - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + (v >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = v + addval2;
					}
					
					// RGB to A6_1
					if (to.equals("A6_1")) 
					{
						v = r - g;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - g;
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + ((v + u) >> 1);
						y = ModuloRange( y, 0, addval-1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A6_2
					if (to.equals("A6_2")) 
					{
						v = g - r;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - r;
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = r + (u >> 1);
						y = ModuloRange( y, 0, addval-1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
					
					// RGB to A6_3
					if (to.equals("A6_3")) 
					{
						v = r - b;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - b;
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + (v >> 1);
						y = ModuloRange( y, 0, addval-1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = -u + (addval2 - 1);
					}
					
					// RGB to A6_4
					if (to.equals("A6_4")) 
					{
						v = r - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + ((v + u) >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A6_5
					if (to.equals("A6_5")) 
					{
						v = g - r;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - r;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = r + (u >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
					
					// RGB to A6_6
					if (to.equals("A6_6")) 
					{
						v = r - b;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + (v >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A6_7
					if (to.equals("A6_7")) 
					{
						v = b - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + ((v + u) >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = v + addval2;
					}
					
					// RGB to A6_8
					if (to.equals("A6_8")) 
					{
						v = g - b;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + (u >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = -v + (addval2 - 1);
					}
					
					// RGB to A6_9
					if (to.equals("A6_9")) 
					{
						v = b - r;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - r;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = r + (v >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
					
					// RGB to A6_10
					if (to.equals("A6_10")) 
					{
						v = r - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + ((v + u) >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A6_11
					if (to.equals("A6_11")) 
					{
						v = r - b;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + (v >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A6_12
					if (to.equals("A6_12")) 
					{
						v = b - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + ((v + u) >> 1);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = v + addval2;
					}
					
					// RGB to A7_1
					if (to.equals("A7_1")) 
					{
						v = r - g;  
						v = ModuloRange( v, -addval2, addval2-1, addval);
						u = b - g;	
						u = ModuloRange( u, -addval2, addval2-1, addval);
						y = g + ((u + v) >> 2);	
						y = ModuloRange( y, 0, addval-1, addval);	
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A7_2
					if (to.equals("A7_2")) 
					{
						v = g - r;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - r;
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = r + (((v << 1) + u) >> 2);
						y = ModuloRange( y, 0, addval-1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
					
					// RGB to A7_3
					if (to.equals("A7_3")) 
					{
						v = r - b;	
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - b;
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + ((v + (u << 1)) >> 2);
						y = ModuloRange( y, 0, addval-1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = -u + (addval2 - 1);
					}
					
					// RGB to A7_4
					if (to.equals("A7_4")) 
					{
						v = r - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + ((v + u) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A7_5
					if (to.equals("A7_5")) 
					{
						v = g - r;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - r;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = r + (((v << 1) + u) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
					
					// RGB to A7_6
					if (to.equals("A7_6")) 
					{
						v = r - b;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + ((v + (u << 1)) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A7_7
					if (to.equals("A7_7")) 
					{
						v = b - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + ((v + u) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = v + addval2;
					}
					
					// RGB to A7_8
					if (to.equals("A7_8")) 
					{
						v = g - b;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + (((v << 1) + u) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = -v + (addval2 - 1);
					}
					
					// RGB to A7_9
					if (to.equals("A7_9")) 
					{
						v = b - r;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - r;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = r + ((v + (u << 1)) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
					
					// RGB to A7_10
					if (to.equals("A7_10")) 
					{
						v = r - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + ((v + u) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A7_11
					if (to.equals("A7_11")) 
					{
						v = r - b;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + ((v + (u << 1)) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A7_12
					if (to.equals("A7_12")) 
					{
						v = b - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + ((v + u) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = v + addval2;
					}
					
					// RGB to A8_1
					if (to.equals("A8_1")) 
					{
						v = r - g;  
						v = ModuloRange( v, -addval2, addval2-1, addval);
						u = b - g;	
						u = ModuloRange( u, -addval2, addval2-1, addval);
						y = g + ((u + (v << 1)) >> 2);	
						y = ModuloRange( y, 0, addval-1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A8_2
					if (to.equals("A8_2")) 
					{
						v = g - r;  
						v = ModuloRange( v, -addval2, addval2-1, addval);
						u = b - r;	
						u = ModuloRange( u, -addval2, addval2-1, addval);
						y = r + ((u + v) >> 2);	
						y = ModuloRange( y, 0, addval-1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
					
					// RGB to A8_3
					if (to.equals("A8_3")) 
					{
						v = r - b;  
						v = ModuloRange( v, -addval2, addval2-1, addval);
						u = g - b;	
						u = ModuloRange( u, -addval2, addval2-1, addval);
						y = b + ((u + (v << 1)) >> 2);	
						y = ModuloRange( y, 0, addval-1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = -u + (addval2 - 1);
					}
					
					// RGB to A8_4
					if (to.equals("A8_4")) 
					{
						v = r - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + (((v << 1) + u) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A8_5
					if (to.equals("A8_5")) 
					{
						v = g - r;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - r;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = r + ((v + u) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
					
					// RGB to A8_6
					if (to.equals("A8_6")) 
					{
						v = r - b;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + (((v << 1) + u) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A8_7
					if (to.equals("A8_7")) 
					{
						v = b - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + ((v + (u << 1)) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = v + addval2;
					}
					
					// RGB to A8_8
					if (to.equals("A8_8")) 
					{
						v = g - b;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + ((v + (u << 1)) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = -v + (addval2 - 1);
					}
					
					// RGB to A8_9
					if (to.equals("A8_9")) 
					{
						v = b - r;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - r;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = r + ((v + u) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
					
					// RGB to A8_10
					if (to.equals("A8_10")) 
					{
						v = r - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + (((v << 1) + u) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A8_11
					if (to.equals("A8_11")) 
					{
						v = r - b;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + (((v << 1) + u) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A8_12
					if (to.equals("A8_12")) 
					{
						v = b - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + ((v + (u << 1)) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = v + addval2;
					}
					
					// RGB to A9_1
					if (to.equals("A9_1")) 
					{
						v = r - g;  
						v = ModuloRange( v, -addval2, addval2-1, addval);
						u = b - g;	
						u = ModuloRange( u, -addval2, addval2-1, addval);
						y = g + ((v + (u << 1)) >> 2);	
						y = ModuloRange( y, 0, addval-1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A9_2
					if (to.equals("A9_2")) 
					{
						v = g - r;  
						v = ModuloRange( v, -addval2, addval2-1, addval);
						u = b - r;	
						u = ModuloRange( u, -addval2, addval2-1, addval);
						y = r + ((v + (u << 1)) >> 2);	
						y = ModuloRange( y, 0, addval-1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
					
					// RGB to A9_3
					if (to.equals("A9_3")) 
					{
						v = r - b;  
						v = ModuloRange( v, -addval2, addval2-1, addval);
						u = g - b;	
						u = ModuloRange( u, -addval2, addval2-1, addval);
						y = b + ((v + u) >> 2);	
						y = ModuloRange( y, 0, addval-1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = -u + (addval2 - 1);
					}
					
					// RGB to A9_4
					if (to.equals("A9_4")) 
					{
						v = r - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + (((u << 1) + v) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A9_5
					if (to.equals("A9_5")) 
					{
						v = g - r;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - r;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = r + (((u << 1) + v) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
					
					// RGB to A9_6
					if (to.equals("A9_6")) 
					{
						v = r - b;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + ((v + u) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A9_7
					if (to.equals("A9_7")) 
					{
						v = b - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + (((v << 1) + u) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = v + addval2;
					}
					
					// RGB to A9_8
					if (to.equals("A9_8")) 
					{
						v = g - b;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + ((v + u) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = -v + (addval2 - 1);
					}
					
					// RGB to A9_9
					if (to.equals("A9_9")) 
					{
						v = b - r;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - r;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = r + (((v << 1) + u) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u + addval2;
					}
					
					// RGB to A9_10
					if (to.equals("A9_10")) 
					{
						v = r - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + (((u << 1) + v) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A9_11
					if (to.equals("A9_11")) 
					{
						v = r - b;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g - b;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = b + ((u + v) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u + addval2;
					}
					
					// RGB to A9_12
					if (to.equals("A9_12")) 
					{
						v = b - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r - g;  
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						y = g + (((v << 1) + u) >> 2);				
						y = ModuloRange( y, 0, addval-1, addval);
						u = u - (v >> 1);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = v + addval2;
					}
					
					// RGB to B1_1
					if (to.equals("B1_1")) 
					{
						y = g;									
						v = r - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b;
						values[0] = y;
						values[1] = v + addval2;
						values[2] = u;
					}
					
					// RGB to B1_2
					if (to.equals("B1_2")) 
					{
						y = g;									
						v = b - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r;
						values[0] = u;
						values[1] = y;
						values[2] = v + addval2;
					}
					
					// RGB to B2_1
					if (to.equals("B2_1")) 
					{
						y = r;									
						v = g - r;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = b;
						values[0] = y;
						values[1] = -v + (addval2 - 1);
						values[2] = u;
					}
					
					// RGB to B2_3
					if (to.equals("B2_3")) 
					{
						y = r;									
						v = b - r;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g;
						values[0] = y;
						values[1] = u;
						values[2] = -v + (addval2 - 1);
					}
					
					// RGB to B3_2
					if (to.equals("B3_2")) 
					{
						y = b;									
						v = g - b;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = r;
						values[0] = u;
						values[1] = -v + (addval2 - 1);
						values[2] = y;
					}
					
					// RGB to B3_3
					if (to.equals("B3_3")) 
					{
						y = b;									
						v = r - b;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						u = g;
						values[0] = u;
						values[1] = v + addval2;
						values[2] = y;
					}
					
					// RGB to B4_1
					if (to.equals("B4_1")) 
					{
						u = r - g;
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						v = b;									
						y = g + (u >> 1);			
						y = ModuloRange( y, 0, addval - 1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = v;
					}
					
					// RGB to B5_2
					if (to.equals("B5_2")) 
					{
						u = g - b;
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						v = r;									
						y = b + (u >> 1);			
						y = ModuloRange( y, 0, addval - 1, addval);
						values[0] = v;
						values[1] = y;
						values[2] = -u + (addval2 - 1);
					}
					
					// RGB to B6_3
					if (to.equals("B6_3")) 
					{
						u = r - b;
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						v = g;									
						y = b + (u >> 1);			
						y = ModuloRange( y, 0, addval-1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = v;
					}
					
					// RGB to PEI09
					if (to.equals("PEI09")) 
					{
						u = b - ((87*r + 169*g) >> 8);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						v = r - g;
						v = ModuloRange( v, -addval2, addval2 - 1, addval);
						y  = g + ((29*u + 86*v) >> 8);
						y = ModuloRange( y, 0, addval-1, addval);
						values[0] = y;
						values[1] = u + addval2;
						values[2] = v + addval2;
					}
			 
				}
	/*------------------------------------------------------------------------------------------*/
	/*																																													*/
	/*														back transformation																	   				*/
	/*																																													*/
	/*------------------------------------------------------------------------------------------*/   
			 else if (to.equals("RGB")) 
			 {
					r = ((p & 0xff0000) >> 16);  // Y
					g = ((p & 0x00ff00) >> 8);   // U
					b =  (p & 0x0000ff);         // V  
			 
					if (from.equals("A1_1")) // A1_1 to RGB a1=a2=0
					{
						y = r;
						v = g - addval2;
						u = b - addval2;
						g = y;
						r = v + g;
						r = ModuloRange( r, 0, addval - 1, addval);
						b = u + g;
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b;
					}
					
					if (from.equals("A1_2")) // A1_2 to RGB a1=1 a2=0, R<=>G
					{
						y = r;
						v = -g + (addval2 - 1);
						u = b - addval2;
						r = y - v;
						r = ModuloRange( r, 0, addval - 1, addval);
						g = v + r;
						g = ModuloRange( g, 0, addval - 1, addval);
						b = u + r;
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b;  
					}
						
					if (from.equals("A1_3")) // A1_3 to RGB a1=0 a2=1, B<=>G
					{
						y = r; 
						v = g - addval2;
						u = -b + (addval2 - 1); 
						b = y - u;
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b;   
					}
					
					if (from.equals("A1_4")) // A1_4 to RGB a1=a2=0 e=1/4
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y;
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 	  
					}
					
					if (from.equals("A1_5")) // A1_5 to RGB a1=1 a2=0, e=1/4, R<=>G
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						u = u + (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						r = y - v;
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (v + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (u + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					} 
					
					if (from.equals("A1_6")) // A1_6 to RGB a1=0 a2=1, e=1/4, B<=>G
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - u;
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 	  
					}
					
					if (from.equals("A1_7"))  // A1_7 to RGB a1=a2=0 e=1/4 B<=>R
					{
						y = r; 
						u = g - addval2;
						v = b - addval2;
						u = u + (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y;
						b = (v + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (u + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 	  
					}
					
					if (from.equals("A1_8")) // A1_8 to RGB a1=1 a2=0, e=1/4, R=>G=>B=>R
					{
						y = r; 
						u = g - addval2;  
						v = -b + (addval2 - 1); 
						u = u + (v >> 2);	
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - v;
						b = ModuloRange( b, 0, addval - 1, addval);
						g = (v + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (u + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 	  
					}
					
					// A1_9 to RGB a1=0 a2=1, e=1/4, R=>B=>G=>R
					if (from.equals("A1_9")) 
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						u = u + (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						r = y - u;
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (v + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						g = (u + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 	  
					}
					
					// A1_10 to RGB a1=a2=0 e=1/2
					if (from.equals("A1_10")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 1);		
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y;
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 	  
					}
					
					// A1_11 to RGB a1=a2=0 e=1/2
					if (from.equals("A1_11")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 1);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - u;
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b;   
					}
					
					// A1_12 to RGB a1=a2=0 e=1/2  R<=>B
					if (from.equals("A1_12")) 
					{
						y = r; 
						u = g - addval2;
						v = b - addval2;
						u = u + (v >> 1);	
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y;
						b = (v + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (u + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A2_1 to RGB a1=1 a2=0
					if (from.equals("A2_1")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						g = y - v;
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 	  
					}
					
					// A2_2 to RGB a1=1 a2=0 R<=>G
					if (from.equals("A2_2")) 
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						r = y;
						g = (v + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (u + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b;  
					}
					
					// A2_3 to RGB a1=1 a2=0, B<=>G
					if (from.equals("A2_3")) 
					{
						y = r; 
						v = g - addval2;
						u = -b + (addval2 - 1);  
						b = y - v;
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b;   
					}
					
					// A2_4 to RGB a1=1 a2=0, e=1/4
					if (from.equals("A2_4")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - v;
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);  
						values[0] = r;
						values[1] = g;
						values[2] = b; 	  
					}
					
					// A2_5 to RGB a1=1 a2=0 e=1/4 R<=>G
					if (from.equals("A2_5"))
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						u = u + (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						r = y;
						g = (v + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (u + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b;   
					}
					
					// A2_6 to RGB a1=1 a2=0, e=1/4, B<=>G
					if (from.equals("A2_6")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - v;
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b;  
					}
					
					// A2_7 to RGB a1=1 a2=0, e=1/4, B<=>R
					if (from.equals("A2_7")) 
					{
						y = r; 
						u = g - addval2;
						v = b - addval2;
						u = u + (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - u;
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (v + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (u + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A2_8 to RGB a1=0 a2=1, e=1/4, R=>G=>B=>R
					if (from.equals("A2_8")) 
					{
						y = r; 
						u = g - addval2;
						v = -b + (addval2 - 1); 
						u = u + (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - u;
						b = ModuloRange( b, 0, addval - 1, addval);
						g = (v + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (u + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A2_9 to RGB a1=a2=0 e=1/4  R=>B=>G=>R
					if (from.equals("A2_9")) 
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						u = u + (v >> 2);			
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						r = y;
						b = (v + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						g = (u + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b;   
					}
					
					// A2_10 to RGB a1=1 a2=0, e=1/2
					if (from.equals("A2_10")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 1);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - v;
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 	  
					}
					
					// A2_11 to RGB a1=1 a2=0, e=1/2, B<=>G
					if (from.equals("A2_11")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 1);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - v;
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A2_12 to RGB a1=0 a2=1, e=1/2, B<=>R
					if (from.equals("A2_12")) 
					{
						y = r; 
						u = g - addval2;
						v = b - addval2;
						u = u + (v >> 1);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - u;
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (v + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (u + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A3_1 to RGB a1=a2=0 e=1/2 B<=>G
					if (from.equals("A3_1")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						g = y - u;			
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A3_2 to RGB a1=0 a2=1, R<=>G
					if (from.equals("A3_2")) 
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						r = y - u;
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (v + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (u + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A3_3 to RGB a1=a2=0 B<=>G
					if (from.equals("A3_3")) 
					{
						y = r; 
						v = g - addval2;
						u = -b + (addval2 - 1); 
						b = y;
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A3_4 to RGB a1=0 a2=1, e=1/4
					if (from.equals("A3_4")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - u;
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A3_5 to RGB a1=0 a2=1, e=1/4, R<=>G
					if (from.equals("A3_5")) 
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						u = u + (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						r = y - u;
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (v + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (u + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A3_6 to RGB a1=a2=0 e=1/4 B<=>G
					if (from.equals("A3_6")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y;
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A3_7 to RGB a1=1 a2=0, e=1/4, B<=>R
					if (from.equals("A3_7")) 
					{
						y = r; 
						u = g - addval2;
						v = b - addval2;
						u = u + (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - v;
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (v + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (u + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A3_8 to RGB a1=a2=0 e=1/4 R=>G=>B=>R
					if (from.equals("A3_8")) 
					{
						y = r; 
						u = g - addval2;
						v = -b + (addval2 - 1); 
						u = u + (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y;
						g = (v + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (u + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A3_9 to RGB a1=1 a2=0, e=1/4, R=>B=>G=>R
					if (from.equals("A3_9")) 
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						u = u + (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						r = y - v;
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (v + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						g = (u + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A3_10 to RGB a1=0 a2=1, e=1/2
					if (from.equals("A3_10")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 1);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - u;
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A3_11 to RGB a1=a2=0 e=1/2 B<=>G
					if (from.equals("A3_11")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 1);			
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y;
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A3_12 to RGB a1=1 a2=0, e=1/2, B<=>R
					if (from.equals("A3_12")) 
					{
						y = r; 
						u = g - addval2;
						v = b - addval2;
						u = u + (v >> 1);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - v;
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (v + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (u + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_1 to RGB a1=1/2 a2=0
					if (from.equals("A4_1")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						g = y - (v >> 1);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_2 to RGB a1=1/2 a2=0, R<=>G
					if (from.equals("A4_2")) 
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						r = y - (v >> 1);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (v + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (u + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_3 to RGB a1=1/2 a2=0, B<=>G
					if (from.equals("A4_3")) 
					{
						y = r; 
						v = g - addval2;
						u = -b + (addval2 - 1); 
						b = y - ((v + u) >> 1);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_4 to RGB a1=1/2 a2=0, e=1/4
					if (from.equals("A4_4")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - (v >> 1);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_5 to RGB a1=1/2 a2=0, e=1/4, R<=>G
					if (from.equals("A4_5")) 
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						u = u + (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						r = y - (v >> 1);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (v + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (u + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_6 to RGB a1=1/2 a2=0, e=1/4, B<=>G
					if (from.equals("A4_6")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - ((v + u) >> 1);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_7 to RGB a2=1/2 a1=0, e=1/4, B<=>R
					if (from.equals("A4_7")) 
					{
						y = r; 
						u = g - addval2;
						v = b - addval2;
						u = u + (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - (u >> 1);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (v + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (u + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_8 to RGB a1=a2=1/2, e=1/4, R=>G=>B=>R
					if (from.equals("A4_8")) 
					{
						y = r; 
						u = g - addval2;
						v = -b + (addval2 - 1); 
						u = u + (v >> 2);	
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - ((v + u) >> 1);
						b = ModuloRange( b, 0, addval - 1, addval);
						g = (v + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (u + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_9 to RGB a2=1/2 a1=0, e=1/4, R=>B=>G=>R
					if (from.equals("A4_9")) 
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						u = u + (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);			
						r = y - (u >> 1);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (v + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_10 to RGB a1=1/2 a2=0, e=1/2
					if (from.equals("A4_10")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 1);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - (v >> 1);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_11 to RGB a1=a2=1/2, e=1/2, B<=>G
					if (from.equals("A4_11")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 1);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - ((v + u) >> 1);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_12 to RGB a2=1/2 a1=0, e=1/2, B<=>R
					if (from.equals("A4_12")) 
					{
						y = r; 
						u = g - addval2;
						v = b - addval2;
						u = u + (v >> 1);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - (u >> 1);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (v + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (u + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_1 to RGB a1=1/2 a2=0, e=1/2, B<=>R
					if (from.equals("A5_1")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						g = y - (u >> 1);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_2 to RGB a1=a2=1/2, R<=>G
					if (from.equals("A5_2")) 
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						r = y - ((v + u) >> 1);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (v + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (u + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_3 to RGB a2=1/2 a1=0, B<=>G
					if (from.equals("A5_3")) 
					{
						y = r; 
						v = g - addval2;
						u = -b + (addval2 - 1); 
						b = y - (u >> 1);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_4 to RGB a2=1/2 a1=0, e=1/4
					if (from.equals("A5_4")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - (u >> 1);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_5 to RGB a1=a2=1/2, e=1/4, R<=>G
					if (from.equals("A5_5")) 
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						u = u + (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						r = y - ((v + u) >> 1);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (v + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (u + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_6 to RGB a2=1/2 a1=0, e=1/4, B<=>G
					if (from.equals("A5_6")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - (u >> 1);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_7 to RGB a1=1/2 a2=0, e=1/4, B<=>R
					if (from.equals("A5_7")) 
					{
						y = r; 
						u = g - addval2;
						v = b - addval2;
						u = u + (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - (v >> 1);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (v + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (u + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_8 to RGB a1=1/2 a2=0, e=1/4, R=>G=>B=>R
					if (from.equals("A5_8")) 
					{
						y = r; 
						u = g - addval2;
						v = -b + (addval2 - 1); 
						u = u + (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - (v >> 1);
						b = ModuloRange( b, 0, addval - 1, addval);
						g = (v + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (u + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_9 to RGB a1=a2=1/2, e=1/4, R=>B=>G=>R
					if (from.equals("A5_9")) 
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						u = u + (v >> 2);	
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						r = y - ((v + u) >> 1);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (v + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						g = (u + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_10 to RGB a2=1/2 a1=0, e=1/2
					if (from.equals("A5_10")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 1);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - (u >> 1);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_11 to RGB a2=1/2 a1=0, e=1/2, B<=>G
					if (from.equals("A5_11")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 1);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - (u >> 1);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_12 to RGB a1=1/2 a2=0, e=1/2, B<=>R
					if (from.equals("A5_12"))
					{
						y = r; 
						u = g - addval2;
						v = b - addval2;
						u = u + (v >> 1);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - (v >> 1);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (v + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (u + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A6_1 to RGB a1=a2=1/2
					if (from.equals("A6_1")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						g = y - ((v+u)>>1);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A6_2 to RGB a2=1/2 a1=0, R<=>G
					if (from.equals("A6_2")) 
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						r = y - (u >> 1);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (v + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (u + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A6_3 to RGB a1=1/2 a2=0, B<=>G
					if (from.equals("A6_3")) 
					{
						y = r; 
						v = g - addval2;
						u = -b + (addval2 - 1); 
						b = y - (v >> 1);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A6_4 to RGB a1=a2=1/2, e=1/4
					if (from.equals("A6_4")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 2);	
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - ((v + u) >> 1);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A6_5 to RGB a2=1/2 a1=0, e=1/4, R<=>G
					if (from.equals("A6_5")) 
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						u = u + (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						r = y - (u >> 1);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (v + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (u + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A6_6 to RGB a1=1/2 a2=0, e=1/4, B<=>G
					if (from.equals("A6_6")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - (v >> 1);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A6_7 to RGB a1=a2=1/2, e=1/4, B<=>R
					if (from.equals("A6_7")) 
					{
						y = r; 
						u = g - addval2;
						v = b - addval2;
						u = u + (v >> 2);	
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - ((v + u) >> 1);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (v + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (u + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A6_8 to RGB a2=1/2 a1=0, e=1/4, R=>G=>B=>R
					if (from.equals("A6_8")) 
					{
						y = r; 
						u = g - addval2;
						v = -b + (addval2 - 1); 
						u = u + (v >> 2);	
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - (u >> 1);
						b = ModuloRange( b, 0, addval - 1, addval);
						g = (v + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (u + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A6_9 to RGB a1=1/2 a2=0, e=1/4, R=>B=>G=>R
					if (from.equals("A6_9")) 
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						u = u + (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						r = y - (v >> 1);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (v + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						g = (u + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A6_10 to RGB a1=a2=1/2, e=1/2
					if (from.equals("A6_10")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 1);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - ((v + u) >> 1);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A6_11 to RGB a1=1/2 a2=0, e=1/2, B<=>G
					if (from.equals("A6_11")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 1);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - (v >> 1);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A6_12 to RGB a1=a2=1/2, e=1/2, B<=>R
					if (from.equals("A6_12")) 
					{
						y = r; 
						u = g - addval2;
						v = b - addval2;
						u = u + (v >> 1);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - ((v + u) >> 1);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (v + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (u + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A7_1 to RGB a1=a2=1/4  == YUV
					if (from.equals("A7_1")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						g = y - ((v + u) >> 2);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = v + (int)g;
						r = ModuloRange( r, 0, addval - 1, addval);
						b = u + (int)g;
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A7_2 to RGB a1=1/2 a2=1/4, R<=>G
					if (from.equals("A7_2")) 
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						r = y - (((v << 1) + u) >> 2);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (v + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (u + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A7_3 to RGB a1=1/4 a2=1/2, B<=>G
					if (from.equals("A7_3"))
					{
						y = r; 
						v = g - addval2;
						u = -b + (addval2 - 1); 
						b = y - (((u << 1) + v) >> 2);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A7_4 to RGB a1=a2=1/4, e=1/4
					if (from.equals("A7_4")) 
					{
						y = r;
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - ((v + u) >> 2);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A7_5 to RGB a1=1/2 a2=1/4, e=1/4, R<=>G
					if (from.equals("A7_5")) 
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						u = u + (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						r = y - (((v << 1) + u) >> 2);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (v + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (u + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A7_6 to RGB a1=1/4 a2=1/2, e=1/4, B<=>G
					if (from.equals("A7_6")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - (((u << 1) + v) >> 2);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A7_7 to RGB a1=a2=1/4, e=1/4, B<=>R
					if (from.equals("A7_7")) 
					{
						y = r; 
						u = g - addval2;
						v = b - addval2;
						u = u + (v >> 2);	
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - ((v + u) >> 2);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (u + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (v + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A7_8 to RGB a1=1/2 a2=1/4, e=1/4, R=>G=>B=>R
					if (from.equals("A7_8")) 
					{
						y = r; 
						u = g - addval2;
						v = -b + (addval2 - 1); 
						u = u + (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - (((v << 1) + u) >> 2);
						b = ModuloRange( b, 0, addval - 1, addval);
						g = (v + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (u + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A7_9 to RGB a1=1/4 a2=1/2, e=1/4, R=>B=>G=>R
					if (from.equals("A7_9")) 
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						u = u + (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						r = y - (((u << 1) + v) >> 2);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (v + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						g = (u + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A7_10 to RGB a1=a2=1/4, e=1/2
					if (from.equals("A7_10")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 1);	
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - ((v + u) >> 2);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A7_11 to RGB a1=1/4 a2=1/2, e=1/2, B<=>G
					if (from.equals("A7_11")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 1);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - (((u << 1) + v) >> 2);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A7_12 to RGB a1=a2=1/4, e=1/2, B<=>R
					if (from.equals("A7_12")) 
					{
						y = r; 
						u = g - addval2;
						v = b - addval2;
						u = u + (v >> 1);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - ((v + u) >> 2);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (v + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (u + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_1 to RGB a1=1/2 a2=1/4
					if (from.equals("A8_1")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						g = y - (((v << 1) + u) >> 2);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_2 to RGB a1=a2=1/4, R<=>G
					if (from.equals("A8_2")) 
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						r = y - ((v + u) >> 2);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (v + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (u + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_3 to RGB a1=1/2 a2=1/4, B<=>G
					if (from.equals("A8_3")) 
					{
						y = r; 
						v = g - addval2;
						u = -b + (addval2 - 1); 
						b = y - (((v << 1) + u) >> 2);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_4 to RGB a1=1/2 a2=1/4, e=1/4
					if (from.equals("A8_4")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - (((v << 1) + u) >> 2);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_5 to RGB a1=a2=1/4, e=1/4, R<=>G
					if (from.equals("A8_5")) 
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						u = u + (v >> 2);	
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						r = y - ((v + u) >> 2);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (v + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (u + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_6 to RGB a1=1/2 a2=1/4, e=1/4, B<=>G
					if (from.equals("A8_6")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - (((v << 1) + u) >> 2);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_7 to RGB a1=1/4 a2=1/2, e=1/4, B<=>R
					if (from.equals("A8_7")) 
					{
						y = r; 
						u = g - addval2;
						v = b - addval2;
						u = u + (v >> 2);	
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - (((u << 1) + v) >> 2);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (v + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (u + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_8 to RGB a1=1/4 a2=1/2, e=1/4, R=>G=>B=>R
					if (from.equals("A8_8")) 
					{
						y = r; 
						u = g - addval2;
						v = -b + (addval2 - 1); 
						u = u + (v >> 2);	
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - (((u << 1) + v) >> 2);
						b = ModuloRange( b, 0, addval - 1, addval);
						g = (v + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (u + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_9 to RGB a1=a2=1/4, e=1/4, R=>B=>G=>R
					if (from.equals("A8_9")) 
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						u = u + (v >> 2);	
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						r = y - ((v + u) >> 2);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (v + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						g = (u + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_10 to RGB a1=1/2 a2=1/4, e=1/2
					if (from.equals("A8_10")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 1);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - (((v << 1) + u) >> 2);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_11 to RGB a1=1/2 a2=1/4, e=1/2, B<=>G
					if (from.equals("A8_11")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 1);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - (((v << 1) + u) >> 2);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_12 to RGB a1=1/2 a2=1/4, e=1/2, B<=>R
					if (from.equals("A8_12")) 
					{
						y = r; 
						u = g - addval2;
						v = b - addval2;
						u = u + (v >> 1);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - (((u << 1) + v) >> 2);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (v + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (u + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A9_1 to RGB a1=1/4 a2=1/2
					if (from.equals("A9_1")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						g = y - (((u << 1) + v) >> 2);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A9_2 to RGB a1=1/4 a2=1/2, R<=>G
					if (from.equals("A9_2")) 
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						r = y - (((u << 1) + v) >> 2);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (v + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (u + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A9_3 to RGB a1=a2=1/4, B<=>G
					if (from.equals("A9_3")) 
					{
						y = r; 
						v = g - addval2;
						u = -b + (addval2 - 1); 
						b = y - ((v + u) >> 2);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A9_4 to RGB a1=1/4 a2=1/2, e=1/4
					if (from.equals("A9_4")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 2);						
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - (((u << 1) + v) >> 2);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A9_5 to RGB a1=1/4 a2=1/2, e=1/4, R<=>G
					if (from.equals("A9_5")) 
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						u = u + (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						r = y - (((u << 1) + v) >> 2);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (v + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (u + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A9_6 to RGB a1=a2=1/4, e=1/4, B<=>G
					if (from.equals("A9_6")) 
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - ((v + u) >> 2);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					if (from.equals("A9_7")) // A9_7 to RGB a1=1/2 a2=1/4, e=1/4, B<=>R
					{
						y = r; 
						u = g - addval2;
						v = b - addval2;
						u = u + (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - (((v << 1) + u) >> 2);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (v + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (u + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					if (from.equals("A9_8"))  // A9_8 to RGB a1=a2=1/4, e=1/4, R=>G=>B=>R
					{
						y = r; 
						u = g - addval2;
						v = -b + (addval2 - 1); 
						u = u + (v >> 2);	
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - ((v + u) >> 2);
						b = ModuloRange( b, 0, addval - 1, addval);
						g = (v + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (u + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					if (from.equals("A9_9")) // A9_9 to RGB a1=1/2 a2=1/4, e=1/4, R=>B=>G=>R
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b - addval2;
						u = u + (v >> 2);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						r = y - (((v << 1) + u) >> 2);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (v + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						g = (u + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					 
					if (from.equals("A9_10")) // A9_10 to RGB a1=1/4 a2=1/2, e=1/2
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 1);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - (((u << 1) + v) >> 2);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = (u + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					if (from.equals("A9_11")) // A9_11 to RGB a1=a2=1/4, e=1/2, B<=>G
					{
						y = r; 
						v = g - addval2;
						u = b - addval2;
						u = u + (v >> 1);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						b = y - ((v + u) >> 2);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					if (from.equals("A9_12")) // A9_12 to RGB a1=1/2 a2=1/4, e=1/2, B<=>R
					{
						y = r; 
						u = g - addval2;
						v = b - addval2;
						u = u + (v >> 1);
						u = ModuloRange( u, -addval2, addval2 - 1, addval);
						g = y - (((v << 1) + u) >> 2);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = (v + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = (u + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					if (from.equals("B1_1")) // B1_1 to RGB
					{
						y = r; 
						v = g - addval2;
						u = b;
						g = y;
						r = (v + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						b = u;
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					} 
					
					if (from.equals("B1_2")) // B1_2 to RGB
					{
						u = r; 
						y = g;
						v = b - addval2;
						g = y;
						b = (v + g);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = u;
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					if (from.equals("B2_1")) // B2_1 to RGB
					{
						y = r; 
						v = -g + (addval2 - 1); 
						u = b;
						r = y;
						g = (v + r);
						g = ModuloRange( g, 0, addval - 1, addval);
						b = u;
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
								
					if (from.equals("B2_3")) // B2_3 to RGB
					{
						y = r; 
						u = g;
						v = -b + (addval2 - 1); 
						r = y;
						g = u;
						b = (v + r);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
							
					if (from.equals("B3_2")) // B3_2 to RGB
					{
						u = r; 
						v = -g + (addval2 - 1); 
						y = b;
						b = y;
						g = (v + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = u;
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					if (from.equals("B3_3")) // B3_3 to RGB
					{
						u = r; 
						v = g - addval2;
						y = b;
						b = y;
						r = (v + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						g = u;
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					if (from.equals("B4_1")) // B4_1 to RGB
					{
						y = r; 
						u = g - addval2;
						v = b;
						g = y - (u >> 1);
						b = v;
						r = (u + g);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}  
					
					if (from.equals("B5_2")) // B5_2 to RGB 
					{
						v = r; 
						y = g ;
						u = -b + (addval2 - 1); 
						b = y - (u >> 1);
						b = ModuloRange( b, 0, addval - 1, addval);
						r = v;
						g = (u + b);
						g = ModuloRange( g, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b;  
					}
						 
					if (from.equals("B6_3"))  // B6_3 to RGB
					{
						y = r; 
						u = g - addval2;
						v = b;
						b = y - (u >> 1);
						b = ModuloRange( b, 0, addval - 1, addval);
						g = v;
						r = (u + b);
						r = ModuloRange( r, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b;    
					}
						
					if (from.equals("PEI09")) // PEI09 to RGB
					{
						y = r;
						u = g - addval2;
						v = b - addval2;
						g  = y - ((29*u + 86*v) >> 8);
						g = ModuloRange( g, 0, addval - 1, addval);
						r = v + g;
						r = ModuloRange( r, 0, addval - 1, addval);
						b = u + ((87*r + 169*g) >> 8);
						b = ModuloRange( b, 0, addval - 1, addval);
						values[0] = r;
						values[1] = g;
						values[2] = b;  
					}
					
				}
				else 
				{
					values[0] = r;
					values[1] = g;
					values[2] = b;
				}
				
				// put the pixel back
				if (separated) 
				{
					ips[0].putPixelValue( col, row, values[0]);
					ips[1].putPixelValue( col, row, values[1]);
					ips[2].putPixelValue( col, row, values[2]);
				}
				else 
				{
					p = values[0];
					p = (p << 8) + values[1];
					p = (p << 8) + values[2];
					ip.putPixel(col, row, p);
	
				}
			}
			percent = ((col+1)*100)/w;
			IJ.showStatus("CSCmod_ is loading, may take a while. Status: "+ percent + "%");
		}
	}	
	
	
/******************************************************************************************/
/*                                                                                    	  */
/*                               MODULO == FALSE																			  	*/
/*																																												*/
/******************************************************************************************/
	
	else 
	{
	  	
	/*--------------------------------------------------------------------*/
	/*														forward transformation						 			*/
	/*--------------------------------------------------------------------*/  
		for (col = 0; col < w; col++) 
		{
			for (row = 0; row < h; row++) 
			{
								// get the pixel
				p = ip.getPixel( col, row);	
				r = ((p & 0xff0000) >> 16);    //R 
				g = ((p & 0x00ff00) >> 8);     //G 
				b =  (p & 0x0000ff);         //B 		 
				if (from.equals("RGB")) 
				{
					
					if (to.equals("A1_1"))	// RGB to A1_1
					{
						y = g;									
						v = r - g;			
						u = b - g;  
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
						
					if (to.equals("A1_2")) // RGB to A1_2
					{
						y = g;									
						v = g - r;	
						u = b - r;  
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
				
					if (to.equals("A1_3")) // RGB to A1_3
					{
						y = g;									
						v = r - b;				
						u = g - b;  	
						values[0] = y;
						values[1] = v + addval3;
						values[2] = -u + addval3;
					}
			 
					if (to.equals("A1_4"))  // RGB to A1_4
					{
						y = g;									
						v = r - g;	
						u = b - g;  
						u = u - (v >> 2);						
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
				
					if (to.equals("A1_5")) // RGB to A1_5
					{
						v = g - r;	
						u = b - r;  
						y = r + v;						
						u = u - (v >> 2);						
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
			 
					if (to.equals("A1_6"))  // RGB to A1_6
					{
						v = r - b;	
						u = g - b;  
						y = b + u;							
						u = u - (v >> 2);						
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
						
					if (to.equals("A1_7")) // RGB to A1_7
					{
						y = g;
						v = b - g;	
						u = r - g;  
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = u + addval3;
						values[2] = v + addval3;
					}
				
					if (to.equals("A1_8")) // RGB to A1_8
					{
						v = g - b;						
						u = r - b;  					
						y = b + v;													
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = u + addval3;
						values[2] = -v + addval3;
					}
				
					if (to.equals("A1_9")) // RGB to A1_9
					{
						v = b - r;				
						u = g - r;  
						y = r + u;							
						u = u - (v >> 2);
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
				
					if (to.equals("A1_10")) // RGB to A1_10
					{
						y = g;
						v = r - g;			
						u = b - g;  			
						u = u - (v >> 1);									
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
			 
					if (to.equals("A1_11"))	 // RGB to A1_11 
					{
						v = r - b;	
						u = g - b;  
						y = b + u;							
						u = u - (v >> 1);								
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
				
					if (to.equals("A1_12")) // RGB to A1_12
					{ 
						y = g;
						v = b - g;	
						u = r - g;  
						u = u - (v >> 1);							
						values[0] = y;
						values[1] = u + addval3;
						values[2] = v + addval3;
					}	
				
					if (to.equals("A2_1")) // RGB to A2_1
					{
						y = r;									
						v = r - g;	
						u = b - g;  	
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
				
					// RGB to A2_2
					if (to.equals("A2_2")) 
					{
						y = r;									
						v = g - r;	
						u = b - r;  
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}  
					
					// RGB to A2_3
					if (to.equals("A2_3")) 
					{
						y = r;									
						v = r - b;				
						u = g - b;  	
						values[0] = y;
						values[1] = v + addval3;
						values[2] = -u + addval3;
					}  
					
					// RGB to A2_4
					if (to.equals("A2_4")) 
					{
						y = r;									
						v = r - g;		
						u = b - g;  	
						u = u - (v >> 2);							
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}          
					
					// RGB to A2_5
					if (to.equals("A2_5")) 
					{
						y = r;									
						v = g - r;		
						u = b - r;  
						u = u - (v >> 2);						
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}

							// RGB to A2_6
					if (to.equals("A2_6")) 
					{
						v = r - b;	
						u = g - b;  
						y = b + v;											
						u = u - (v >> 2);											  
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}      
					
					// RGB to A2_7
					if (to.equals("A2_7")) 
					{
						v = b - g;						
						u = r - g;  					
						y = g + u;											
					  u = u - (v >> 2);										
						values[0] = y;
						values[1] = u + addval3;
						values[2] = v + addval3;
					}
					
					// RGB to A2_8
					if (to.equals("A2_8")) 
					{
						v = g - b;						
						u = r - b;  				
						y = b + u;											
						u = u - (v >> 2);										
						values[0] = y;
						values[1] = u + addval3;
						values[2] = -v + addval3;
					} 
					
					// RGB to A2_9
					if (to.equals("A2_9")) 
					{
						y = r;
						v = b - r;					
						u = g - r; 					
						u = u - (v >> 2);									
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
					 
					if (to.equals("A2_10")) // RGB to A2_10
					{
						v = r - g;						
						u = b - g;  					
						y = g + v;											
						u = u - (v >> 1);										
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A2_11
					if (to.equals("A2_11")) 
					{
						v = r - b;						
						u = g - b;  					
						y = b + v;											
						u = u - (v >> 1);										
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A2_12
					if (to.equals("A2_12")) 
					{
						v = b - g;						
						u = r - g;  				
						y = g + u;											
						u = u - (v >> 1);										
						values[0] = y;
						values[1] = u + addval3;
						values[2] = v + addval3;
					}
					
					// RGB to A3_1
					if (to.equals("A3_1")) 
					{
						y = b;									
						v = r - g;					
						u = b - g;  				
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A3_2
					if (to.equals("A3_2")) 
					{
						y = b;									
						v = g - r;					
						u = b - r;  				
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A3_3
					if (to.equals("A3_3")) 
					{
						y = b;									
						v = r - b;						
						u = g - b;  					
						values[0] = y;
						values[1] = v + addval3;
						values[2] = -u + addval3;
					}
					
					// RGB to A3_4
					if (to.equals("A3_4")) 
					{
						y = b;									
						v = r - g;				
						u = b - g;  			
						u = u - (v >> 2);										
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A3_5
					if (to.equals("A3_5")) 
					{								
						v = g - r;				
						u = b - r;  			
						y = r + u;									
						u = u - (v >> 2);									
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A3_6
					if (to.equals("A3_6")) 
					{
						y = b;
						v = r - b;					
						u = g - b;  			
						u = u - (v >> 2);									
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A3_7
					if (to.equals("A3_7")) 
					{
						v = b - g;					
						u = r - g;  				
						y = g + v;									
						u = u - (v >> 2);									
						values[0] = y;
						values[1] = u + addval3;
						values[2] = v + addval3;
					}
					
					// RGB to A3_8
					if (to.equals("A3_8")) 
					{
						y = b;
						v = g - b;					
						u = r - b;  				
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = u + addval3;
						values[2] = -v + addval3;
					}
					
					// RGB to A3_9
					if (to.equals("A3_9")) 
					{
						v = b - r;						
						u = g - r;  					
						y = r + v;											
						u = u - (v >> 2);										
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A3_10
					if (to.equals("A3_10")) 
					{
						v = r - g;				
						u = b - g;  			
						y = g + u;										
						u = u - (v >> 1);										
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A3_11
					if (to.equals("A3_11")) 
					{
						y = b; 
						v = r - b;					
						u = g - b;  									
						u = u - (v >> 1);										
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A3_12
					if (to.equals("A3_12")) 
					{
						v = b - g;					
						u = r - g;  			
						y = g + v;											
						u = u - (v >> 1);											
						values[0] = y;
						values[1] = u + addval3;
						values[2] = v + addval3;
					}
					
					// RGB to A4_1
					if (to.equals("A4_1")) 
					{
						v = r - g;				
						u = b - g;
						y = g + (v >> 1);				
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A4_2
					if (to.equals("A4_2")) 
					{
						v = g - r;					
						u = b - r;					
						y = r + (v >> 1);					
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A4_3
					if (to.equals("A4_3")) 
					{
						v = r - b;		
						u = g - b;		
						y = b + ((v + u) >> 1);		
						values[0] = y;
						values[1] = v + addval3;
						values[2] = -u + addval3;
					}
					
					// RGB to A4_4
					if (to.equals("A4_4")) 
					{
						v = r - g;			
						u = b - g; 				
						y = g + (v >> 1);								
						u = u - (v >> 2);										
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A4_5
					if (to.equals("A4_5")) 
					{
						v = g - r;						
						u = b - r;  				
						y = r + (v >> 1);								
						u = u - (v >> 2);										
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A4_6
					if (to.equals("A4_6")) 
					{
						v = r - b;						
						u = g - b;  					
						y = b + ((v + u) >> 1);									
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A4_7
					if (to.equals("A4_7")) 
					{
						v = b - g;						
						u = r - g;  					
						y = g + (u >> 1);									
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = u + addval3;
						values[2] = v + addval3;
					}
					
					// RGB to A4_8
					if (to.equals("A4_8")) 
					{
						v = g - b;						
						u = r - b; 						
						y = b + ((v + u) >> 1);									
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = u + addval3;
						values[2] = -v + addval3;
					}
					
					// RGB to A4_9
					if (to.equals("A4_9")) 
					{
						v = b - r;						
						u = g - r;  					
						y = r + (u >> 1);									
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A4_10
					if (to.equals("A4_10")) 
					{
						v = r - g;						
						u = b - g;  					
						y = g + (v >> 1);									
						u = u - (v >> 1);											
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A4_11
					if (to.equals("A4_11"))
					{
						v = r - b;						
						u = g - b;  					
						y = b + ((v + u) >> 1);									
						u = u - (v >> 1);											
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A4_12
					if (to.equals("A4_12")) 
					{
						v = b - g;						
						u = r - g;  				
						y = g + (u >> 1);								
						u = u - (v >> 1);										
						values[0] = y;
						values[1] = u + addval3;
						values[2] = v + addval3;
					}
					
					// RGB to A5_1
					if (to.equals("A5_1")) 
					{
						v = r - g;						
						u = b - g;					
						y = g + (u >> 1);					
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A5_2
					if (to.equals("A5_2")) 
					{ 
						v = g - r;						
						u = b - r;					
						y = r + ((u + v) >> 1);					
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A5_3
					if (to.equals("A5_3")) 
					{
						v = r - b;						
						u = g - b;						
						y = b + (u >> 1);						
						values[0] = y;
						values[1] = v + addval3;
						values[2] = -u + addval3;
					}
					
					// RGB to A5_4
					if (to.equals("A5_4")) 
					{
						v = r - g;					
						u = b - g;  						
						y = g + (u >> 1);										
						u = u - (v >> 2);										
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A5_5
					if (to.equals("A5_5")) 
					{
						v = g - r;					
						u = b - r;  					
						y = r + ((v + u) >> 1);									
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A5_6
					if (to.equals("A5_6")) 
					{
						v = r - b;					
						u = g - b;  						
						y = b + (u >> 1);										
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A5_7
					if (to.equals("A5_7")) 
					{
						v = b - g;						
						u = r - g;  						
						y = g + (v >> 1);									
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = u + addval3;
						values[2] = v + addval3;
					}
					
					// RGB to A5_8
					if (to.equals("A5_8")) 
					{
						v = g - b;						
						u = r - b;  					
						y = b + (v >> 1);										
						u = u - (v >> 2);												
						values[0] = y;
						values[1] = u + addval3;
						values[2] = -v + addval3;
					}
					
					// RGB to A5_9
					if (to.equals("A5_9")) 
					{
						v = b - r;					
						u = g - r;  					
						y = r + ((v + u) >> 1);										
						u = u - (v >> 2);												
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A5_10
					if (to.equals("A5_10")) 
					{
						v = r - g;						
						u = b - g;  						
						y = g + (u >> 1);									
						u = u - (v >> 1);											
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A5_11
					if (to.equals("A5_11")) 
					{
						v = r - b;		
						u = g - b;  			
						y = b + (u >> 1);							
						u = u - (v >> 1);										
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A5_12
					if (to.equals("A5_12")) 
					{
						v = b - g;			
						u = r - g;  				
						y = g + (v >> 1);								
						u = u - (v >> 1);										
						values[0] = y;
						values[1] = u + addval3;
						values[2] = v + addval3;
					}
					
					// RGB to A6_1
					if (to.equals("A6_1")) 
					{
						v = r - g;					
						u = b - g;				
						y = g + ((v + u) >> 1);				
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A6_2
					if (to.equals("A6_2")) 
					{
						v = g - r;					
						u = b - r;				
						y = r + (u >> 1);				
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A6_3
					if (to.equals("A6_3")) 
					{
						v = r - b;				
						u = g - b;						
						y = b + (v >> 1);
						values[0] = y;
						values[1] = v + addval3;
						values[2] = -u + addval3;
					}
					
					// RGB to A6_4
					if (to.equals("A6_4")) 
					{
						v = r - g;					
						u = b - g;  						
						y = g + ((v + u) >> 1);										
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A6_5
					if (to.equals("A6_5")) 
					{
						v = g - r;						
						u = b - r;  					
						y = r + (u >> 1);										
						u = u - (v >> 2);												
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A6_6
					if (to.equals("A6_6")) 
					{
						v = r - b;						
						u = g - b;  						
						y = b + (v >> 1);										
						u = u - (v >> 2);												
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A6_7
					if (to.equals("A6_7")) 
					{
						v = b - g;						
						u = r - g;  						
						y = g + ((v + u) >> 1);										
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = u + addval3;
						values[2] = v + addval3;
					}
					
					// RGB to A6_8
					if (to.equals("A6_8")) 
					{
						v = g - b;						
						u = r - b;  						
						y = b + (u >> 1);										
						u = u - (v >> 2);												
						values[0] = y;
						values[1] = u + addval3;
						values[2] = -v + addval3;
					}
					
					// RGB to A6_9
					if (to.equals("A6_9")) 
					{
						v = b - r;						
						u = g - r;  						
						y = r + (v >> 1);										
						u = u - (v >> 2);												
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A6_10
					if (to.equals("A6_10")) 
					{
						v = r - g;						
						u = b - g;  						
						y = g + ((v + u) >> 1);									
						u = u - (v >> 1);											
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A6_11
					if (to.equals("A6_11")) 
					{
						v = r - b;					
						u = g - b;  					
						y = b + (v >> 1);										
						u = u - (v >> 1);												
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A6_12
					if (to.equals("A6_12")) 
					{
						v = b - g;					
						u = r - g;  			
						y = g + ((v + u) >> 1);									
						u = u - (v >> 1);										
						values[0] = y;
						values[1] = u + addval3;
						values[2] = v + addval3;
					}
					
					// RGB to A7_1
					if (to.equals("A7_1")) 
					{
						v = r - g;  				
						u = b - g;					
						y = g + ((u + v) >> 2);						
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A7_2
					if (to.equals("A7_2")) 
					{
						v = g - r;						
						u = b - r;					
						y = r + (((v << 1) + u) >> 2);					
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A7_3
					if (to.equals("A7_3")) 
					{
						v = r - b;						
						u = g - b;					
						y = b + ((v + (u << 1)) >> 2);					
						values[0] = y;
						values[1] = v + addval3;
						values[2] = -u + addval3;
					}
					
					// RGB to A7_4
					if (to.equals("A7_4")) 
					{
						v = r - g;					
						u = b - g;  						
						y = g + ((v + u) >> 2);									
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A7_5
					if (to.equals("A7_5")) 
					{
						v = g - r;					
						u = b - r;  						
						y = r + (((v << 1) + u) >> 2);										
						u = u - (v >> 2);												
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A7_6
					if (to.equals("A7_6")) 
					{
						v = r - b;						
						u = g - b;  					
						y = b + ((v + (u << 1)) >> 2);									
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A7_7
					if (to.equals("A7_7")) 
					{
						v = b - g;					
						u = r - g;  					
						y = g + ((v + u) >> 2);									
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = u + addval3;
						values[2] = v + addval3;
					}
					
					// RGB to A7_8
					if (to.equals("A7_8")) 
					{
						v = g - b;					
						u = r - b;  						
						y = b + (((v << 1) + u) >> 2);									
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = u + addval3;
						values[2] = -v + addval3;
					}
					
					// RGB to A7_9
					if (to.equals("A7_9")) 
					{
						v = b - r;					
						u = g - r;  					
						y = r + ((v + (u << 1)) >> 2);										
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A7_10
					if (to.equals("A7_10")) 
					{
						v = r - g;					
						u = b - g;  
						y = g + ((v + u) >> 2);				
						u = u - (v >> 1);											
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A7_11
					if (to.equals("A7_11")) 
					{
						v = r - b;						
						u = g - b;  						
						y = b + ((v + (u << 1)) >> 2);										
						u = u - (v >> 1);											
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A7_12
					if (to.equals("A7_12")) 
					{
						v = b - g;					
						u = r - g;  					
						y = g + ((v + u) >> 2);									
						u = u - (v >> 1);											
						values[0] = y;
						values[1] = u + addval3;
						values[2] = v + addval3;
					}
					
					// RGB to A8_1
					if (to.equals("A8_1")) 
					{
						v = r - g;  					
						u = b - g;						
						y = g + ((u + (v << 1)) >> 2);						
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A8_2
					if (to.equals("A8_2")) 
					{
						v = g - r; 						
						u = b - r;						
						y = r + ((u + v) >> 2);						
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A8_3
					if (to.equals("A8_3")) 
					{
						v = r - b;  					
						u = g - b;							
						y = b + ((u + (v << 1)) >> 2);							
						values[0] = y;
						values[1] = v + addval3;
						values[2] = -u + addval3;
					}
					
					// RGB to A8_4
					if (to.equals("A8_4")) 
					{
						v = r - g;					
						u = b - g;  						
						y = g + (((v << 1) + u) >> 2);									
						u = u - (v >> 2);												
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A8_5
					if (to.equals("A8_5")) 
					{
						v = g - r;
						u = b - r; 						
						y = r + ((v + u) >> 2);									
						u = u - (v >> 2);										
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A8_6
					if (to.equals("A8_6")) 
					{
						v = r - b;				
						u = g - b;  				
						y = b + (((v << 1) + u) >> 2);								
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A8_7
					if (to.equals("A8_7")) 
					{
						v = b - g;					
						u = r - g;  					
						y = g + ((v + (u << 1)) >> 2);									
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = u + addval3;
						values[2] = v + addval3;
					}
					
					// RGB to A8_8
					if (to.equals("A8_8")) 
					{
						v = g - b;					
						u = r - b;  				
						y = b + ((v + (u << 1)) >> 2);									
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = u + addval3;
						values[2] = -v + addval3;
					}
					
					// RGB to A8_9
					if (to.equals("A8_9")) 
					{
						v = b - r;						
						u = g - r;  						
						y = r + ((v + u) >> 2);									
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A8_10
					if (to.equals("A8_10")) 
					{
						v = r - g;					
						u = b - g;  						
						y = g + (((v << 1) + u) >> 2);									
						u = u - (v >> 1);											
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A8_11
					if (to.equals("A8_11")) 
					{
						v = r - b;					
						u = g - b;  					
						y = b + (((v << 1) + u) >> 2);										
						u = u - (v >> 1);											
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A8_12
					if (to.equals("A8_12")) 
					{
						v = b - g;					
						u = r - g;  					
						y = g + ((v + (u << 1)) >> 2);									
						u = u - (v >> 1);											
						values[0] = y;
						values[1] = u + addval3;
						values[2] = v + addval3;
					}
					
					// RGB to A9_1
					if (to.equals("A9_1")) 
					{
						v = r - g;  					
						u = b - g;						
						y = g + ((v + (u << 1)) >> 2);						
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A9_2
					if (to.equals("A9_2")) 
					{
						v = g - r;  						
						u = b - r;				
						y = r + ((v + (u << 1)) >> 2);						
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A9_3
					if (to.equals("A9_3")) 
					{
						v = r - b;  
						u = g - b;						
						y = b + ((v + u) >> 2);						
						values[0] = y;
						values[1] = v + addval3;
						values[2] = -u + addval3;
					}
					
					// RGB to A9_4
					if (to.equals("A9_4")) 
					{
						v = r - g;					
						u = b - g;  					
						y = g + (((u << 1) + v) >> 2);									
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A9_5
					if (to.equals("A9_5")) 
					{
						v = g - r;						
						u = b - r;  						
						y = r + (((u << 1) + v) >> 2);									
						u = u - (v >> 2);												
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A9_6
					if (to.equals("A9_6")) 
					{
						v = r - b;					
						u = g - b;  					
						y = b + ((v + u) >> 2);									
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A9_7
					if (to.equals("A9_7")) 
					{
						v = b - g;					
						u = r - g;  					
						y = g + (((v << 1) + u) >> 2);										
						u = u - (v >> 2);												
						values[0] = y;
						values[1] = u + addval3;
						values[2] = v + addval3;
					}
					
					// RGB to A9_8
					if (to.equals("A9_8")) 
					{
						v = g - b;					
						u = r - b;  					
						y = b + ((v + u) >> 2);										
						u = u - (v >> 2);												
						values[0] = y;
						values[1] = u + addval3;
						values[2] = -v + addval3;
					}
					
					// RGB to A9_9
					if (to.equals("A9_9")) 
					{
						v = b - r;					
						u = g - r;  					
						y = r + (((v << 1) + u) >> 2);									
						u = u - (v >> 2);											
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A9_10
					if (to.equals("A9_10")) 
					{
						v = r - g;						
						u = b - g;  						
						y = g + (((u << 1) + v) >> 2);										
						u = u - (v >> 1);											
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A9_11
					if (to.equals("A9_11")) 
					{
						v = r - b;					
						u = g - b;  					
						y = b + ((u + v) >> 2);									
						u = u - (v >> 1);											
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u + addval3;
					}
					
					// RGB to A9_12
					if (to.equals("A9_12")) 
					{
						v = b - g;					
						u = r - g;  					
						y = g + (((v << 1) + u) >> 2);									
						u = u - (v >> 1);												
						values[0] = y;
						values[1] = u + addval3;
						values[2] = v + addval3;
					}
					
					// RGB to B1_1
					if (to.equals("B1_1")) 
					{
						y = g;									
						v = r - g;			
						u = b;
						values[0] = y;
						values[1] = v + addval3;
						values[2] = u;
					}
					
					// RGB to B1_2
					if (to.equals("B1_2")) 
					{
						y = g;									
						v = b - g;				
						u = r;
						values[0] = u;
						values[1] = y;
						values[2] = v + addval3;
					}
					
					// RGB to B2_1
					if (to.equals("B2_1")) 
					{
						y = r;									
						v = g - r;				
						u = b;
						values[0] = y;
						values[1] = -v + addval3;
						values[2] = u;
					}
					
					// RGB to B2_3
					if (to.equals("B2_3")) 
					{
						y = r;									
						v = b - r;					
						u = g;
						values[0] = y;
						values[1] = u;
						values[2] = -v + addval3;
					}
					
					// RGB to B3_2
					if (to.equals("B3_2")) 
					{
						y = b;									
						v = g - b;					
						u = r;
						values[0] = u;
						values[1] = -v + addval3;
						values[2] = y;
					}
					
					// RGB to B3_3
					if (to.equals("B3_3")) 
					{
						y = b;									
						v = r - b;					
						u = g;
						values[0] = u;
						values[1] = v + addval3;
						values[2] = y;
					}
					
					// RGB to B4_1
					if (to.equals("B4_1")) 
					{
						u = r - g;			
						v = b;									
						y = g + (u >> 1);						
						values[0] = y;
						values[1] = u + addval3;
						values[2] = v;
					}
					
					// RGB to B5_2
					if (to.equals("B5_2")) 
					{
						u = g - b;				
						v = r;									
						y = b + (u >> 1);							
						values[0] = v;
						values[1] = y;
						values[2] = -u + addval3;
					}
					
					// RGB to B6_3
					if (to.equals("B6_3")) 
					{
						u = r - b;			
						v = g;									
						y = b + (u >> 1);							
						values[0] = y;
						values[1] = u + addval3;
						values[2] = v;
					}
					
					// RGB to PEI09
					if (to.equals("PEI09")) 
					{
						u = b - ((87*r + 169*g) >> 8);			
						v = r - g;				
						y  = g + ((29*u + 86*v) >> 8);			
						values[0] = y;
						values[1] = u + addval3;
						values[2] = v + addval3;
					}
			 
				}
	/*------------------------------------------------------------------------------------------*/
	/*																																													*/
	/*														back transformation																	   				*/
	/*																																													*/
	/*------------------------------------------------------------------------------------------*/   
			 else if (to.equals("RGB")) 
			 {
					r = ((p & 0xff0000) >> 16);  // Y
					g = ((p & 0x00ff00) >> 8);   // U
					b =  (p & 0x0000ff);         // V  
			 
					if (from.equals("A1_1")) // A1_1 to RGB a1=a2=0
					{
						y = r;
						v = g - addval3;
						u = b - addval3;
						g = y;
						r = v + g;				
						b = u + g;
						values[0] = r;
						values[1] = g;
						values[2] = b;
					}
					
					if (from.equals("A1_2")) // A1_2 to RGB a1=1 a2=0, R<=>G
					{
						y = r;
						v = -g + addval3;
						u = b - addval3;
						r = y - v;				
						g = v + r;				
						b = u + r;					
						values[0] = r;
						values[1] = g;
						values[2] = b;  
					}
						
					if (from.equals("A1_3")) // A1_3 to RGB a1=0 a2=1, B<=>G
					{
						y = r; 
						v = g - addval3;
						u = -b + addval3; 
						b = y - u;					
						r = (v + b);					
						g = (u + b);					
						values[0] = r;
						values[1] = g;
						values[2] = b;   
					}
					
					if (from.equals("A1_4")) // A1_4 to RGB a1=a2=0 e=1/4
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 2);											
						g = y;
						r = (v + g);					
						b = (u + g);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 	  
					}
					
					if (from.equals("A1_5")) // A1_5 to RGB a1=1 a2=0, e=1/4, R<=>G
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						u = u + (v >> 2);										
						r = y - v;				
						g = (v + r);			
						b = (u + r);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					} 
					
					if (from.equals("A1_6")) // A1_6 to RGB a1=0 a2=1, e=1/4, B<=>G
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 2);				
						b = y - u;					
						r = (v + b);					
						g = (u + b);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 	  
					}
					
					if (from.equals("A1_7"))  // A1_7 to RGB a1=a2=0 e=1/4 B<=>R
					{
						y = r; 
						u = g - addval3;
						v = b - addval3;
						u = u + (v >> 2);										
						g = y;
						b = (v + g);					
						r = (u + g);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 	  
					}
					
					if (from.equals("A1_8")) // A1_8 to RGB a1=1 a2=0, e=1/4, R=>G=>B=>R
					{
						y = r; 
						u = g - addval3;  
						v = -b + addval3; 
						u = u + (v >> 2);						
						b = y - v;					
						g = (v + b);				
						r = (u + b);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 	  
					}
					
					// A1_9 to RGB a1=0 a2=1, e=1/4, R=>B=>G=>R
					if (from.equals("A1_9")) 
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						u = u + (v >> 2);					
						r = y - u;					
						b = (v + r);					
						g = (u + r);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 	  
					}
					
					// A1_10 to RGB a1=a2=0 e=1/2
					if (from.equals("A1_10")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 1);							
						g = y;
						r = (v + g);				
						b = (u + g);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 	  
					}
					
					// A1_11 to RGB a1=a2=0 e=1/2
					if (from.equals("A1_11")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 1);						
						b = y - u;					
						r = (v + b);					
						g = (u + b);					
						values[0] = r;
						values[1] = g;
						values[2] = b;   
					}
					
					// A1_12 to RGB a1=a2=0 e=1/2  R<=>B
					if (from.equals("A1_12")) 
					{
						y = r; 
						u = g - addval3;
						v = b - addval3;
						u = u + (v >> 1);						
						g = y;
						b = (v + g);						
						r = (u + g);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A2_1 to RGB a1=1 a2=0
					if (from.equals("A2_1")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						g = y - v;		
						r = (v + g);		
						b = (u + g);			
						values[0] = r;
						values[1] = g;
						values[2] = b; 	  
					}
					
					// A2_2 to RGB a1=1 a2=0 R<=>G
					if (from.equals("A2_2")) 
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						r = y;
						g = (v + r);			
						b = (u + r);				
						values[0] = r;
						values[1] = g;
						values[2] = b;  
					}
					
					// A2_3 to RGB a1=1 a2=0, B<=>G
					if (from.equals("A2_3")) 
					{
						y = r; 
						v = g - addval3;
						u = -b + addval3;  
						b = y - v;				
						r = (v + b);				
						g = (u + b);				
						values[0] = r;
						values[1] = g;
						values[2] = b;   
					}
					
					// A2_4 to RGB a1=1 a2=0, e=1/4
					if (from.equals("A2_4")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 2);										
						g = y - v;				
						r = (v + g);				
						b = (u + g);					  
						values[0] = r;
						values[1] = g;
						values[2] = b; 	  
					}
					
					// A2_5 to RGB a1=1 a2=0 e=1/4 R<=>G
					if (from.equals("A2_5"))
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						u = u + (v >> 2);											
						r = y;
						g = (v + r);					
						b = (u + r);				
						values[0] = r;
						values[1] = g;
						values[2] = b;   
					}
					
					// A2_6 to RGB a1=1 a2=0, e=1/4, B<=>G
					if (from.equals("A2_6")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 2);											
						b = y - v;					
						r = (v + b);					
						g = (u + b);					
						values[0] = r;
						values[1] = g;
						values[2] = b;  
					}
					
					// A2_7 to RGB a1=1 a2=0, e=1/4, B<=>R
					if (from.equals("A2_7")) 
					{
						y = r; 
						u = g - addval3;
						v = b - addval3;
						u = u + (v >> 2);					
						g = y - u;					
						b = (v + g);					
						r = (u + g);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A2_8 to RGB a1=0 a2=1, e=1/4, R=>G=>B=>R
					if (from.equals("A2_8")) 
					{
						y = r; 
						u = g - addval3;
						v = -b + addval3; 
						u = u + (v >> 2);					
						b = y - u;					
						g = (v + b);					
						r = (u + b);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A2_9 to RGB a1=a2=0 e=1/4  R=>B=>G=>R
					if (from.equals("A2_9")) 
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						u = u + (v >> 2);								
						r = y;
						b = (v + r);				
						g = (u + r);					
						values[0] = r;
						values[1] = g;
						values[2] = b;   
					}
					
					// A2_10 to RGB a1=1 a2=0, e=1/2
					if (from.equals("A2_10")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 1);					
						g = y - v;					
						r = (v + g);					
						b = (u + g);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 	  
					}
					
					// A2_11 to RGB a1=1 a2=0, e=1/2, B<=>G
					if (from.equals("A2_11")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 1);					
						b = y - v;			
						r = (v + b);					
						g = (u + b);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A2_12 to RGB a1=0 a2=1, e=1/2, B<=>R
					if (from.equals("A2_12")) 
					{
						y = r; 
						u = g - addval3;
						v = b - addval3;
						u = u + (v >> 1);					
						g = y - u;					
						b = (v + g);					
						r = (u + g);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A3_1 to RGB a1=a2=0 e=1/2 B<=>G
					if (from.equals("A3_1")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						g = y - u;								
						r = (v + g);					
						b = (u + g);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A3_2 to RGB a1=0 a2=1, R<=>G
					if (from.equals("A3_2")) 
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						r = y - u;				
						g = (v + r);					
						b = (u + r);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A3_3 to RGB a1=a2=0 B<=>G
					if (from.equals("A3_3")) 
					{
						y = r; 
						v = g - addval3;
						u = -b + addval3; 
						b = y;
						r = (v + b);					
						g = (u + b);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A3_4 to RGB a1=0 a2=1, e=1/4
					if (from.equals("A3_4")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 2);											
						g = y - u;					
						r = (v + g);					
						b = (u + g);		
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A3_5 to RGB a1=0 a2=1, e=1/4, R<=>G
					if (from.equals("A3_5")) 
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						u = u + (v >> 2);										
						r = y - u;				
						g = (v + r);					
						b = (u + r);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A3_6 to RGB a1=a2=0 e=1/4 B<=>G
					if (from.equals("A3_6")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 2);										
						b = y;
						r = (v + b);				
						g = (u + b);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A3_7 to RGB a1=1 a2=0, e=1/4, B<=>R
					if (from.equals("A3_7")) 
					{
						y = r; 
						u = g - addval3;
						v = b - addval3;
						u = u + (v >> 2);				
						g = y - v;		
						b = (v + g);			
						r = (u + g);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A3_8 to RGB a1=a2=0 e=1/4 R=>G=>B=>R
					if (from.equals("A3_8")) 
					{
						y = r; 
						u = g - addval3;
						v = -b + addval3; 
						u = u + (v >> 2);											
						b = y;
						g = (v + b);				
						r = (u + b);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A3_9 to RGB a1=1 a2=0, e=1/4, R=>B=>G=>R
					if (from.equals("A3_9")) 
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						u = u + (v >> 2);				
						r = y - v;					
						b = (v + r);					
						g = (u + r);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A3_10 to RGB a1=0 a2=1, e=1/2
					if (from.equals("A3_10")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 1);				
						g = y - u;		
						r = (v + g);				
						b = (u + g);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A3_11 to RGB a1=a2=0 e=1/2 B<=>G
					if (from.equals("A3_11")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 1);						
						b = y;
						r = (v + b);			
						g = (u + b);			
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A3_12 to RGB a1=1 a2=0, e=1/2, B<=>R
					if (from.equals("A3_12")) 
					{
						y = r; 
						u = g - addval3;
						v = b - addval3;
						u = u + (v >> 1);		
						g = y - v;		
						b = (v + g);				
						r = (u + g);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_1 to RGB a1=1/2 a2=0
					if (from.equals("A4_1")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						g = y - (v >> 1);					
						r = (v + g);					
						b = (u + g);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_2 to RGB a1=1/2 a2=0, R<=>G
					if (from.equals("A4_2")) 
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						r = y - (v >> 1);				
						g = (v + r);				
						b = (u + r);			
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_3 to RGB a1=1/2 a2=0, B<=>G
					if (from.equals("A4_3")) 
					{
						y = r; 
						v = g - addval3;
						u = -b + addval3; 
						b = y - ((v + u) >> 1);					
						r = (v + b);					
						g = (u + b);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_4 to RGB a1=1/2 a2=0, e=1/4
					if (from.equals("A4_4")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 2);											
						g = y - (v >> 1);					
						r = (v + g);					
						b = (u + g);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_5 to RGB a1=1/2 a2=0, e=1/4, R<=>G
					if (from.equals("A4_5")) 
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						u = u + (v >> 2);											
						r = y - (v >> 1);						
						g = (v + r);				
						b = (u + r);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_6 to RGB a1=1/2 a2=0, e=1/4, B<=>G
					if (from.equals("A4_6")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 2);									
						b = y - ((v + u) >> 1);			
						r = (v + b);			
						g = (u + b);			
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_7 to RGB a2=1/2 a1=0, e=1/4, B<=>R
					if (from.equals("A4_7")) 
					{
						y = r; 
						u = g - addval3;
						v = b - addval3;
						u = u + (v >> 2);
						g = y - (u >> 1);			
						b = (v + g);			
						r = (u + g);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_8 to RGB a1=a2=1/2, e=1/4, R=>G=>B=>R
					if (from.equals("A4_8")) 
					{
						y = r; 
						u = g - addval3;
						v = -b + addval3; 
						u = u + (v >> 2);				
						b = y - ((v + u) >> 1);			
						g = (v + b);				
						r = (u + b);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_9 to RGB a2=1/2 a1=0, e=1/4, R=>B=>G=>R
					if (from.equals("A4_9")) 
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						u = u + (v >> 2);							
						r = y - (u >> 1);				
						g = (u + r);				
						b = (v + r);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_10 to RGB a1=1/2 a2=0, e=1/2
					if (from.equals("A4_10")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 1);					
						g = y - (v >> 1);					
						r = (v + g);				
						b = (u + g);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_11 to RGB a1=a2=1/2, e=1/2, B<=>G
					if (from.equals("A4_11")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 1);			
						b = y - ((v + u) >> 1);			
						r = (v + b);			
						g = (u + b);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A4_12 to RGB a2=1/2 a1=0, e=1/2, B<=>R
					if (from.equals("A4_12")) 
					{
						y = r; 
						u = g - addval3;
						v = b - addval3;
						u = u + (v >> 1);			
						g = y - (u >> 1);			
						b = (v + g);				
						r = (u + g);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_1 to RGB a1=1/2 a2=0, e=1/2, B<=>R
					if (from.equals("A5_1")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						g = y - (u >> 1);	
						r = (v + g);
						b = (u + g);		
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_2 to RGB a1=a2=1/2, R<=>G
					if (from.equals("A5_2")) 
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						r = y - ((v + u) >> 1);				
						g = (v + r);				
						b = (u + r);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_3 to RGB a2=1/2 a1=0, B<=>G
					if (from.equals("A5_3")) 
					{
						y = r; 
						v = g - addval3;
						u = -b + addval3; 
						b = y - (u >> 1);					
						r = (v + b);				
						g = (u + b);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_4 to RGB a2=1/2 a1=0, e=1/4
					if (from.equals("A5_4")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 2);				
						g = y - (u >> 1);				
						r = (v + g);				
						b = (u + g);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_5 to RGB a1=a2=1/2, e=1/4, R<=>G
					if (from.equals("A5_5")) 
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						u = u + (v >> 2);					
						r = y - ((v + u) >> 1);					
						g = (v + r);					
						b = (u + r);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_6 to RGB a2=1/2 a1=0, e=1/4, B<=>G
					if (from.equals("A5_6")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 2);						
						b = y - (u >> 1);					
						r = (v + b);					
						g = (u + b);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_7 to RGB a1=1/2 a2=0, e=1/4, B<=>R
					if (from.equals("A5_7")) 
					{
						y = r; 
						u = g - addval3;
						v = b - addval3;
						u = u + (v >> 2);					
						g = y - (v >> 1);					
						b = (v + g);					
						r = (u + g);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_8 to RGB a1=1/2 a2=0, e=1/4, R=>G=>B=>R
					if (from.equals("A5_8")) 
					{
						y = r; 
						u = g - addval3;
						v = -b + addval3; 
						u = u + (v >> 2);					
						b = y - (v >> 1);					
						g = (v + b);					
						r = (u + b);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_9 to RGB a1=a2=1/2, e=1/4, R=>B=>G=>R
					if (from.equals("A5_9")) 
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						u = u + (v >> 2);						
						r = y - ((v + u) >> 1);					
						b = (v + r);					
						g = (u + r);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_10 to RGB a2=1/2 a1=0, e=1/2
					if (from.equals("A5_10")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 1);					
						g = y - (u >> 1);					
						r = (v + g);					
						b = (u + g);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_11 to RGB a2=1/2 a1=0, e=1/2, B<=>G
					if (from.equals("A5_11")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 1);					
						b = y - (u >> 1);					
						r = (v + b);					
						g = (u + b);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A5_12 to RGB a1=1/2 a2=0, e=1/2, B<=>R
					if (from.equals("A5_12"))
					{
						y = r; 
						u = g - addval3;
						v = b - addval3;
						u = u + (v >> 1);					
						g = y - (v >> 1);					
						b = (v + g);					
						r = (u + g);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A6_1 to RGB a1=a2=1/2
					if (from.equals("A6_1")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						g = y - ((v+u)>>1);					
						r = (v + g);					
						b = (u + g);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A6_2 to RGB a2=1/2 a1=0, R<=>G
					if (from.equals("A6_2")) 
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						r = y - (u >> 1);					
						g = (v + r);					
						b = (u + r);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A6_3 to RGB a1=1/2 a2=0, B<=>G
					if (from.equals("A6_3")) 
					{
						y = r; 
						v = g - addval3;
						u = -b + addval3; 
						b = y - (v >> 1);					
						r = (v + b);					
						g = (u + b);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A6_4 to RGB a1=a2=1/2, e=1/4
					if (from.equals("A6_4")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 2);						
						g = y - ((v + u) >> 1);					
						r = (v + g);					
						b = (u + g);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A6_5 to RGB a2=1/2 a1=0, e=1/4, R<=>G
					if (from.equals("A6_5")) 
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						u = u + (v >> 2);					
						r = y - (u >> 1);					
						g = (v + r);					
						b = (u + r);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A6_6 to RGB a1=1/2 a2=0, e=1/4, B<=>G
					if (from.equals("A6_6")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 2);												
						b = y - (v >> 1);					
						r = (v + b);					
						g = (u + b);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A6_7 to RGB a1=a2=1/2, e=1/4, B<=>R
					if (from.equals("A6_7")) 
					{
						y = r; 
						u = g - addval3;
						v = b - addval3;
						u = u + (v >> 2);				
						g = y - ((v + u) >> 1);				
						b = (v + g);					
						r = (u + g);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A6_8 to RGB a2=1/2 a1=0, e=1/4, R=>G=>B=>R
					if (from.equals("A6_8")) 
					{
						y = r; 
						u = g - addval3;
						v = -b + addval3; 
						u = u + (v >> 2);					
						b = y - (u >> 1);				
						g = (v + b);			
						r = (u + b);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A6_9 to RGB a1=1/2 a2=0, e=1/4, R=>B=>G=>R
					if (from.equals("A6_9")) 
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						u = u + (v >> 2);				
						r = y - (v >> 1);					
						b = (v + r);						
						g = (u + r);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A6_10 to RGB a1=a2=1/2, e=1/2
					if (from.equals("A6_10")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 1);					
						g = y - ((v + u) >> 1);					
						r = (v + g);					
						b = (u + g);						
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A6_11 to RGB a1=1/2 a2=0, e=1/2, B<=>G
					if (from.equals("A6_11")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 1);						
						b = y - (v >> 1);						
						r = (v + b);						
						g = (u + b);	
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A6_12 to RGB a1=a2=1/2, e=1/2, B<=>R
					if (from.equals("A6_12")) 
					{
						y = r; 
						u = g - addval3;
						v = b - addval3;
						u = u + (v >> 1);			
						g = y - ((v + u) >> 1);			
						b = (v + g);			
						r = (u + g);			
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A7_1 to RGB a1=a2=1/4  == YUV
					if (from.equals("A7_1")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						g = y - ((v + u) >> 2);						
						r = v + (int)g;					
						b = u + (int)g;					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A7_2 to RGB a1=1/2 a2=1/4, R<=>G
					if (from.equals("A7_2")) 
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						r = y - (((v << 1) + u) >> 2);					
						g = (v + r);					
						b = (u + r);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A7_3 to RGB a1=1/4 a2=1/2, B<=>G
					if (from.equals("A7_3"))
					{
						y = r; 
						v = g - addval3;
						u = -b + addval3; 
						b = y - (((u << 1) + v) >> 2);					
						r = (v + b);					
						g = (u + b);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A7_4 to RGB a1=a2=1/4, e=1/4
					if (from.equals("A7_4")) 
					{
						y = r;
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 2);					
						g = y - ((v + u) >> 2);						
						r = (v + g);					
						b = (u + g);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A7_5 to RGB a1=1/2 a2=1/4, e=1/4, R<=>G
					if (from.equals("A7_5")) 
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						u = u + (v >> 2);											
						r = y - (((v << 1) + u) >> 2);					
						g = (v + r);					
						b = (u + r);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A7_6 to RGB a1=1/4 a2=1/2, e=1/4, B<=>G
					if (from.equals("A7_6")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 2);											
						b = y - (((u << 1) + v) >> 2);					
						r = (v + b);					
						g = (u + b);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A7_7 to RGB a1=a2=1/4, e=1/4, B<=>R
					if (from.equals("A7_7")) 
					{
						y = r; 
						u = g - addval3;
						v = b - addval3;
						u = u + (v >> 2);						
						g = y - ((v + u) >> 2);					
						r = (u + g);					
						b = (v + g);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A7_8 to RGB a1=1/2 a2=1/4, e=1/4, R=>G=>B=>R
					if (from.equals("A7_8")) 
					{
						y = r; 
						u = g - addval3;
						v = -b + addval3; 
						u = u + (v >> 2);						
						b = y - (((v << 1) + u) >> 2);					
						g = (v + b);					
						r = (u + b);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A7_9 to RGB a1=1/4 a2=1/2, e=1/4, R=>B=>G=>R
					if (from.equals("A7_9")) 
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						u = u + (v >> 2);						
						r = y - (((u << 1) + v) >> 2);						
						b = (v + r);						
						g = (u + r);						
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A7_10 to RGB a1=a2=1/4, e=1/2
					if (from.equals("A7_10")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 1);							
						g = y - ((v + u) >> 2);					
						r = (v + g);					
						b = (u + g);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}

					// A7_11 to RGB a1=1/4 a2=1/2, e=1/2, B<=>G
					if (from.equals("A7_11")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 1);					
						b = y - (((u << 1) + v) >> 2);					
						r = (v + b);					
						g = (u + b);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A7_12 to RGB a1=a2=1/4, e=1/2, B<=>R
					if (from.equals("A7_12")) 
					{
						y = r; 
						u = g - addval3;
						v = b - addval3;
						u = u + (v >> 1);					
						g = y - ((v + u) >> 2);					
						b = (v + g);					
						r = (u + g);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_1 to RGB a1=1/2 a2=1/4
					if (from.equals("A8_1")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						g = y - (((v << 1) + u) >> 2);					
						r = (v + g);					
						b = (u + g);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_2 to RGB a1=a2=1/4, R<=>G
					if (from.equals("A8_2")) 
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						r = y - ((v + u) >> 2);				
						g = (v + r);				
						b = (u + r);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_3 to RGB a1=1/2 a2=1/4, B<=>G
					if (from.equals("A8_3")) 
					{
						y = r; 
						v = g - addval3;
						u = -b + addval3; 
						b = y - (((v << 1) + u) >> 2);			
						r = (v + b);				
						g = (u + b);
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_4 to RGB a1=1/2 a2=1/4, e=1/4
					if (from.equals("A8_4")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 2);										
						g = y - (((v << 1) + u) >> 2);				
						r = (v + g);				
						b = (u + g);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_5 to RGB a1=a2=1/4, e=1/4, R<=>G
					if (from.equals("A8_5")) 
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						u = u + (v >> 2);					
						r = y - ((v + u) >> 2);				
						g = (v + r);				
						b = (u + r);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_6 to RGB a1=1/2 a2=1/4, e=1/4, B<=>G
					if (from.equals("A8_6")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 2);					
						b = y - (((v << 1) + u) >> 2);					
						r = (v + b);					
						g = (u + b);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_7 to RGB a1=1/4 a2=1/2, e=1/4, B<=>R
					if (from.equals("A8_7")) 
					{
						y = r; 
						u = g - addval3;
						v = b - addval3;
						u = u + (v >> 2);		
						g = y - (((u << 1) + v) >> 2);		
						b = (v + g);		
						r = (u + g);	
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_8 to RGB a1=1/4 a2=1/2, e=1/4, R=>G=>B=>R
					if (from.equals("A8_8")) 
					{
						y = r; 
						u = g - addval3;
						v = -b + addval3; 
						u = u + (v >> 2);			
						b = y - (((u << 1) + v) >> 2);		
						g = (v + b);		
						r = (u + b);		
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_9 to RGB a1=a2=1/4, e=1/4, R=>B=>G=>R
					if (from.equals("A8_9")) 
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						u = u + (v >> 2);			
						r = y - ((v + u) >> 2);		
						b = (v + r);		
						g = (u + r);		
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_10 to RGB a1=1/2 a2=1/4, e=1/2
					if (from.equals("A8_10")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 1);					
						g = y - (((v << 1) + u) >> 2);				
						r = (v + g);					
						b = (u + g);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_11 to RGB a1=1/2 a2=1/4, e=1/2, B<=>G
					if (from.equals("A8_11")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 1);				
						b = y - (((v << 1) + u) >> 2);				
						r = (v + b);
						g = (u + b);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A8_12 to RGB a1=1/2 a2=1/4, e=1/2, B<=>R
					if (from.equals("A8_12")) 
					{
						y = r; 
						u = g - addval3;
						v = b - addval3;
						u = u + (v >> 1);
						g = y - (((u << 1) + v) >> 2);
						b = (v + g);
						r = (u + g);	
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A9_1 to RGB a1=1/4 a2=1/2
					if (from.equals("A9_1")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						g = y - (((u << 1) + v) >> 2);	
						r = (v + g);		
						b = (u + g);		
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A9_2 to RGB a1=1/4 a2=1/2, R<=>G
					if (from.equals("A9_2")) 
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						r = y - (((u << 1) + v) >> 2);	
						g = (v + r);		
						b = (u + r);			
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A9_3 to RGB a1=a2=1/4, B<=>G
					if (from.equals("A9_3")) 
					{
						y = r; 
						v = g - addval3;
						u = -b + addval3; 
						b = y - ((v + u) >> 2);		
						r = (v + b);		
						g = (u + b);			
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A9_4 to RGB a1=1/4 a2=1/2, e=1/4
					if (from.equals("A9_4")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 2);							
						g = y - (((u << 1) + v) >> 2);			
						r = (v + g);				
						b = (u + g);			
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A9_5 to RGB a1=1/4 a2=1/2, e=1/4, R<=>G
					if (from.equals("A9_5")) 
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						u = u + (v >> 2);			
						r = y - (((u << 1) + v) >> 2);				
						g = (v + r);					
						b = (u + r);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					// A9_6 to RGB a1=a2=1/4, e=1/4, B<=>G
					if (from.equals("A9_6")) 
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 2);				
						b = y - ((v + u) >> 2);				
						r = (v + b);					
						g = (u + b);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					if (from.equals("A9_7")) // A9_7 to RGB a1=1/2 a2=1/4, e=1/4, B<=>R
					{
						y = r; 
						u = g - addval3;
						v = b - addval3;
						u = u + (v >> 2);					
						g = y - (((v << 1) + u) >> 2);				
						b = (v + g);					
						r = (u + g);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					if (from.equals("A9_8"))  // A9_8 to RGB a1=a2=1/4, e=1/4, R=>G=>B=>R
					{
						y = r; 
						u = g - addval3;
						v = -b + addval3; 
						u = u + (v >> 2);					
						b = y - ((v + u) >> 2);				
						g = (v + b);				
						r = (u + b);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					if (from.equals("A9_9")) // A9_9 to RGB a1=1/2 a2=1/4, e=1/4, R=>B=>G=>R
					{
						y = r; 
						v = -g + addval3; 
						u = b - addval3;
						u = u + (v >> 2);				
						r = y - (((v << 1) + u) >> 2);				
						b = (v + r);				
						g = (u + r);				
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					 
					if (from.equals("A9_10")) // A9_10 to RGB a1=1/4 a2=1/2, e=1/2
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 1);					
						g = y - (((u << 1) + v) >> 2);					
						r = (v + g);				
						b = (u + g);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					if (from.equals("A9_11")) // A9_11 to RGB a1=a2=1/4, e=1/2, B<=>G
					{
						y = r; 
						v = g - addval3;
						u = b - addval3;
						u = u + (v >> 1);				
						b = y - ((v + u) >> 2);					
						r = (v + b);					
						g = (u + b);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					if (from.equals("A9_12")) // A9_12 to RGB a1=1/2 a2=1/4, e=1/2, B<=>R
					{
						y = r; 
						u = g - addval3;
						v = b - addval3;
						u = u + (v >> 1);					
						g = y - (((v << 1) + u) >> 2);					
						b = (v + g);				
						r = (u + g);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					if (from.equals("B1_1")) // B1_1 to RGB
					{
						y = r; 
						v = g - addval3;
						u = b;
						g = y;
						r = (v + g);					
						b = u;
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					} 
					
					if (from.equals("B1_2")) // B1_2 to RGB
					{
						u = r; 
						y = g;
						v = b - addval3;
						g = y;
						b = (v + g);					
						r = u;
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					if (from.equals("B2_1")) // B2_1 to RGB
					{
						y = r; 
						v = -g + addval3; 
						u = b;
						r = y;
						g = (v + r);					
						b = u;
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
								
					if (from.equals("B2_3")) // B2_3 to RGB
					{
						y = r; 
						u = g;
						v = -b + addval3; 
						r = y;
						g = u;
						b = (v + r);					
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
							
					if (from.equals("B3_2")) // B3_2 to RGB
					{
						u = r; 
						v = -g + addval3; 
						y = b;
						b = y;
						g = (v + b);					
						r = u;
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					if (from.equals("B3_3")) // B3_3 to RGB
					{
						u = r; 
						v = g - addval3;
						y = b;
						b = y;
						r = (v + b);	
						g = u;
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}
					
					if (from.equals("B4_1")) // B4_1 to RGB
					{
						y = r; 
						u = g - addval3;
						v = b;
						g = y - (u >> 1);
						b = v;
						r = (u + g);	
						values[0] = r;
						values[1] = g;
						values[2] = b; 
					}  
					
					if (from.equals("B5_2")) // B5_2 to RGB 
					{
						v = r; 
						y = g ;
						u = -b + addval3; 
						b = y - (u >> 1);
						r = v;
						g = (u + b);
						values[0] = r;
						values[1] = g;
						values[2] = b;  
					}
						 
					if (from.equals("B6_3"))  // B6_3 to RGB
					{
						y = r; 
						u = g - addval3;
						v = b;
						b = y - (u >> 1);
						g = v;
						r = (u + b);
						values[0] = r;
						values[1] = g;
						values[2] = b;    
					}
						
					if (from.equals("PEI09")) // PEI09 to RGB
					{
						y = r;
						u = g - addval3;
						v = b - addval3;
						g  = y - ((29*u + 86*v) >> 8);
						r = v + g;		
						b = u + ((87*r + 169*g) >> 8);				
						values[0] = r;
						values[1] = g;
						values[2] = b;  
					}
					
				}
				else 
				{
					values[0] = r;
					values[1] = g;
					values[2] = b;
				}
				
				// put the pixel back
				ips[0].putPixelValue( col, row, values[0]);
				ips[1].putPixelValue( col, row, values[1]);
				ips[2].putPixelValue( col, row, values[2]);
			}
			percent = ((col+1)*100)/w;
			IJ.showStatus("CSCmod_ is loading, may take a while. Status: "+ percent + "%");
		}	
	}
	
    // show separated images
  if (separated) 
	{
    imps[0].show();
    imps[1].show();
		imps[2].show();
  }
	else
	{
		if (modulo == true)
		{
			GenericDialog info = new GenericDialog("Information");
			info.addMessage("Number of Modulo-Operations: " + numOfModulo);
			info.showDialog();
		}
	}	
}

 
  public int setup(String arg, ImagePlus imp) 
	{
	/*OpenDialog od = new OpenDialog("Select Image", null);*/
    if (imp != null) 
		{
     title = imp.getTitle();
			/*title = od.getFileName();
			String dir = od.getDirectory ();
			if (title == null) return;
			FileInfo fi = load(dir, title);
			FileOpener fo = new FileOpener(fi);
			/*imp = fo.open(true);*/
      // set default choices
      separated = false;
      to = "RGB";
      from = "RGB";
			mode = "not Separated with Modulo";

			if (title.endsWith(") with Modulo"))
			{
				modulo = true;
			}
			else if (title.endsWith(")"))
			{
				modulo = false;
			}
			else
			{
				modulo = true;
			}
      if (title.endsWith(" (RGB)") || title.endsWith(" (RGB) with Modulo")) 
			{
        from = "RGB";
      }
      else if (title.endsWith(" (A1_1)") || title.endsWith(" (A1_1) with Modulo")) 
			{
        from = "A1_1";
      }
      else if (title.endsWith(" (A1_2)") || title.endsWith(" (A1_2) with Modulo")) 
			{
        from = "A1_2";
      }
      else if (title.endsWith(" (A1_3)") || title.endsWith(" (A1_3) with Modulo")) 
			{
        from = "A1_3";
      }
			else if (title.endsWith(" (A1_4)") || title.endsWith(" (A1_4) with Modulo")) 
			{
        from = "A1_4";
      }
			else if (title.endsWith(" (A1_5)") || title.endsWith(" (A1_5) with Modulo")) 
			{
        from = "A1_5";
      }
			else if (title.endsWith(" (A1_6)") || title.endsWith(" (A1_6) with Modulo")) 
			{
        from = "A1_6";
      }
			else if (title.endsWith(" (A1_7)") || title.endsWith(" (A1_7) with Modulo")) 
			{
        from = "A1_7";
      }
			else if (title.endsWith(" (A1_8)") || title.endsWith(" (A1_8) with Modulo")) 
			{
        from = "A1_8";
      }
			else if (title.endsWith(" (A1_9)") || title.endsWith(" (A1_9) with Modulo")) 
			{
        from = "A1_9";
      }
			else if (title.endsWith(" (A1_10)") || title.endsWith(" (A1_10) with Modulo")) 
			{
        from = "A1_10";
      }
			else if (title.endsWith(" (A1_11)") || title.endsWith(" (A1_11) with Modulo")) 
			{
        from = "A1_11";
      }
			else if (title.endsWith(" (A1_12)") || title.endsWith(" (A1_12) with Modulo")) 
			{
        from = "A1_12";
      }
			else if (title.endsWith(" (A2_1)") || title.endsWith(" (A2_1) with Modulo")) 
			{
        from = "A2_1";
      }
      else if (title.endsWith(" (A2_2)") || title.endsWith(" (A2_2) with Modulo")) 
			{
        from = "A2_2";
      }
      else if (title.endsWith(" (A2_3)") || title.endsWith(" (A2_3) with Modulo")) 
			{
        from = "A2_3";
      }
			else if (title.endsWith(" (A2_4)") || title.endsWith(" (A2_4) with Modulo")) 
			{
        from = "A2_4";
      }
			else if (title.endsWith(" (A2_5)") || title.endsWith(" (A2_5) with Modulo")) 
			{
        from = "A2_5";
      }
			else if (title.endsWith(" (A2_6)") || title.endsWith(" (A2_6) with Modulo")) 
			{
        from = "A2_6";
      }
			else if (title.endsWith(" (A2_7)") || title.endsWith(" (A2_7) with Modulo")) 
			{
        from = "A2_7";
      }
			else if (title.endsWith(" (A2_8)") || title.endsWith(" (A2_8) with Modulo")) 
			{
        from = "A2_8";
      }
			else if (title.endsWith(" (A2_9)") || title.endsWith(" (A2_9) with Modulo")) 
			{
        from = "A2_9";
      }
			else if (title.endsWith(" (A2_10)") || title.endsWith(" (A2_10) with Modulo")) 
			{
        from = "A2_10";
      }
			else if (title.endsWith(" (A2_11)") || title.endsWith(" (A2_11) with Modulo")) 
			{
        from = "A2_11";
      }
			else if (title.endsWith(" (A2_12)") || title.endsWith(" (A2_12) with Modulo")) 
			{
        from = "A2_12";
      }
			else if (title.endsWith(" (A3_1)") || title.endsWith(" (A3_1) with Modulo")) 
			{
        from = "A3_1";
      }
      else if (title.endsWith(" (A3_2)") || title.endsWith(" (A3_2) with Modulo")) 
			{
        from = "A3_2";
      }
      else if (title.endsWith(" (A3_3)") || title.endsWith(" (A3_3) with Modulo")) 
			{
        from = "A3_3";
      }
			else if (title.endsWith(" (A3_4)") || title.endsWith(" (A3_4) with Modulo")) 
			{
        from = "A3_4";
      }
			else if (title.endsWith(" (A3_5)") || title.endsWith(" (A3_5) with Modulo")) 
			{
        from = "A3_5";
      }
			else if (title.endsWith(" (A3_6)") || title.endsWith(" (A3_6) with Modulo")) 
			{
        from = "A3_6";
      }
			else if (title.endsWith(" (A3_7)") || title.endsWith(" (A3_7) with Modulo")) 
			{
        from = "A3_7";
      }
			else if (title.endsWith(" (A3_8)") || title.endsWith(" (A3_8) with Modulo"))
			{
        from = "A3_8";
      }
			else if (title.endsWith(" (A3_9)") || title.endsWith(" (A3_9) with Modulo")) 
			{
        from = "A3_9";
      }
			else if (title.endsWith(" (A3_10)") || title.endsWith(" (A3_10) with Modulo")) 
			{
        from = "A3_10";
      }
			else if (title.endsWith(" (A3_11)") || title.endsWith(" (A3_11) with Modulo")) 
			{
        from = "A3_11";
      }
			else if (title.endsWith(" (A3_12)") || title.endsWith(" (A3_12) with Modulo")) 
			{
        from = "A3_12";
      }
			else if (title.endsWith(" (A4_1)") || title.endsWith(" (A4_1) with Modulo")) 
			{
        from = "A4_1";
      }
      else if (title.endsWith(" (A4_2)") || title.endsWith(" (A4_2) with Modulo")) 
			{
        from = "A4_2";
      }
      else if (title.endsWith(" (A4_3)") || title.endsWith(" (A4_3) with Modulo")) 
			{
        from = "A4_3";
      }
			else if (title.endsWith(" (A4_4)") || title.endsWith(" (A4_4) with Modulo")) 
			{
				from = "A4_4";
      }
			else if (title.endsWith(" (A4_5)") || title.endsWith(" (A4_5) with Modulo")) 
			{
        from = "A4_5";
      }
			else if (title.endsWith(" (A4_6)") || title.endsWith(" (A4_6) with Modulo")) 
			{
        from = "A4_6";
      }
			else if (title.endsWith(" (A4_7)") || title.endsWith(" (A4_7) with Modulo")) 
			{
        from = "A4_7";
      }
			else if (title.endsWith(" (A4_8)") || title.endsWith(" (A4_8) with Modulo")) {
        from = "A4_8";
      }
			else if (title.endsWith(" (A4_9)") || title.endsWith(" (A4_9) with Modulo")) 
			{
        from = "A4_9";
      }
			else if (title.endsWith(" (A4_10)") || title.endsWith(" (A4_10) with Modulo")) 
			{
        from = "A4_10";
      }
			else if (title.endsWith(" (A4_11)") || title.endsWith(" (A4_11) with Modulo")) 
			{
        from = "A4_11";
      }
			else if (title.endsWith(" (A4_12)") || title.endsWith(" (A4_12) with Modulo")) 
			{
        from = "A4_12";
      }
			else if (title.endsWith(" (A5_1)") || title.endsWith(" (A5_1) with Modulo")) 
			{
        from = "A5_1";
      }
      else if (title.endsWith(" (A5_2)") || title.endsWith(" (A5_2) with Modulo")) 
			{
        from = "A5_2";
      }
      else if (title.endsWith(" (A5_3)") || title.endsWith(" (A5_3) with Modulo")) 
			{
        from = "A5_3";
      }
			else if (title.endsWith(" (A5_4)") || title.endsWith(" (A5_4) with Modulo")) 
			{
        from = "A5_4";
      }
			else if (title.endsWith(" (A5_5)") || title.endsWith(" (A5_5) with Modulo")) 
			{
        from = "A5_5";
      }
			else if (title.endsWith(" (A5_6)") || title.endsWith(" (A5_6) with Modulo")) 
			{
        from = "A5_6";
      }
			else if (title.endsWith(" (A5_7)") || title.endsWith(" (A5_7) with Modulo")) 
			{
        from = "A5_7";
      }
			else if (title.endsWith(" (A5_8)") || title.endsWith(" (A5_8) with Modulo")) 
			{
        from = "A5_8";
      }
			else if (title.endsWith(" (A5_9)") || title.endsWith(" (A5_9) with Modulo")) 
			{
        from = "A5_9";
      }
			else if (title.endsWith(" (A5_10)") || title.endsWith(" (A5_10) with Modulo")) 
			{
        from = "A5_10";
      }
			else if (title.endsWith(" (A5_11)") || title.endsWith(" (A5_11) with Modulo")) 
			{
        from = "A5_11";
      }
			else if (title.endsWith(" (A5_12)") || title.endsWith(" (A5_12) with Modulo")) 
			{
        from = "A5_12";
      }
			else if (title.endsWith(" (A6_1)") || title.endsWith(" (A6_1) with Modulo")) 
			{
        from = "A6_1";
      }
      else if (title.endsWith(" (A6_2)") || title.endsWith(" (A6_2) with Modulo")) 
			{
        from = "A6_2";
      }
      else if (title.endsWith(" (A6_3)") || title.endsWith(" (A6_3) with Modulo")) 
			{
        from = "A6_3";
      }
			else if (title.endsWith(" (A6_4)") || title.endsWith(" (A6_4) with Modulo")) 
			{
        from = "A6_4";
      }
			else if (title.endsWith(" (A6_5)") || title.endsWith(" (A6_5) with Modulo")) 
			{
        from = "A6_5";
      }
			else if (title.endsWith(" (A6_6)") || title.endsWith(" (A6_6) with Modulo")) 
			{
        from = "A6_6";
      }
			else if (title.endsWith(" (A6_7)") || title.endsWith(" (A6_7) with Modulo")) 
			{
        from = "A6_7";
      }
			else if (title.endsWith(" (A6_8)") || title.endsWith(" (A6_8) with Modulo")) 
			{
        from = "A6_8";
      }
			else if (title.endsWith(" (A6_9)") || title.endsWith(" (A6_9) with Modulo")) 
			{
        from = "A6_9";
      }
			else if (title.endsWith(" (A6_10)") || title.endsWith(" (A6_10) with Modulo")) 
			{
        from = "A6_10";
      }
			else if (title.endsWith(" (A6_11)") || title.endsWith(" (A6_11) with Modulo")) 
			{
        from = "A6_11";
      }
			else if (title.endsWith(" (A6_12)") || title.endsWith(" (A6_12) with Modulo")) 
			{
        from = "A6_12";
      }
			else if (title.endsWith(" (A7_1)") || title.endsWith(" (A7_1) with Modulo")) 
			{
        from = "A7_1";
      }
      else if (title.endsWith(" (A7_2)") || title.endsWith(" (A7_2) with Modulo")) 
			{
        from = "A7_2";
      }
      else if (title.endsWith(" (A7_3)") || title.endsWith(" (A7_3) with Modulo")) 
			{
        from = "A7_3";
      }
			else if (title.endsWith(" (A7_4)") || title.endsWith(" (A7_4) with Modulo")) 
			{
        from = "A7_4";
      }
			else if (title.endsWith(" (A7_5)") || title.endsWith(" (A7_5) with Modulo")) 
			{
        from = "A7_5";
      }
			else if (title.endsWith(" (A7_6)") || title.endsWith(" (A7_6) with Modulo")) 
			{
        from = "A7_6";
      }
			else if (title.endsWith(" (A7_7)") || title.endsWith(" (A7_7) with Modulo")) 
			{
        from = "A7_7";
      }
			else if (title.endsWith(" (A7_8)") || title.endsWith(" (A7_8) with Modulo")) 
			{
        from = "A7_8";
      }
			else if (title.endsWith(" (A7_9)") || title.endsWith(" (A7_9) with Modulo")) 
			{
        from = "A7_9";
      }
			else if (title.endsWith(" (A7_10)") || title.endsWith(" (A7_10) with Modulo")) 
			{
        from = "A7_10";
      }
			else if (title.endsWith(" (A7_11)") || title.endsWith(" (A7_11) with Modulo")) 
			{
        from = "A7_11";
      }
			else if (title.endsWith(" (A7_12)") || title.endsWith(" (A7_12) with Modulo")) 
			{
        from = "A7_12";
      }
			else if (title.endsWith(" (A8_1)") || title.endsWith(" (A8_1) with Modulo")) 
			{
        from = "A8_1";
      }
      else if (title.endsWith(" (A8_2)") || title.endsWith(" (A8_2) with Modulo")) 
			{
        from = "A8_2";
      }
      else if (title.endsWith(" (A8_3)") || title.endsWith(" (A8_3) with Modulo")) 
			{
        from = "A8_3";
      }
			else if (title.endsWith(" (A8_4)") || title.endsWith(" (A8_4) with Modulo")) 
			{
        from = "A8_4";
      }
			else if (title.endsWith(" (A8_5)") || title.endsWith(" (A8_5) with Modulo")) 
			{
        from = "A8_5";
      }
			else if (title.endsWith(" (A8_6)") || title.endsWith(" (A8_6) with Modulo")) 
			{
        from = "A8_6";
      }
			else if (title.endsWith(" (A8_7)") || title.endsWith(" (A8_7) with Modulo")) 
			{
        from = "A8_7";
      }
			else if (title.endsWith(" (A8_8)") || title.endsWith(" (A8_8) with Modulo")) 
			{
        from = "A8_8";
      }
			else if (title.endsWith(" (A8_9)") || title.endsWith(" (A8_9) with Modulo")) 
			{
        from = "A8_9";
      }
			else if (title.endsWith(" (A8_10)") || title.endsWith(" (A8_10) with Modulo")) 
			{
        from = "A8_10";
      }
			else if (title.endsWith(" (A8_11)") || title.endsWith(" (A8_11) with Modulo"))
			{
        from = "A8_11";
      }
			else if (title.endsWith(" (A8_12)") || title.endsWith(" (A8_12) with Modulo")) 
			{
        from = "A8_12";
      }
			else if (title.endsWith(" (A9_1)") || title.endsWith(" (A9_1) with Modulo")) 
			{
        from = "A9_1";
      }
      else if (title.endsWith(" (A9_2)") || title.endsWith(" (A9_2) with Modulo")) 
			{
        from = "A9_2";
      }
      else if (title.endsWith(" (A9_3)") || title.endsWith(" (A9_3) with Modulo")) 
			{
        from = "A9_3";
      }
			else if (title.endsWith(" (A9_4)") || title.endsWith(" (A9_4) with Modulo")) 
			{
        from = "A9_4";
      }
			else if (title.endsWith(" (A9_5)") || title.endsWith(" (A9_5) with Modulo")) 
			{
        from = "A9_5";
      }
			else if (title.endsWith(" (A9_6)") || title.endsWith(" (A9_6) with Modulo")) 
			{
        from = "A9_6";
      }
			else if (title.endsWith(" (A9_7)") || title.endsWith(" (A9_7) with Modulo")) 
			{
        from = "A9_7";
      }
			else if (title.endsWith(" (A9_8)") || title.endsWith(" (A9_8) with Modulo")) 
			{
        from = "A9_8";
      }
			else if (title.endsWith(" (A9_9)") || title.endsWith(" (A9_9) with Modulo")) 
			{
        from = "A9_9";
      }
			else if (title.endsWith(" (A9_10)") || title.endsWith(" (A9_10) with Modulo")) 
			{
        from = "A9_10";
      }
			else if (title.endsWith(" (A9_11)") || title.endsWith(" (A9_11) with Modulo")) 
			{
        from = "A9_11";
      }
			else if (title.endsWith(" (A9_12)") || title.endsWith(" (A9_12) with Modulo")) 
			{
        from = "A9_12";
      }
			else if (title.endsWith(" (B1_1)") || title.endsWith(" (B1_1) with Modulo")) 
			{
        from = "B1_1";
      }
      else if (title.endsWith(" (B1_2)") || title.endsWith(" (B1_2) with Modulo")) 
			{
        from = "B1_2";
      }
      else if (title.endsWith(" (B2_1)") || title.endsWith(" (B2_1) with Modulo")) 
			{
        from = "B2_1";
      }
			else if (title.endsWith(" (B2_3)") || title.endsWith(" (B2_3) with Modulo")) 
			{
        from = "B2_3";
      }
			else if (title.endsWith(" (B3_2)") || title.endsWith(" (B3_2) with Modulo")) 
			{
        from = "B3_2";
      }
			else if (title.endsWith(" (B3_3)") || title.endsWith(" (B3_3) with Modulo")) 
			{
        from = "B3_3";
      }
			else if (title.endsWith(" (B4_1)") || title.endsWith(" (B4_1) with Modulo")) 
			{
        from = "B4_1";
      }
			else if (title.endsWith(" (B5_2)") || title.endsWith(" (B5_2) with Modulo")) 
			{
        from = "B5_2";
      }
			else if (title.endsWith(" (B6_3)") || title.endsWith(" (B6_3) with Modulo")) 
			{
        from = "B6_3";
      }
			else if (title.endsWith(" (PEI09)") || title.endsWith(" (PEI09) with Modulo")) 
			{
        from = "PEI09";
      }

      // show dialog
      if (!showDialog()) 
			{ 
				return DONE; 
			}

      // set title
      if (!separated) 
			{
        if (title.endsWith(" (RGB)")) 
				{
          title = title.substring(0, title.length() - 6); 
        }
        else if (title.endsWith("_1)")) //" (Ax_1)" (1 <= x >= 9)
				{						
          title = title.substring(0, title.length() - 7);
        }
        else if (title.endsWith("_2)")) //" (Ax_2)" (1 <= x >= 9)
				{
          title = title.substring(0, title.length() - 7);
        }
        else if (title.endsWith("_3)")) //" (Ax_3)" (1 <= x >= 9)
				{
          title = title.substring(0, title.length() - 7);
        }
				else if (title.endsWith("_4)")) //" (Ax_4)" (1 <= x >= 9)
				{
          title = title.substring(0, title.length() - 7);
        }
				else if (title.endsWith("_5)")) //" (Ax_5)" (1 <= x >= 9)
				{
          title = title.substring(0, title.length() - 7);
        }
				else if (title.endsWith("_6)")) //" (Ax_6)" (1 <= x >= 9)
				{
          title = title.substring(0, title.length() - 7);
        }
				else if (title.endsWith("_7)")) //" (Ax_7)" (1 <= x >= 9)
				{
          title = title.substring(0, title.length() - 7);
        }
				else if (title.endsWith("_8)")) //" (Ax_8)" (1 <= x >= 9)
				{
          title = title.substring(0, title.length() - 7);
        }
				else if (title.endsWith("_9)")) //" (Ax_9)" (1 <= x >= 9)
				{
          title = title.substring(0, title.length() - 7);
        }
				else if (title.endsWith("_10)")) //" (Ax_10)" (1 <= x >= 9)
				{
          title = title.substring(0, title.length() - 8);
        }
				else if (title.endsWith("_11)")) //" (Ax_11)" (1 <= x >= 9)
				{
          title = title.substring(0, title.length() - 8);
        }
				else if (title.endsWith("_12)")) //" (Ax_12)" (1 <= x >= 9)
				{
          title = title.substring(0, title.length() - 8);
        }
				else if (title.endsWith(" (PEI09)")) 
				{
          title = title.substring(0, title.length() - 8);
        }
				else if (title.endsWith(" (RGB) with Modulo")) 
				{
          title = title.substring(0, title.length() - 18); 
        }
        else if (title.endsWith("_1) with Modulo")) //" (Ax_1) with Modulo" (1 <= x >= 9)
				{		
          title = title.substring(0, title.length() - 19); 
        }
        else if (title.endsWith("_2) with Modulo")) //" (Ax_2) with Modulo" (1 <= x >= 9)
				{
          title = title.substring(0, title.length() - 19);
        }
        else if (title.endsWith("_3) with Modulo")) //" (Ax_3) with Modulo" (1 <= x >= 9)
				{
          title = title.substring(0, title.length() - 19);
        }
				else if (title.endsWith("_4) with Modulo")) //" (Ax_4) with Modulo" (1 <= x >= 9)
				{
          title = title.substring(0, title.length() - 19);
        }
				else if (title.endsWith("_5) with Modulo")) //" (Ax_5) with Modulo" (1 <= x >= 9)
				{
          title = title.substring(0, title.length() - 19);
        }
				else if (title.endsWith("_6) with Modulo")) //" (Ax_6) with Modulo" (1 <= x >= 9)
				{
          title = title.substring(0, title.length() - 19);
        }
				else if (title.endsWith("_7) with Modulo")) //" (Ax_7) with Modulo" (1 <= x >= 9)
				{
          title = title.substring(0, title.length() - 19);
        }
				else if (title.endsWith("_8) with Modulo")) //" (Ax_8) with Modulo" (1 <= x >= 9)
				{
          title = title.substring(0, title.length() - 19);
        }
				else if (title.endsWith("_9) with Modulo")) //" (Ax_9) with Modulo" (1 <= x >= 9)
				{
          title = title.substring(0, title.length() - 19);
        }
				else if (title.endsWith("_10) with Modulo")) //" (Ax_10) with Modulo" (1 <= x >= 9)
				{
          title = title.substring(0, title.length() - 20);
        }
				else if (title.endsWith("_11) with Modulo")) //" (Ax_11) with Modulo" (1 <= x >= 9)
				{
          title = title.substring(0, title.length() - 20);
        }
				else if (title.endsWith("_12) with Modulo")) //" (Ax_12) with Modulo" (1 <= x >= 9)
				{
          title = title.substring(0, title.length() - 20);
        }
				else if (title.endsWith(" (PEI09) with Modulo")) 
				{
          title = title.substring(0, title.length() - 20);
        }
		
				if ( modulo == true && !to.equals("RGB")) 
				{
					imp.setTitle(title + " (" + to + ")" + " with Modulo");
				}	
				else 
				{
					imp.setTitle(title + " (" + to + ")");
				}
      }
    }

		return DOES_ALL;
  }


  // --------------------------------------------------------
  // about
  void showAbout() 
	{
    GenericDialog about = new GenericDialog("About Color Space Converter with Modulo (CSCmod_)");
    about.addMessage("This plug-in filter converts an image to a different color space with or without increased bit depth.");
		about.addMessage("Based on the Multiplierless Reversible Colour Transforms introduced by Tilo Strutz.");
		about.addMessage("Improved by limiting the bit depth to 8 instead of 9 bits.");
		about.addMessage("CSCmod_ Version: 1.3\nlast modification: 21.07.2014");
		about.addMessage("Alexander Leipnitz\nHochschule f\u00fcr Telekommunikation Leipzig\ncontact: azleipnitz@hft-leipzig.de");
		about.addMessage("Copyright (C) GNU General Public License  ");
		about.showDialog();
		showDialog();
  }
  
  // --------------------------------------------------------
  // error
  void showError() 
	{ 
		GenericDialog error = new GenericDialog("Error");
		error.addMessage("This action is not supported");
		error.addMessage("Only Transformations from or to RGB are allowed");
    error.showDialog();
		showDialog();
	}

  boolean showDialog() 
	{
    String[] spaces = { "RGB", "A1_1","A1_2","A1_3","A1_4","A1_5","A1_6","A1_7","A1_8","A1_9","A1_10","A1_11","A1_12",
												"A2_1","A2_2","A2_3","A2_4","A2_5","A2_6","A2_7","A2_8","A2_9","A2_10","A2_11","A2_12",
												"A3_1","A3_2","A3_3","A3_4","A3_5","A3_6","A3_7","A3_8","A3_9","A3_10","A3_11","A3_12",
												"A4_1","A4_2","A4_3","A4_4","A4_5","A4_6","A4_7","A4_8","A4_9","A4_10","A4_11","A4_12",
												"A5_1","A5_2","A5_3","A5_4","A5_5","A5_6","A5_7","A5_8","A5_9","A5_10","A5_11","A5_12",
												"A6_1","A6_2","A6_3","A6_4","A6_5","A6_6","A6_7","A6_8","A6_9","A6_10","A6_11","A6_12",
												"A7_1","A7_2","A7_3","A7_4","A7_5","A7_6","A7_7","A7_8","A7_9","A7_10","A7_11","A7_12",
												"A8_1","A8_2","A8_3","A8_4","A8_5","A8_6","A8_7","A8_8","A8_9","A8_10","A8_11","A8_12",
												"A9_1","A9_2","A9_3","A9_4","A9_5","A9_6","A9_7","A9_8","A9_9","A9_10","A9_11","A9_12",
												"B1_1","B1_2","B2_1","B2_3","B3_2","B3_3","B4_1","B5_2","B6_3","PEI09"};
		String[] modes = { "Separated with Modulo", "Separated without Modulo", "not Separated with Modulo" };
    // create a dialog
    GenericDialog dialog = new GenericDialog("Color Space Converter settings");
    dialog.addChoice("from ColorSpace", spaces, from);
    dialog.addChoice("to ColorSpace", spaces, to);
		dialog.addChoice("Channelmode", modes, mode);
		dialog.addMessage("Separated with Modulo: Shows each component (Y, U, V) separated in an 8-Bit channel.");
		dialog.addMessage("Separated without Modulo: Shows each component (Y, U, V) separated in an 32-Bit Float channel,");
		dialog.addMessage("													                      	                            because each component has a bit depth of 8 or 9 bits.");
		dialog.addMessage("not Separated with Modulo: Shows the complete transformation without increased bit depth,");
		dialog.addMessage("                                                   back transformation is possible");
		dialog.addMessage("(not Separated without Modulo: Not displayable, Picture would have a 25, 26 or 27-bit bit depth.)");
		dialog.enableYesNoCancel("Run","Show About");
		IJ.showStatus("CSCmod_ started");
    dialog.showDialog();
    if (dialog.wasCanceled()) 
		{ 
			return false; 
		}
		else if (dialog.wasOKed()) 
		{
			// get options
			from = dialog.getNextChoice();
			to = dialog.getNextChoice();
			mode = dialog.getNextChoice();
			if (mode.equals("Separated with Modulo"))
			{
				modulo = true;
				separated = true;
			}
			else if (mode.equals("Separated without Modulo"))
			{
				modulo = false;
				separated = true;
			}
			else if (mode.equals("not Separated with Modulo"))
			{
				modulo = true;
				separated = false;
			}

			if (from.equals(to)) 
			{ 
				showError(); 
				return true;
			}
			if (!from.equals("RGB") && !to.equals("RGB") ) 
			{ 
				showError(); 
				return true;
			}	
		}
		else 
		{ 
			showAbout(); 
			return true;
		}
		return true;
  }
  
  public static int ModuloRange(int e, int l, int u, int r) 
	{
		if (e > u) 
		{
			e = e - r;
			numOfModulo++;
		}
		else if (e < l) 
		{
			e = e + r;
			numOfModulo++;
		}
		else {e = e;}
		return e;
  }

}
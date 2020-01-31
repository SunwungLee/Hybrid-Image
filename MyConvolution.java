package uk.ac.soton.ecs.sunwunglee.hybridimages;

import org.openimaj.image.FImage;
import org.openimaj.image.processor.SinglebandImageProcessor;

public class MyConvolution implements SinglebandImageProcessor<Float, FImage>{
	private float[][] kernel;

	public MyConvolution(float[][] kernel){
		this.kernel 			= kernel; 
	}

	@Override
	public void processImage(FImage image){
		int imgRow 				= image.getHeight(), imgCol = image.getWidth();
		int kerRow 				= kernel.length, kerCol = kernel[0].length; // height, width // row, col
		int tmpRow 				= (int) (Math.floor(kerRow/2));
		int tmpCol 				= (int) (Math.floor(kerCol/2));
		float[][] tempArray 	= new float[imgRow+(2*tmpCol)][imgCol+(2*tmpRow)];
		float[][] convolved 	= new float[imgRow][imgCol];

		// set the contents of my temporary buffer image to the image
		//
		FImage cpyimg 			= null;
		cpyimg 					= image.internalAssign(image);

		// zero padding
		// 
		for (int x=tmpCol; x<(tempArray.length-tmpCol); x++) {
			for (int y=tmpRow; y<(tempArray[0].length-tmpRow); y++) {
				tempArray[x][y] = cpyimg.getPixel(y-tmpRow, x-tmpCol);
			}
		}
		
		// calculate convloution
		//
		float sum;
		for (int x=tmpCol; x<(tempArray.length-tmpCol); x++) { 
			for (int y=tmpRow; y<(tempArray[0].length-tmpRow); y++) {
				sum=0;
				for (int iwin=0; iwin<kerCol; iwin++) { 
					for(int jwin=0; jwin<kerRow; jwin++) { 
						sum += (tempArray[x+iwin-tmpCol][y+jwin-tmpRow] * kernel[kerRow-jwin-1][kerCol-iwin-1]);
					}
				}
				convolved[x-tmpCol][y-tmpRow] = sum;
			}
		}
		
		// restore
		//
		for (int i=0; i<imgRow; i++) { 
			for(int j=0; j<imgCol; j++) {
				image.pixels[i][j] = convolved[i][j]; 
			}
		}
		
		return;
	}
}

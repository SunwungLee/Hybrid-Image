package uk.ac.soton.ecs.sunwunglee.hybridimages;

import org.openimaj.image.FImage;
import org.openimaj.image.MBFImage;

import uk.ac.soton.ecs.sunwunglee.hybridimages.MyConvolution;

public class MyHybridImages {
	/**
	 * Compute a hybrid image combining low-pass and high-pass filtered images
	 * 
	 * @param lowImage
	 * 		the image to which apply the low-pass filter
	 * @param lowSigme
	 * 		the standard deviation of the low-pass filter
	 * @param highImage
	 * 		the image to which apply the high-pass filter
	 * @param highSigma
	 * 		the standard deviation of the low-pass component of computing the
	 * 		high-pass filtered image
	 * @return the computed hybrid image
	 */
	public static MBFImage makeHybrid(MBFImage lowImage, float lowSigma, MBFImage highImage, float highSigma) {
		MBFImage lowimg 		= lowImage;
		MBFImage highimg 		= highImage;
		MBFImage clonel		 	= lowimg.clone();
		int widSizel 			= lowimg.getWidth(); 	// column
		int heightSizel 		= lowimg.getHeight(); 	// row
		float[][] lredi 		= new float[heightSizel][widSizel];
		float[][] lgreeni 		= new float[heightSizel][widSizel];
		float[][] lbluei 		= new float[heightSizel][widSizel];
		MBFImage cloneh 		= highimg.clone();
		int widSizeh 			= highimg.getWidth(); 	// column
		int heightSizeh 		= highimg.getHeight(); 	// row
		float[][] hredi 		= new float[heightSizeh][widSizeh];
		float[][] hgreeni 		= new float[heightSizeh][widSizeh];
		float[][] hbluei 		= new float[heightSizeh][widSizeh];

		// seperate R,G,B from original image
		//
		for (int y = 0; y < heightSizel; y++) { 		// row
			for (int x = 0; x < widSizel; x++) { 		// col
				lredi[y][x] 	= clonel.getBand(0).pixels[y][x];
				lgreeni[y][x] 	= clonel.getBand(1).pixels[y][x];
				lbluei[y][x] 	= clonel.getBand(2).pixels[y][x];
			}
		}
		for (int y = 0; y < heightSizeh; y++) { 		// row
			for (int x = 0; x < widSizeh; x++) { 		// col
				hredi[y][x] 	= cloneh.getBand(0).pixels[y][x];
				hgreeni[y][x] 	= cloneh.getBand(1).pixels[y][x];
				hbluei[y][x] 	= cloneh.getBand(2).pixels[y][x];
			}
		}
		// generate FImage and it is gray-scale images.
		//
		FImage flredi 			= new FImage(lredi);
		FImage flgreeni 		= new FImage(lgreeni);
		FImage flbluei 			= new FImage(lbluei);
		
		FImage fhredi 			= new FImage(hredi);
		FImage fhgreeni 		= new FImage(hgreeni);
		FImage fhbluei 			= new FImage(hbluei);
		// make gaussian kernel
		//
		float lgkernel[][] = {}, hgkernel[][] = {}; 
		lgkernel 				= makeGaussianKernel(lowSigma);
		hgkernel 				= makeGaussianKernel(highSigma);
		// convolution 
		//
		MyConvolution CovImg 	= new MyConvolution(lgkernel);
		CovImg.processImage(flredi);
		CovImg.processImage(flgreeni);
		CovImg.processImage(flbluei);
		
		CovImg = new MyConvolution(hgkernel);
		CovImg.processImage(fhredi);
		CovImg.processImage(fhgreeni);
		CovImg.processImage(fhbluei);
		
		// Filtering
		//
		// Low-pass Filter
		MBFImage lwCovImg 		= new MBFImage(flredi,flgreeni,flbluei);
		// High-pass Filter
		MBFImage HiCovImg 		= new MBFImage(fhredi,fhgreeni,fhbluei);
		HiCovImg = highimg.subtract(HiCovImg).add(0.5f);
		
		// visualisation
		//
		//DisplayUtilities.display(lwCovImg);
		//DisplayUtilities.display(HiCovImg);
		
		// generate hybrid image
		//
		MBFImage MyHybridImg 	= null;
		MyHybridImg 			= HiCovImg.add(lwCovImg).divide(2.0f);
		
		return MyHybridImg;
	}
	
	public static float[][] makeGaussianKernel(float sigma){
		int size = 0, centre = 0;
		float sum = 0, e = 0, dist = 0, sigma2 = 0;
		
		size = (int) Math.abs(8.0f * sigma + 1.0f);
		if (size % 2 == 0) size++; 
		float[][] gkernel = new float[size][size];
		
		centre = (int) Math.floor((double) size/2);		
		for (int y=0; y<size; y++) {
			for(int x=0; x<size; x++) {
				dist = Math.abs(x-centre) * Math.abs(x-centre) + Math.abs(y-centre) * Math.abs(y-centre);
				sigma2 = 2*sigma*sigma;
			
				e = (float) Math.exp(-dist / sigma2);

				gkernel[y][x] = e;
				sum += e;
			}
		}
		
		for (int y=0; y<size; y++) {
			for(int x=0; x<size; x++) {
				gkernel[y][x] /= sum;
			}
		}
		
		return gkernel;
	}
}

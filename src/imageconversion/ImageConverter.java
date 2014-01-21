package imageconversion;

// Standard imports:
import java.awt.image.*;
import java.io.*;
import java.util.Arrays;

// External imports:
import com.sun.image.codec.jpeg.*;

// Project imports:
import exceptions.*;


/**
 * This is a class for converting images.
 *
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 * @see java.awt.image.BufferedImage
 */
public class ImageConverter {
    
    /**
     * Predefined pixelbuffers.
     */
    private int[] pixelbuffer1;
    private int[] pixelbuffer2;


    /**
     * Size of predefined pixelbuffers.
     */
    private int buffersize;

    
    /**
     * The constructor
     * Sets the buffersize to zero.
     */
    public ImageConverter() {
	buffersize = 0;
    }
    

    /**
     * Allocates larger buffers if needed.
     *
     *@param size the requested buffersize.
     */
    private void initiateBuffers(int size) {
	if (buffersize < size) {
	    buffersize = size;
	    pixelbuffer1 = new int[buffersize];
	    pixelbuffer2 = new int[buffersize];
	}
    }


    /**
     * Creates a black image of the given size.
     *
     * @param width  the width of the requested image.
     * @param height  the height of the requested image.
     * @return a black image
     */
    public BufferedImage createBlack(int width, int height) {
	
	int size = height * width;
	initiateBuffers(size);
	BufferedImage blkImg = 
		new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); 
	Arrays.fill(pixelbuffer1, 0);
	blkImg.setRGB(0, 0, width, height, pixelbuffer1, 0, width);
	return blkImg;
    }

    
    /**
     * Encodes a BufferedImage to JPEG byte array using given quality.
     * Quality is a value between 0 and 1. Higher value is better quality
     * but also larger byte array.
     *
     * @see #decodeJPEG
     *
     * @param img an image that will be encoded
     * @param quality the quality of the encoding
     * @throws IOException
     * @return a JPEG byte array 
     */
    public byte[] encodeJPEG(BufferedImage img, float quality) 
    throws IOException {
	
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
	JPEGEncodeParam param = JPEGCodec.getDefaultJPEGEncodeParam(img);
	param.setQuality(quality, false); 
	encoder.encode(img, param);
	byte[] bytearray = out.toByteArray();
	out.close();
	return bytearray;
    }
    

    /**
     * Decodes a JPEG byte array into a BufferedImage.
     *
     * @see #encodeJPEG
     * 
     * @param imgdata a JPEG byte array
     * @throws IOException
     * @return a Buffered Image
     */
    public BufferedImage decodeJPEG(byte[] imgdata) 
    throws IOException {
	
	ByteArrayInputStream in = new ByteArrayInputStream(imgdata);
	JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(in);
	BufferedImage img = decoder.decodeAsBufferedImage();
	in.close();
	return img;
    }
    
    
    /**
     * Creates a BufferedImage as the new image subtracted by the old image.
     * This is for creating JPEG-friendly images with large one-color areas.
     *
     * @see #restoreDelta
     * 
     * @param img1 the original image
     * @param img2 the new image
     * @throws IncompatibleImageException if the size of the images are unequal
     * @return the difference image
     */
    public BufferedImage createDelta(BufferedImage oldimg, BufferedImage newimg) 
    throws IncompatibleImageException {		

	int height = oldimg.getHeight();
	int width = oldimg.getWidth();
	int size = height * width;

	if (height != newimg.getHeight() || width != newimg.getWidth())
	    throw new IncompatibleImageException();

	initiateBuffers(size);

	BufferedImage difimg = 
	    new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

	oldimg.getRGB(0, 0, width, height, pixelbuffer1, 0, width);
	newimg.getRGB(0, 0, width, height, pixelbuffer2, 0, width);

	// for each color layer: dif = ((new[7 bits] - old[7 bits]) + 256) / 2

	for (int i = 0; i < size; i++) pixelbuffer1[i] = (0xfe000000 | 
			(((0x00fe0000 & pixelbuffer2[i]) - 
			(0x00fe0000 & pixelbuffer1[i])) + 0x01000000) | 
			(((0x0000fe00 & pixelbuffer2[i]) - 
			(0x0000fe00 & pixelbuffer1[i])) + 0x00010000) | 
			(((0x000000fe & pixelbuffer2[i]) - 
			(0x000000fe & pixelbuffer1[i])) + 0x00000100)) >> 1;

	difimg.setRGB(0, 0, width, height, pixelbuffer1, 0, width);

	return difimg;
    }
    

    /**
     * Restores a BufferedImage from an old image and a difference image.
     * This method only makes sense when used on a difference image
     * created by the createDelta method over the same old image that
     * is now passed to this method. 
     *
     * @see #createDelta
     * 
     * @param img1 the original image
     * @param img2 the new image
     * @throws IncompatibleImageException if the size of the images are unequal
     * @return the difference image
     */
    public BufferedImage restoreDelta(BufferedImage oldimg, BufferedImage difimg) 
    throws IncompatibleImageException {		

	int height = oldimg.getHeight();
	int width = oldimg.getWidth();
	int size = height * width;

	if (height != difimg.getHeight() || width != difimg.getWidth())
	    throw new IncompatibleImageException();

	initiateBuffers(size);

	BufferedImage resimg = 
	    new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

	oldimg.getRGB(0, 0, width, height, pixelbuffer1, 0, width);
	difimg.getRGB(0, 0, width, height, pixelbuffer2, 0, width);

	// for each color layer: new = ((dif * 2 - 256) + old[7 bits])

	int r, g, b;
	for (int i = 0; i < size; i++) {
	
		r = (((0x00ff0000 & pixelbuffer2[i]) << 1) - 0x01000000) 
		    + (0x00fe0000 & pixelbuffer1[i]);

		g = (((0x0000ff00 & pixelbuffer2[i]) << 1) - 0x00010000) 
		    + (0x0000fe00 & pixelbuffer1[i]);

		b = (((0x000000ff & pixelbuffer2[i]) << 1) - 0x00000100) 
		    + (0x000000fe & pixelbuffer1[i]);

		// We have to clip since the image probably have been JPEG'd
		// and thus risk to fall out of the interval.

		if (r > 0x00ff0000) r = 0x00ff0000;
		if (g > 0x0000ff00) g = 0x0000ff00;
		if (b > 0x000000ff) b = 0x000000ff;
		if (r < 0x00000000) r = 0x00000000;
		if (g < 0x00000000) g = 0x00000000;
		if (b < 0x00000000) b = 0x00000000;

		pixelbuffer1[i] = 0xff000000 | r | g | b;
	}	
	resimg.setRGB(0, 0, width, height, pixelbuffer1, 0, width);	
	return resimg;	
    }
}

















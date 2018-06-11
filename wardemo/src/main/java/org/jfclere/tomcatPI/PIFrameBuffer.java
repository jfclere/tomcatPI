package org.jfclere.tomcatPI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class PIFrameBuffer {

	RandomAccessFile file;
	int length = 64 * 2; // 8 x 8 and 2 bytes per pixel.
	
	public PIFrameBuffer(String name, String permission) throws FileNotFoundException {
		file = new RandomAccessFile(name, permission);
	}
	public void writepix(int x, int y, short color) throws IOException {
		// byte[] b = new byte[] { (byte) color };
		file.seek((x*8+y)*2);
		file.writeShort(color);
	}
	public void clear(int color) throws IOException {
	     for (int i = 0; i < length; i++) {
	       	file.writeByte(0xAA);
	     }
	}
	
    public static short color(int red, int green, int blue) {
        if (red > 31) {
            red = 31;
        }
        
        if (green > 63) {
            green = 63;
        }
        
        if (blue > 31) {
            blue = 31;
        }
        
        return (short) ((blue<<8)+red*8+green/8+((green%8)<<13)); 
    }
 
	/* read the pixel */
	public short readpix(int x, int y) throws IOException {
		file.seek((x*8+y)*2);
		return file.readShort();
	}
	
	/* get the red from the short pixel */
	public int getRed(short pixel) {
		int p = pixel & 0xFFFF;
		int cache = 0x00F8;
		return (p & cache) / 8;
	}
	/* get the blue */
	public int getBlue(short pixel) {
		int cache = 0x1F00;
		return (pixel & cache) >> 8;
	}
	/* get the green */
	public int getGreen(short pixel) {
		int cache = 0xE000;
		int green = (pixel & cache) >> 13;
		cache = 0x0007;
		return (pixel & cache) * 8 + green;
	}
	
	public static void main(String[] args) throws IOException {
		
	}
	
	private void close() throws IOException {
		file.close();
		
	}

}

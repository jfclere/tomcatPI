package org.jfclere.tomcatPI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
// import java.nio.channels.FileChannel;

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
	/* Convert the color to the corresponding short RGB565 */
	public short color(int red, int green, int blue) {
		if (red > 31)
			red = 31;
		if (green>63)
			green = 63;
		if  (blue>31)
			blue = 31;
		// return (short) (blue*256+red*16+green/8+(green%8)*512);
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
		/*
		 * [root@pc-151 tomcatPI]# ls -lt /sys/class/graphics/fb*
		 * lrwxrwxrwx 1 root root 0 Oct  9 10:01 /sys/class/graphics/fb1 -> ../../devices/platform/soc/3f804000.i2c/i2c-1/1-0046/rpi-sense-fb/graphics/fb1
		 * lrwxrwxrwx 1 root root 0 Feb 11  2016 /sys/class/graphics/fb0 -> ../../devices/platform/soc/soc:fb/graphics/fb0
		 * lrwxrwxrwx 1 root root 0 Feb 11  2016 /sys/class/graphics/fbcon -> ../../devices/virtual/graphics/fbcon
         * so we use fb1 ...
		 */
		PIFrameBuffer pi = new PIFrameBuffer("/dev/fb1", "rw");
		pi.clear(0);
		pi.writepix(0, 0, pi.color(0,0,31));
		pi.writepix(4, 4, pi.color(0,63,0)); /* green */
		pi.writepix(7, 6, pi.color(31,0,0));
		pi.writepix(7, 7, pi.color(7,0,0)); /* they short be red... */
		
		short pixel = pi.readpix(0, 0);
		System.out.println("blue: " + pi.getRed(pixel) + "  " + pi.getBlue(pixel) + " " + pi.getGreen(pixel));

		pixel = pi.readpix(4, 4);
		System.out.println("green: " + pi.getRed(pixel) + "  " + pi.getBlue(pixel) + " " + pi.getGreen(pixel));
		
		pixel = pi.readpix(7,7);
		System.out.println("red: " + pi.getRed(pixel) + "  " + pi.getBlue(pixel) + " " + pi.getGreen(pixel));
		
		int x = 0;
		int y = 0;
		for (int i=0; i < 32; i++) {
			pi.writepix(x, y, pi.color(0,i,0));
			x++;
			if (x==8) {
				y++;
				x = 0;
			}
		}

		/*
		RandomAccessFile file = new RandomAccessFile("/dev/fb1", "rw");
		long length = file.length();
		System.out.println("file is " + length);
        length = 64 * 2; // 8 x 8 and 2 bytes per pixel.
        FileChannel chan = file.getChannel();
        for (int i = 0; i < length; i++) {
        	file.writeByte(0xAA);
        }
        chan.truncate(length);
        chan.position(0);
        System.out.println("size file is " + chan.size());
		// MappedByteBuffer fd = chan.map(FileChannel.MapMode.READ_WRITE, (long) 0, length);
		
		for (int i = 0; i < length; i++) {
			// System.out.println("pixel " + i + " : " + fd.get());
			System.out.println("pixel " + i + " : " + file.readByte());
		}
		*/
		pi.close();
	}
	private void close() throws IOException {
		file.close();
		
	}

}

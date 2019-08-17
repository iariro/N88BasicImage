package kumagai.n88basicimage;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

public class N88BasicImage
{
	static final int [] colors =
	{
		0x000000, 0x0000ff, 0x00ff00, 0x00ffff, 0xff0000, 0xff00ff, 0xffff00, 0xffffff,
		0x777777, 0x0000aa, 0x00aa00, 0x00aaaa, 0xaa0000, 0xaa00aa, 0xaaaa00, 0xaaaaaa
	};

	/**
	 * @param args [0]=入力ファイルパス [1]=出力ファイルパス
	 * @throws IOException
	 */
	static public void main(String[] args)
		throws IOException
	{
		int adjust = 1;
		BufferedImage image = ImageIO.read(new File(args[0]));
		N88BasicImage basicImage = new N88BasicImage(image.getWidth()-adjust, image.getHeight());
		System.out.printf("%dx%d\n", image.getWidth()-adjust, image.getHeight());
		for (int x=0 ; x<image.getWidth()-adjust ; x++)
		{
			for (int y=0 ; y<image.getHeight() ; y++)
			{
				int c = image.getRGB(x, y);
				int index = getNearestColorIndex(c);
				basicImage.putPixel(x, y, index);
			}
		}

		basicImage.dump(new FileOutputStream(args[1]));
		System.out.printf("written %d bytes.\n", basicImage.bytes.length);
	}

	/**
	 * 一番近い色のインデックスを求める
	 * @param color 色
	 * @return 色のインデックス
	 */
	static private int getNearestColorIndex(int color)
	{
		Integer index = null;
		Integer min = null;
		for (int i=0 ; i<colors.length ; i++)
		{
			int diff = getDiff(color, colors[i]);
			if (min == null || min > diff)
			{
				min = diff;
				index = i;
			}
		}
		return index;
	}

	/**
	 * ２つの色の差を求める
	 * @param color1 色１
	 * @param color2 色２
	 * @return 色の差
	 */
	static private int getDiff(int color1, int color2)
	{
		int r = Math.abs(((color1 & 0xff0000) - (color2 & 0xff0000)) >> 16);
		int g = Math.abs(((color1 & 0x00ff00) - (color2 & 0x00ff00)) >> 8);
		int b = Math.abs(((color1 & 0x0000ff) - (color2 & 0x0000ff)));

		return r + g + b;
	}

	int width;
	int height;
	int byteNumPerLine;
	byte [] bytes;

	public N88BasicImage(int width, int height)
	{
		this.width = width;
		this.height = height;
		this.byteNumPerLine = ((width + 7) / 8);
		System.out.println(this.byteNumPerLine);
		this.bytes = new byte [((4 + byteNumPerLine * 4 * height + 1) / 2) * 2];

		this.bytes[0] = (byte)(width / 0x100);
		this.bytes[1] = (byte)(width % 0x100);
		this.bytes[2] = (byte)(height / 0x100);
		this.bytes[3] = (byte)(height % 0x100);
	}

	/**
	 * 点を配置する
	 * @param x X座標
	 * @param y Y座標
	 * @param index インデックス値
	 */
	public void putPixel(int x, int y, int index)
	{
		int bit = 1 << (7 - (x % 8));
		int xoffset;
		if ((x % 16) < 8)
		{
			xoffset = (x / 8) + 1;
		}
		else
		{
			xoffset = (x / 8) - 1;
		}

		if ((index & 0x01) > 0)
		{
			bytes[4 + xoffset + byteNumPerLine * 4 * y] |= bit;
		}
		if ((index & 0x02) > 0)
		{
			bytes[4 + xoffset + byteNumPerLine * (4 * y + 1)] |= bit;
		}
		if ((index & 0x04) > 0)
		{
			bytes[4 + xoffset + byteNumPerLine * (4 * y + 2)] |= bit;
		}
		if ((index & 0x08) > 0)
		{
			bytes[4 + xoffset + byteNumPerLine * (4 * y + 3)] |= bit;
		}
	}

	/**
	 * ファイル出力
	 * @param stream ファイルストリーム
	 */
	public void dump(OutputStream stream)
	{
		PrintWriter writer =
			new PrintWriter(
				new BufferedWriter(
					new OutputStreamWriter(stream)));

		for (int i=0 ; i<bytes.length ; i+=2)
		{
			writer.printf("%02x%02x", bytes[i], bytes[i+1]);
			if (i % 16 == 14)
			{
				writer.println(",");
			}
			else
			{
				writer.print(",");
			}
		}
		writer.close();
	}
}

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
		BufferedImage image = ImageIO.read(new File(args[0]));
		N88BasicImage basicImage = new N88BasicImage(image.getWidth(), image.getHeight());
		System.out.printf("%dx%d\n", image.getWidth(), image.getHeight());
		for (int x=0 ; x<image.getWidth() ; x++)
		{
			for (int y=0 ; y<image.getHeight() ; y++)
			{
				int c = image.getRGB(x, y);
				int index = getNearestColorIndex(c);
				basicImage.putPixel(x, y, index);
			}
		}

		basicImage.dump(new FileOutputStream(args[1]));
		System.out.printf("written %d words.\n", basicImage.words.length);
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
	short [] words;

	public N88BasicImage(int width, int height)
	{
		this.width = width;
		this.height = height;
		this.byteNumPerLine = ((width + 7) / 8);
		this.words = new short [2 + (byteNumPerLine * 4 * height) / 2];

		this.words[0] = (short)width;
		this.words[1] = (short)height;
	}

	/**
	 * 点を配置する
	 * @param x X座標
	 * @param y Y座標
	 * @param index インデックス値
	 */
	public void putPixel(int x, int y, int index)
	{
		int bit;
		if (x % 16 < 8)
		{
			bit = 1 << (7 - x % 16);
		}
		else
		{
			bit = 1 << (15 - (x % 16 - 8));
		}

		if ((index & 0x01) > 0)
		{
			words[2 + (x / 8 + byteNumPerLine * 4 * y) / 2] |= bit;
		}
		if ((index & 0x02) > 0)
		{
			words[2 + (x / 8 + byteNumPerLine * (4 * y + 1)) / 2] |= bit;
		}
		if ((index & 0x04) > 0)
		{
			words[2 + (x / 8 + byteNumPerLine * (4 * y + 2)) / 2] |= bit;
		}
		if ((index & 0x08) > 0)
		{
			words[2 + (x / 8 + byteNumPerLine * (4 * y + 3)) / 2] |= bit;
		}
	}

	/**
	 * ファイル出力
	 * @param stream ファイルストリーム
	 */
	public void dump(OutputStream stream)
	{
		PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(stream)));
		for (int i=0 ; i<words.length ; i++)
		{
			writer.printf("%04x", words[i]);
			if (i < words.length-1)
			{
				writer.print(",");
			}
			if (i % 8 == 7)
			{
				writer.println();
			}
		}
		writer.close();
	}
}

package kumagai.n88basicimage;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

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
		if (args[0].equals("encode"))
		{
			encode(new String [] { args[1], args[2], args[3] });
		}
		if (args[0].equals("decode"))
		{
			decode(new String [] { args[1], args[2] });
		}
	}

	static public void decode(String[] args)
		throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(args[0]));
		PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]))));

		int count = 0;
		ArrayList<String> buffer = new ArrayList<String>();
		String str;
		while ((str = reader.readLine()) != null)
		{
			String [] words = str.split(",");
			for (String word : words)
			{
				if (count >= 2)
				{
					buffer.add(word);

					if (buffer.size() == 4 * 4)
					{
						for (int i=0 ; i<64 ; i++)
						{
							int j = 15 - (i + 8) % 16;

							int a = (Integer.parseInt(buffer.get(i / 16     ), 16) & (1 << j)) > 0 ? 1 : 0;
							int b = (Integer.parseInt(buffer.get(i / 16 +  4), 16) & (1 << j)) > 0 ? 2 : 0;
							int c = (Integer.parseInt(buffer.get(i / 16 +  8), 16) & (1 << j)) > 0 ? 4 : 0;
							int d = (Integer.parseInt(buffer.get(i / 16 + 12), 16) & (1 << j)) > 0 ? 8 : 0;

							writer.printf("<span style='color:#%06x;'>@</span>", colors[a + b + c + d]);
						}
						writer.println("<br>");
						buffer.clear();
					}
				}
				count++;
			}
		}
		reader.close();
		writer.close();
	}

	static public void encode(String[] args)
		throws IOException
	{
		int adjust = 1;
		BufferedImage image = ImageIO.read(new File(args[0]));
		N88BasicImage basicImage = new N88BasicImage(image.getWidth()-adjust, image.getHeight());
		System.out.printf("%dx%d\n", image.getWidth()-adjust, image.getHeight());
		ColorStatistics colorStatistics = new ColorStatistics();
		for (int x=0 ; x<image.getWidth()-adjust ; x++)
		{
			for (int y=0 ; y<image.getHeight() ; y++)
			{
				int c = image.getRGB(x, y);
				colorStatistics.put12bitColor(c);
				int index = getNearestColorIndex(c);
				basicImage.putPixel(x, y, index);
			}
		}
		System.out.println("color num=" + colorStatistics.size());
		colorStatistics.dump(new FileOutputStream(args[2]));

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

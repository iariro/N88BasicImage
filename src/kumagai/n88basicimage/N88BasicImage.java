package kumagai.n88basicimage;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * N88-BASICのPUT/GET形式の画像データ
 */
public class N88BasicImage
{
	int width;
	int height;
	int byteNumPerLine;
	Color12bitList colors;
	byte [] bytes;

	/**
	 * メンバーを初期化する
	 * @param width 幅
	 * @param height 高さ
	 */
	public N88BasicImage(int width, int height)
	{
		this.width = width;
		this.height = height;
		this.byteNumPerLine = ((width + 7) / 8);
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
	 * 画像ファイル出力
	 * @param stream ファイルストリーム
	 */
	public void dump(OutputStream stream)
	{
		PrintWriter writer =
			new PrintWriter(
				new BufferedWriter(
					new OutputStreamWriter(stream)));

		for (int i=0 ; i<16 ; i++)
		{
			if (i > 0)
			{
				writer.print(',');
			}

			writer.printf("%03x", colors.get(i).getGrb12bit());
		}
		writer.println();

		for (int i=0 ; i<bytes.length ; i+=2)
		{
			writer.printf("%02x%02x", bytes[i], bytes[i+1]);
			if (i % 16 == 14 ||
				i + 2 >= bytes.length)
			{
				writer.println();
			}
			else
			{
				writer.print(",");
			}
		}
		writer.close();
	}

	/**
	 * 確認用に画像を@マークでHTML出力
	 * @param filename ファイル名
	 */
	public void dumpHtml(String filename)
		throws FileNotFoundException
	{
		PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename))));
		writer.println("<table>");
		for (int i=0 ; i<colors.size() ; i++)
		{
			writer.printf(
				"<tr><td>%d</td><td>%06x</td><td><span style='color:#%06x;'>@</span></td></tr>",
				i,
				colors.get(i).getRgb24bit(),
				colors.get(i).getRgb24bit());
		}
		writer.println("</table>");

		for (int i=0 ; i<bytes.length ; i+=32)
		{
			if (i + 32 <= bytes.length)
			for (int j=0 ; j<width ; j++)
			{
				int k = 7 - j % 8;

				int a = (bytes[4 + i + j / 8 + (j % 16 < 8 ? 1 : -1)] & (1 << k)) > 0 ? 1 : 0;
				int b = (bytes[4 + i + j / 8 + (j % 16 < 8 ? 1 : -1) + 8] & (1 << k)) > 0 ? 2 : 0;
				int c = (bytes[4 + i + j / 8 + (j % 16 < 8 ? 1 : -1) + 16] & (1 << k)) > 0 ? 4 : 0;
				int d = (bytes[4 + i + j / 8 + (j % 16 < 8 ? 1 : -1) + 24] & (1 << k)) > 0 ? 8 : 0;

				writer.printf("<span style='color:#%06x;'>@</span>", colors.get(a + b + c + d).getRgb24bit());
			}
			writer.println("<br>");
		}
		writer.close();
	}
}

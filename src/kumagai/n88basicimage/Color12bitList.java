package kumagai.n88basicimage;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * 12bit色値のリスト
 */
public class Color12bitList
	extends ArrayList<Color12bit>
{
	/*
	 * 
		static final int [] colors =
		{
			0x000000, 0x0000ff, 0x00ff00, 0x00ffff, 0xff0000, 0xff00ff, 0xffff00, 0xffffff,
			0x777777, 0x0000aa, 0x00aa00, 0x00aaaa, 0xaa0000, 0xaa00aa, 0xaaaa00, 0xaaaaaa
		};

	 */

	/**
	 * 一番近い色のインデックスを求める
	 * @param rgb24 色
	 * @return 色のインデックス
	 */
	public int getNearestColorIndex(int rgb24)
	{
		Integer index = null;
		Integer min = null;
		Color12bit grb12 = new Color12bit(Color12bit.fromColor24bit(rgb24));
	
		for (int i=0 ; i<size() ; i++)
		{
			int r = Math.abs(grb12.r - get(i).r);
			int g = Math.abs(grb12.g - get(i).g);
			int b = Math.abs(grb12.b - get(i).b);
			int diff = r + g + b;
			if (min == null || min > diff)
			{
				min = diff;
				index = i;
			}
		}
		return index;
	}

	/**
	 * 色リストをHTML出力する
	 * @param stream HTMLを出力するストリーム
	 */
	public void dump(OutputStream stream)
	{
		PrintWriter writer =
			new PrintWriter(
				new BufferedWriter(
					new OutputStreamWriter(stream)));

		writer.printf("<table>");
		writer.printf("<tr><th>index</th><th>color</th><th>num</th><th width='100px'>color</th></tr>\n");
		for (int i=0; i<size() ; i++)
		{
			writer.printf("<tr><td>%d</td><td>%06x</td><td>%d</td><td style='background-color:#%06x;'></td></tr>\n", i, get(i).getRgb24bit(), 0, get(i).getRgb24bit());
		}
		writer.printf("</table>");
		writer.close();
	}
}

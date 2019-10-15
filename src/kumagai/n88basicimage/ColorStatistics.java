package kumagai.n88basicimage;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorStatistics
	extends HashMap<Integer, Integer>
{
	public void put12bitColor(int c)
	{
		int r = (c & 0xff0000) >> 16;
		int g = (c & 0xff00) >> 8;
		int b = (c & 0xff);

		int rgb12 =
			(((r & 0xc0) + 0x30) << 16) +
			(((g & 0xc0) + 0x30) << 8) +
			((b & 0xc0) + 0x30);

		if (!containsKey(rgb12))
		{
			put(rgb12, 0);
		}
		put(rgb12, get(rgb12) + 1);
	}

	public void dump(List<Map.Entry<Integer, Integer>> mapOrderList, OutputStream stream)
	{
		PrintWriter writer =
			new PrintWriter(
				new BufferedWriter(
					new OutputStreamWriter(stream)));

		writer.printf("<table>");
		writer.printf("<tr><th>index</th><th>color</th><th>num</th><th width='100px'>color</th></tr>\n");
		for (int i=0; i<mapOrderList.size() ; i++)
		{
			writer.printf("<tr><td>%d</td><td>%06x</td><td>%d</td><td style='background-color:#%06x;'></td></tr>\n", i, mapOrderList.get(i).getKey(), mapOrderList.get(i).getValue(), mapOrderList.get(i).getKey());
		}
		writer.printf("</table>");
		writer.close();
	}

	public List<Map.Entry<Integer, Integer>> getSortedList()
	{
		List<Map.Entry<Integer, Integer>> mapOrderList = new ArrayList<>(entrySet());

		// 使用頻度の高い順にソート
		Collections.sort(
			mapOrderList,
			new Comparator<Map.Entry<Integer, Integer>>()
			{
				public int compare(Map.Entry<Integer, Integer> object1, Map.Entry<Integer, Integer> object2)
				{
					return - object1.getValue().compareTo(object2.getValue());
				}
			});

		// １６色に絞る
		while (mapOrderList.size() > 16)
		{
			mapOrderList.remove(16);
		}

		// RGB値が低い順にソート
		Collections.sort(
			mapOrderList,
			new Comparator<Map.Entry<Integer, Integer>>()
			{
				public int compare(Map.Entry<Integer, Integer> color1, Map.Entry<Integer, Integer> color2)
				{
					int rgb1 =
						((color1.getKey() & 0xff0000) >> 16) +
						((color1.getKey() & 0xff00) >> 8) +
						(color1.getKey() & 0xff);
					int rgb2 =
						((color2.getKey() & 0xff0000) >> 16) +
						((color2.getKey() & 0xff00) >> 8) +
						(color2.getKey() & 0xff);
					return Integer.compare(rgb1, rgb2);
				}
			});

		return mapOrderList;
	}
}

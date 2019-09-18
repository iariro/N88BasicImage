package kumagai.n88basicimage;

import java.io.*;
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
			((r & 0xc0) << 16) +
			((g & 0xc0) << 8) +
			(b & 0xc0);

		if (!containsKey(rgb12))
		{
			put(rgb12, 0);
		}
		put(rgb12, get(rgb12) + 1);
	}

	public void dump(OutputStream stream)
	{
		List<Map.Entry<Integer, Integer>> mapOrderList = new ArrayList<>(entrySet());
		Collections.sort(
			mapOrderList,
			new Comparator<Map.Entry<Integer, Integer>>()
			{
		        public int compare(Map.Entry<Integer, Integer> object1, Map.Entry<Integer, Integer> object2)
		        {
		            return - object1.getValue().compareTo(object2.getValue());
		        }
		    });

		PrintWriter writer =
			new PrintWriter(
				new BufferedWriter(
					new OutputStreamWriter(stream)));

		int i=0;
		writer.printf("<table>");
		writer.printf("<tr><th>index</th><th>color</th><th>num</th><th width='100px'>color</th></tr>\n");
		for (Map.Entry<Integer, Integer> entry : mapOrderList)
		{
			writer.printf("<tr><td>%d</td><td>%06x</td><td>%d</td><td style='background-color:#%06x;'></td></tr>\n", i, entry.getKey(), entry.getValue(), entry.getKey());
			i++;
		}
		writer.printf("</table>");
		writer.close();
	}
}

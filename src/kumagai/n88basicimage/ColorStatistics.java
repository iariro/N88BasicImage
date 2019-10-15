package kumagai.n88basicimage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 色情報統計
 */
public class ColorStatistics
	extends HashMap<Integer, Integer>
{
	/**
	 * 24bit色を受け12bit色として１件追加する
	 * @param rgb24 24bit色
	 */
	public void put24bitColorAs12bit(int rgb24)
	{
		int grb12 = Color12bit.fromColor24bit(rgb24);

		if (!containsKey(grb12))
		{
			// 初出

			put(grb12, 0);
		}
		put(grb12, get(grb12) + 1);
	}

	/**
	 * 使用頻度の高い１６色を得る
	 * @return 使用頻度の高い１６色
	 */
	public Color12bitList getTop16Colors()
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

		// 上位16件のみでリスト作成
		Color12bitList colorTop16 = new Color12bitList();
		for (int i=0 ; i<16 ; i++)
		{
			if (i < mapOrderList.size())
			{
				colorTop16.add(new Color12bit(mapOrderList.get(i).getKey()));
			}
			else
			{
				colorTop16.add(new Color12bit(0));
			}
		}

		// RGB値が低い順にソート
		Collections.sort(
			colorTop16,
			new Comparator<Color12bit>()
			{
				public int compare(Color12bit color1, Color12bit color2)
				{
					int grb1 = color1.g + color1.r + color1.b;
					int grb2 = color2.g + color2.r + color2.b;
					return Integer.compare(grb1, grb2);
				}
			});

		return colorTop16;
	}
}

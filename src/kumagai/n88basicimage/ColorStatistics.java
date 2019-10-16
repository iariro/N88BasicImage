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
	 * @param coverageThresh 色網羅率下限(%)
	 * @return 使用頻度の高い１６色
	 */
	public Color12bitList getTop16Colors(int coverageThresh)
	{
		List<Map.Entry<Integer, Integer>> colors = new ArrayList<>(entrySet());

		// 使用頻度の高い順にソート
		Collections.sort(
			colors,
			new Comparator<Map.Entry<Integer, Integer>>()
			{
				public int compare(Map.Entry<Integer, Integer> object1, Map.Entry<Integer, Integer> object2)
				{
					return - object1.getValue().compareTo(object2.getValue());
				}
			});

		// 全色ごとの差分を調べる
		ArrayList<IndexPair> diffs = new ArrayList<>();
		for (int i=0 ; i<colors.size()/4 ; i++)
		{
			for (int j=i+1 ; j<colors.size() ; j++)
			{
				int diff =
					Math.abs(((colors.get(i).getKey() & 0xf00) >> 8) - ((colors.get(j).getKey() & 0xf00) >> 8)) +
					Math.abs(((colors.get(i).getKey() & 0x0f0) >> 4) - ((colors.get(j).getKey() & 0x0f0) >> 4)) +
					Math.abs(((colors.get(i).getKey() & 0x00f)) - ((colors.get(j).getKey() & 0x00f)));

				diffs.add(new IndexPair(i, j, diff));
			}
		}
		// 差分が小さい＝色が近い順にソート
		Collections.sort(
				diffs,
			new Comparator<IndexPair>()
			{
				public int compare(IndexPair color1, IndexPair color2)
				{
					return Integer.compare(color1.diff, color2.diff);
				}
			});

		int reducedColor = colors.size();
		Color12bitList colorTop16;
		do
		{
			// 上位16件のみでリスト作成
			colorTop16 = new Color12bitList();
			colorTop16.totalColor = colors.size();
			for (int i=0 ; i<colors.size() ; i++)
			{
				if (colors.get(i) == null)
				{
					continue;
				}

				colorTop16.totalPixel += colors.get(i).getValue();

				if (colorTop16.size() < 16)
				{
					colorTop16.coveredPixel += colors.get(i).getValue();

					if (i < colors.size())
					{
						colorTop16.add(new Color12bit(colors.get(i).getKey()));
					}
					else
					{
						colorTop16.add(new Color12bit(0));
					}
				}
			}

			if (colorTop16.getCoveredRadio() >= 97)
			{
				colorTop16.reducedColor = reducedColor;
				break;
			}

			int index1 = diffs.get(0).index1;
			int index2 = diffs.get(0).index2;
			if (colors.get(index1) != null && colors.get(index2) != null)
			{
				colors.get(index1).setValue((Integer)(colors.get(index1).getValue() + colors.get(index2).getValue()));
				colors.set(index2, null);
				reducedColor--;
			}
			diffs.remove(0);
		}
		while (true);

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

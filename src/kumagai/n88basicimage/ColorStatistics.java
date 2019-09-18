package kumagai.n88basicimage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorStatistics
	extends HashMap<Integer, Integer>
{
	public void dump()
	{
		List<Map.Entry<Integer, Integer>> mapOrderList = new ArrayList<>(entrySet());
		Collections.sort(
			mapOrderList,
			new Comparator<Map.Entry<Integer, Integer>>()
			{
		        public int compare(Map.Entry<Integer, Integer> object1, Map.Entry<Integer, Integer> object2)
		        {
		            return object1.getValue().compareTo(object2.getValue());
		        }
		    });
		for (Map.Entry<Integer, Integer> entry : mapOrderList)
		{
			System.out.printf("%x : %d\n", entry.getKey(), entry.getValue());
		}
	}
}

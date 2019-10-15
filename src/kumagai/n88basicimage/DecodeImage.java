package kumagai.n88basicimage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 確認用に画像を@マークでHTML出力
 */
public class DecodeImage
{
	/**
	 * エントリポイント
	 * @param args [0]=画像ファイルのパス [1]=出力するHTMLファイルのパス
	 */
	public static void main(String[] args)
		throws IOException
	{
		int count = 0;
		int width = 0;
		int height = 0;
		String line;
		Color12bitList colors = new Color12bitList();
		ArrayList<Byte> bytes = new ArrayList<Byte>();

		// ファイル読み込み
		BufferedReader reader = new BufferedReader(new FileReader(args[0]));
		while ((line = reader.readLine()) != null)
		{
			String [] words = line.split(",");
			for (String word : words)
			{
				if (count < 16)
				{
					colors.add(new Color12bit(Integer.parseInt(word, 16)));
				}
				else
				{
					int b2 = Integer.parseInt(word, 16);
					bytes.add((byte)(b2 >> 8));
					bytes.add((byte)(b2 & 0xff));

					if (count == 16)
					{
						width = b2;
					}
					else if (count == 17)
					{
						height = b2;
					}
				}
				count++;
			}
		}
		reader.close();

		N88BasicImage image = new N88BasicImage(width, height);
		image.colors = colors;
		for (int i=0 ; i<bytes.size() ; i++)
		{
			image.bytes[i] = bytes.get(i);
		}

		image.dumpHtml(args[1]);
	}
}

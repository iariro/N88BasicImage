package kumagai.n88basicimage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 画像データをN88-BASICのPUT/GET形式に変換
 */
public class EncodeImage
{
	/**
	 * エントリポイント
	 * @param args [0]=入力ファイルパス [1]=画像ファイルパス [2]=パレットリストファイルパス [3]=色網羅率(%)
	 */
	static public void main(String[] args)
		throws IOException
	{
		BufferedImage image = ImageIO.read(new File(args[0]));
		N88BasicImage basicImage = new N88BasicImage(image.getWidth(), image.getHeight());
		System.out.printf("%dx%d\n", image.getWidth(), image.getHeight());

		// 使用している色の統計をとる
		ColorStatistics colorStatistics = new ColorStatistics();
		for (int x=0 ; x<image.getWidth() ; x++)
		{
			for (int y=0 ; y<image.getHeight() ; y++)
			{
				colorStatistics.put24bitColorAs12bit(image.getRGB(x, y));
			}
		}

		// 16色に絞る
		Color12bitList top16colors = colorStatistics.getTop16Colors(Integer.valueOf(args[3]));
		System.out.printf(
			"color=%d->%d pixel coverage : %d / %d = %2.2f%% \n",
			top16colors.totalColor,
			top16colors.reducedColor,
			top16colors.coveredPixel,
			top16colors.totalPixel,
			top16colors.getCoveredRadio());
		basicImage.colors = top16colors;

		// 近い色のパレットを求めドットを打っていく
		for (int x=0 ; x<image.getWidth() ; x++)
		{
			for (int y=0 ; y<image.getHeight() ; y++)
			{
				basicImage.putPixel(x, y, top16colors.getNearestColorIndex(image.getRGB(x, y)));
			}
		}

		// 画像ファイル出力
		basicImage.dump(new FileOutputStream(args[1]));
		// パレット一覧出力
		top16colors.dump(new FileOutputStream(args[2]));

		System.out.printf("written %d bytes.\n", basicImage.bytes.length);
	}
}

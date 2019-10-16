package kumagai.n88basicimage;

/**
 * 12bit色
 */
public class Color12bit
{
	/**
	 * 24bit色を12色に変換
	 * @param rgb24 24bit色値
	 * @return 12色値
	 */
	static public int fromColor24bit(int rgb24)
	{
		int r = (rgb24 & 0xf00000) >> 20;
		int g = (rgb24 & 0xf000) >> 12;
		int b = (rgb24 & 0xf0) >> 4;

		return (g << 8) + (r << 4) + b;
	}

	public int r, g, b;

	/**
	 * 値をメンバーに割り当て
	 * @param grb12 12bit色値
	 */
	public Color12bit(int grb12)
	{
		this.r = (grb12 & 0x0f0) >> 4;
		this.g = (grb12 & 0xf00) >> 8;
		this.b = (grb12 & 0x00f);
	}

	/**
	 * 12bit色値を得る
	 * @return 12bit色値
	 */
	public int getGrb12bit()
	{
		return (g << 8) | (r << 4) | b;
	}

	/**
	 * 24bit色値として得る
	 * @return 24bit色値
	 */
	public int getRgb24bit()
	{
		return
			(r << 20) | (r << 16) |
			(g << 12) | (g << 8) |
			(b << 4) | b;
	}
}

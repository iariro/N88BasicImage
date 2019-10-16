package kumagai.n88basicimage;

/**
 * 色インデックスの対とその色の差分を保持
 */
public class IndexPair
{
	public int index1;
	public int index2;
	public int diff;

	/**
	 * メンバーに値をセット
	 * @param index1 インデックス１
	 * @param index2 インデックス２
	 * @param diff 色差分
	 */
	public IndexPair(Integer index1, Integer index2, int diff)
	{
		this.index1 = index1;
		this.index2 = index2;
		this.diff = diff;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return String.format("%d:%d=%d", index1, index2, diff);
	}
}

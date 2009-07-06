package org.nutz.doc;

public class IndexTable extends FinalLine {

	private int top;
	private int bottom;

	IndexTable(String s) {
		super();
		int pos = s.indexOf(',');
		if (pos <= 0) {
			top = 0;
			bottom = Integer.parseInt(s);
		} else {
			top = Integer.parseInt(s.substring(0, pos));
			bottom = Integer.parseInt(s.substring(pos + 1));
		}
	}

	public boolean atRight(int deep) {
		return deep < top;
	}

	public boolean isin(int deep) {
		return deep >= top && deep <= bottom;
	}

	public boolean atLeft(int deep) {
		return deep > bottom;
	}

	@Override
	public String toString() {
		return String.format("#index:%d,%d", top, bottom);
	}

}

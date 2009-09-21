package org.nutz.doc.meta;

public class ZStyle {

	private ZFont font;

	public ZFont getFont() {
		if (null == font)
			font = new ZFont();
		return font;
	}

	public void removeFont() {
		this.font = null;
	}

	public boolean hasFont() {
		return null != font;
	}

	/**
	 * It will replace item in style one by one. current style is higher
	 * priority.
	 * 
	 * @param s
	 * @return self
	 */
	public ZStyle merge(ZStyle s) {
		if (null != s && s.hasFont())
			font.merge(s.getFont());
		return this;
	}
}

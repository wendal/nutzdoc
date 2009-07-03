package org.nutz.doc.style;

public class Style {

	private FontStyle font;

	public FontStyle getFont() {
		if (null == font)
			font = new FontStyle();
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
	public Style merge(Style s) {
		if (null != s && s.hasFont())
			font.merge(s.getFont());
		return this;
	}
}

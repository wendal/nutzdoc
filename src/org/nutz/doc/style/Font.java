package org.nutz.doc.style;

import org.nutz.lang.Maths;

public class Font {

	public static void main(String[] args) {

	}

	public static final int BOLD = 1;
	public static final int ITALIC = 1 << 1;
	public static final int STRIKE = 1 << 2;

	private static enum TYPE {
		NORMAL, SUB, SUP
	}

	private int style;
	private TYPE type;

	public Font setStyle(int style) {
		this.style = style;
		return this;
	}

	public Font setAsSub() {
		type = TYPE.SUB;
		return this;
	}

	public Font setAsSup() {
		type = TYPE.SUP;
		return this;
	}

	public boolean isBold() {
		return Maths.isMask(style, BOLD);
	}

	public boolean isItalic() {
		return Maths.isMask(style, ITALIC);
	}

	public boolean isStrike() {
		return Maths.isMask(style, STRIKE);
	}

	public boolean isSub() {
		return type == TYPE.SUB;
	}

	public boolean isSup() {
		return type == TYPE.SUP;
	}

	public boolean isNormal() {
		return type == TYPE.NORMAL;
	}

	public boolean is(int style) {
		return Maths.isMask(this.style, style);
	}

	public void merge(Font f) {
		style |= f.style;
	}

}

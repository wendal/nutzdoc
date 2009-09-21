package org.nutz.doc.meta;

import org.nutz.lang.Maths;

public class ZFont {

	public static final int BOLD = 1;
	public static final int ITALIC = 1 << 1;
	public static final int STRIKE = 1 << 2;

	private static enum TYPE {
		NORMAL, SUB, SUP
	}

	private int style;
	private TYPE type;
	private ZColor color;

	public boolean hasColor() {
		return null != color;
	}

	public ZColor getColor() {
		if (!hasColor())
			color = new ZColor();
		return color;
	}

	public void setColor(String color) {
		this.color = new ZColor(color);
	}

	public void removeColor() {
		color = null;
	}

	public ZFont addStyle(int style) {
		this.style |= style;
		return this;
	}

	public ZFont setStyle(int style) {
		this.style = style;
		return this;
	}

	public ZFont setAsSub() {
		type = TYPE.SUB;
		return this;
	}

	public ZFont setAsSup() {
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

	public void merge(ZFont f) {
		style |= f.style;
	}

}

package org.nutz.doc.meta;

import org.nutz.lang.Maths;

import static org.nutz.doc.meta.ZD.*;

public class ZFont {

	public static final int BOLD = 1;
	public static final int ITALIC = 1 << 1;
	public static final int STRIKE = 1 << 2;
	public static final int UNDERLINE = 1 << 3;
	public static final int SUB = 1 << 4;
	public static final int SUP = 1 << 5;

	ZFont() {}

	private int style;
	private ZColor color;

	public boolean hasColor() {
		return null != color;
	}

	public ZColor getColor() {
		if (!hasColor())
			color = color();
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

	public boolean isBold() {
		return Maths.isMask(style, BOLD);
	}

	public boolean isItalic() {
		return Maths.isMask(style, ITALIC);
	}

	public boolean isUnderline() {
		return Maths.isMask(style, UNDERLINE);
	}

	public boolean isStrike() {
		return Maths.isMask(style, STRIKE);
	}

	public boolean isSub() {
		return Maths.isMask(style, SUB);
	}

	public boolean isSup() {
		return Maths.isMask(style, SUP);
	}

	public boolean isNormal() {
		return Maths.isNoMask(style, SUB | SUP);
	}

	public boolean is(int style) {
		return Maths.isMask(this.style, style);
	}

	public void merge(ZFont f) {
		style |= f.style;
	}

}

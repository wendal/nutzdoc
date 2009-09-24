package org.nutz.doc.meta;

import java.util.regex.Pattern;

public class ZEle {

	ZEle(String text) {
		style = new ZStyle();
		this.text = text;
	}

	private ZRefer src;
	private int height;
	private int width;

	public ZRefer getSrc() {
		return src;
	}

	public void setSrc(ZRefer src) {
		this.src = src;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	private ZBlock paragraph;

	public ZBlock getParagraph() {
		return paragraph;
	}

	public ZEle setParagraph(ZBlock paragraph) {
		this.paragraph = paragraph;
		return this;
	}

	private ZStyle style;

	public ZStyle getStyle() {
		return style;
	}

	public void setStyle(ZStyle style) {
		this.style = style;
	}

	public boolean hasStyle() {
		return null != style;
	}

	private ZRefer href;

	public ZRefer getHref() {
		return href;
	}

	public ZEle setHref(ZRefer refer) {
		this.href = refer;
		return this;
	}

	public boolean hasHref() {
		return null != href;
	}

	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isImage() {
		return null != src;
	}

	private static Pattern PTN = Pattern.compile("^[ \t]*[\r]?[\n]$");

	public boolean isBr() {
		if (null == text)
			return false;
		return PTN.matcher(text).find();
	}

}

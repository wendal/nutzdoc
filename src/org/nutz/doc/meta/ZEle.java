package org.nutz.doc.meta;

import org.nutz.lang.Strings;

public class ZEle {

	ZEle(String text) {
		this.text = text;
	}

	private ZRefer src;
	private int height;
	private int width;

	public ZRefer getSrc() {
		return src;
	}

	public ZEle setSrc(ZRefer src) {
		this.src = src.setEle(this);
		return this;
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

	private ZBlock block;

	public ZBlock getBlock() {
		return block;
	}

	public ZDoc getDoc() {
		if (null != block)
			return block.getDoc();
		return null;
	}

	public ZEle setBlock(ZBlock paragraph) {
		this.block = paragraph;
		return this;
	}

	private ZStyle style;

	public ZStyle getStyle() {
		return style;
	}

	public ZStyle style() {
		if (null == style)
			style = ZDocs.style();
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
		this.href = refer.setEle(this);
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

	public String toString() {
		if (isImage()) {
			return String.format("<%s>", src.value());
		} else if (hasHref()) {
			if (Strings.isBlank(text))
				return String.format("[%s]", href.value());
			return String.format("[%s %s]", href.value(), text);
		}
		return text;
	}

}

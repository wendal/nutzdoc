package org.nutz.doc.meta;

public class ZEle {

	public ZEle(String text) {
		style = new ZStyle();
		this.text = text;
	}

	private ZParagraph paragraph;

	public ZParagraph getParagraph() {
		return paragraph;
	}

	public ZEle setParagraph(ZParagraph paragraph) {
		this.paragraph = paragraph;
		return this;
	}

	private ZStyle style;

	public ZStyle style() {
		return style;
	}

	private ZRefer refer;

	public ZRefer getRefer() {
		return refer;
	}

	public ZEle setRefer(ZRefer refer) {
		this.refer = refer;
		return this;
	}

	public boolean hasRefer() {
		return null != refer;
	}

	private String text;

	public String getText() {
		return text;
	}

}

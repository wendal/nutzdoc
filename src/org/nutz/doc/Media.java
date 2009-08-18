package org.nutz.doc;

public class Media extends Inline {

	private Refer src;

	private int width;

	private int height;

	public Media width(int width) {
		this.width = width;
		return this;
	}

	public int width() {
		return width;
	}

	public Media height(int height) {
		this.height = height;
		return this;
	}

	public int height() {
		return height;
	}

	public Refer src() {
		return src;
	}

	public String getSrc() {
		return src.toString();
	}

	public void src(String src) {
		this.src = Doc.refer(this,src);
	}

	public void src(Refer src) {
		this.src = src;
	}

	@Override
	public String getText() {
		return src.toString();
	}

	@Override
	public boolean isBlank() {
		return false;
	}

}

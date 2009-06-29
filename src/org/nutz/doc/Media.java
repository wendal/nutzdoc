package org.nutz.doc;

import org.nutz.lang.Lang;

public class Media extends Line {

	private String src;

	private int width;

	private int height;

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public void addChild(Line block) {
		throw Lang.makeThrow("Media can not contains children");
	}

	@Override
	public void addChild(int index, Line block) {
		throw Lang.makeThrow("Media can not contains children");
	}

}

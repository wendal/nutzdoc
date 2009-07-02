package org.nutz.doc;

import org.nutz.doc.style.Style;
import org.nutz.lang.Strings;

public class Inline extends Ele implements Text {

	protected Inline() {
		super();
	}

	private String text;

	private Refer href;

	public boolean isAnchor() {
		return null != href;
	}

	public Refer getHref() {
		return href;
	}

	public void href(Refer href) {
		this.href = href;
	}

	public void href(String str) {
		this.href = Doc.refer(str);
	}

	private Line block;

	public Line getBlock() {
		return block;
	}

	void setBlock(Line block) {
		this.block = block;
	}

	public String getText() {
		return null == text ? "" : text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isBlank() {
		return Strings.isBlank(text);
	}

	public Style getRealStyle() {
		if (hasStyle())
			return getStyle().merge(getBlock().getRealStyle());
		return getBlock().getRealStyle();
	}

	@Override
	public String toString() {
		return getText();
	}

}

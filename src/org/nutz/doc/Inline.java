package org.nutz.doc;

import org.nutz.doc.style.Style;
import org.nutz.lang.Strings;

public class Inline extends Ele implements Text, DocBase {

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
		this.href = Doc.refer(this, str);
	}

	private Line line;

	public Line getLine() {
		return line;
	}

	void setLine(Line block) {
		this.line = block;
	}

	@Override
	public String getAbsolutePath() {
		return line.getDoc().getAbsolutePath();
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
			return getStyle().merge(getLine().getRealStyle());
		return getLine().getRealStyle();
	}

	@Override
	public String toString() {
		return getText();
	}

}

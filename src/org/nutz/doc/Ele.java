package org.nutz.doc;

import org.nutz.doc.style.Style;

public abstract class Ele {

	private Style style;

	public Style getStyle() {
		if (null == style)
			style = new Style();
		return style;
	}

	public void removeStyle() {
		style = null;
	}

	public boolean hasStyle() {
		return style != null;
	}
	
	public abstract Style getRealStyle();
	
}

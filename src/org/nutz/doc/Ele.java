package org.nutz.doc;

import org.nutz.doc.style.Style;

public abstract class Ele {

	private static int ID = 0;

	int id;

	protected Ele() {
		id = ++ID;
	}

	public int ID() {
		return id;
	}

	public String UID() {
		return "H" + id;
	}

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

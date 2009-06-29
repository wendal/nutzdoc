package org.nutz.doc;

import java.util.ArrayList;
import java.util.List;

public class Doc {

	static <T extends Ele> List<T> list(Class<T> type) {
		return new ArrayList<T>();
	}

	public static Inline inline(String text) {
		Inline i = new Inline();
		i.setText(text);
		return i;
	}

	public static Line line(List<Inline> eles) {
		Line b = new Line();
		for (Inline e : eles)
			b.append(e);
		return b;
	}

	public static Line line(Inline ele) {
		return new Line().append(ele);
	}

	public static Line line(String text) {
		return line(inline(text));
	}

	public Href href(String str) {
		return new Href(str);
	}
}
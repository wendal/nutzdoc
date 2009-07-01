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

	public static Including including(String s) {
		return including(s);
	}

	public static Including including(Refer refer,DocParser parser) {
		Including inc = new Including();
		inc.setRefer(refer);
		inc.setParser(parser);
		return inc;
	}

	public static Refer refer(String str) {
		return new Refer(str);
	}

	public static Media media(String src) {
		Media m = new Media();
		m.src(src);
		return m;
	}

	/*-----------------------------------------------------------------*/

	public Doc() {
		root = new Line();
	}

	private Line root;

	public Line root() {
		return root;
	}

}

package org.nutz.doc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Doc {

	public static <T> List<T> list(Class<T> type) {
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

	public static Including including(Refer refer, DocParser parser) {
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

	public static Code code(String text, Code.TYPE type) {
		Code code = new Code();
		code.setType(null == type ? Code.TYPE.Unknown : type);
		code.setText(text);
		return code;
	}

	public static IndexTable indexTable(int level) {
		IndexTable it = new IndexTable();
		it.setLevel(level);
		return it;
	}

	/*-----------------------------------------------------------------*/

	public Doc() {
		root = new Line();
		root.setDoc(this);
	}

	private Line root;

	public Line root() {
		return root;
	}

	public Line getIndex(int level) {
		return getIndex(root, level);
	}

	private static Line getIndex(Line line, int level) {
		if (line instanceof FinalLine)
			return null;
		Line root = Doc.line(line.getText());
		if (level > 0) {
			for (Iterator<Line> it = line.children(); it.hasNext();) {
				Line indxtab = getIndex(it.next(), level - 1);
				if (null != indxtab)
					root.addChild(indxtab);
			}
		}
		return root;
	}
}

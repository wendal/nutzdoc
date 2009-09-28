package org.nutz.doc.zdoc;

import java.io.BufferedReader;
import java.io.IOException;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

class ZDocScanning {

	Line scan(BufferedReader reader) {
		try {
			String l;
			Line root = Line.make(null, null);
			Line last = root;
			while (null != (l = reader.readLine())) {
				int depth = countTab(l);
				l = Strings.trim(l);
				if (last != root && last.isBlank()) {
					last = Line.make(last.getParent(), l);
				} else if (Strings.isEmpty(l)) {
					last = Line.make(last == root ? last : last.getParent(), "");
				} else {
					while (depth < last.depth())
						last = last.getParent();
					last = Line.make(last, l);
				}
			}
			return root;
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

	private int countTab(String l) {
		int i = 0;
		for (; i < l.length(); i++)
			if (l.charAt(i) != '\t')
				break;
		return i;
	}

}

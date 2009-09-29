package org.nutz.doc.zdoc;

import java.io.BufferedReader;
import java.io.IOException;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

class ZDocScanning {

	Line scan(BufferedReader reader) {
		try {
			String str;
			Line root = Line.make(null, null);
			Line last = root;
			while (null != (str = reader.readLine())) {
				int depth = countTab(str);
				str = Strings.trim(str);
				if (Strings.isEmpty(str)) {
					last = Line.make(last == root ? last : last.getParent(), "");
				} else {
					while (depth < last.depth())
						last = last.getParent();
					last = Line.make(last, str);
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

package org.nutz.doc.zdoc;

import java.io.BufferedReader;
import java.io.IOException;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

class ZDocScanning {

	Line scan(BufferedReader reader) {
		try {
			String str;
			Line root = Line.make(null);
			Line last = root;
			while (null != (str = reader.readLine())) {
				int depth = countTab(str);
				str = Strings.trim(str);
				Line line = Line.make(str);

				/*
				 * When HR or Blank
				 */
				if (line.isHr() || line.isBlank()) {
					if (last == root) {
						last.add(line);
					} else {
						last.getParent().add(line);
					}
				} else {
					while (depth < last.depth())
						last = last.getParent();
					if (null != last.getParent())
						if (last.isBlank() || last.isHr())
							last = last.getParent();
					last.add(line);
				}
				last = line;
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

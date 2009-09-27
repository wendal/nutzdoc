package org.nutz.doc.zdoc;

import java.io.BufferedReader;
import java.io.IOException;

class ZDocScanning {

	Line scan(BufferedReader reader) throws IOException {
		String l;
		Line root = Line.make(null, null);
		Line last = root;
		while (null != (l = reader.readLine())) {
			int depth = countTab(l);
			while (depth < last.depth())
				last = last.getParent();
			last = Line.make(last, l);
		}
		return root;
	}

	private int countTab(String l) {
		for (int i = 0; i < l.length(); i++)
			if (l.charAt(i) != '\t')
				return i;
		return 0;
	}

}

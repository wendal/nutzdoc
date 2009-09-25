package org.nutz.doc.zdoc;

import java.io.BufferedReader;
import java.io.IOException;


class Scanning {

	private BufferedReader reader;
	private Line root;

	Scanning(BufferedReader reader) {
		this.reader = reader;
		root = Line.make(null, null);
	}

	void scan() throws IOException {
		String l;
		Line last = root;
		while (null != (l = reader.readLine())) {
			int depth = countTab(l);
			while (depth < last.depth())
				last = last.getParent();
			last = Line.make(last, l);
		}
	}

	private int countTab(String l) {
		for (int i = 0; i < l.length(); i++)
			if (l.charAt(i) != '\t')
				return i;
		return 0;
	}

	Line get() {
		return root;
	}
}

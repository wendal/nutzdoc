package org.nutz.doc.zdoc;

import static org.nutz.doc.meta.ZDocs.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.nutz.doc.meta.Author;
import org.nutz.doc.meta.ZBlock;
import org.nutz.doc.meta.ZDoc;
import org.nutz.lang.util.IntRange;

class Parsing {

	private ZDoc doc;
	private BufferedReader reader;

	Parsing(BufferedReader reader) {
		this.doc = new ZDoc();
		this.reader = reader;
	}

	ZDoc getDoc() {
		return doc;
	}

	void parse() throws IOException {
		Scanning scanning = new Scanning(reader);
		scanning.scan();
		format(doc.root(), scanning.get());
	}

	private void format(ZBlock p, Line line) {
		Iterator<Line> it = line.it();
		List<ZBlock> blocks = new LinkedList<ZBlock>();
		ZBlock last = null;
		while (it.hasNext()) {
			line = it.next();
			if (setupDocProperties(p, line))
				continue;
			if (asIndexRange(p, line))
				continue;
			
		}

	}

	static boolean setupDocProperties(ZBlock p, Line line) {
		String s = line.getTitle();
		if (s != null) {
			p.getDoc().setTitle(s);
			return true;
		}
		Author a = line.getAuthor();
		if (null != a) {
			p.getDoc().addAuthor(a);
			return true;
		}
		a = line.getVerifier();
		if (null != a) {
			p.getDoc().addVerifier(a);
			return true;
		}
		return false;
	}

	static boolean asIndexRange(ZBlock p, Line line) {
		IntRange ir = line.getIndexRange();
		if (null != ir) {
			p.add(index(ir));
			return true;
		}
		return false;
	}

}

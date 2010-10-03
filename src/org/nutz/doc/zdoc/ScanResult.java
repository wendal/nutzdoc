package org.nutz.doc.zdoc;

import org.nutz.doc.meta.ZDoc;

public class ScanResult {

	private Line rootLine;

	private ZDoc doc;

	public ScanResult() {
		rootLine = new Line();
		doc = new ZDoc();
	}

	public Line root() {
		return rootLine;
	}

	public ZDoc doc() {
		return doc;
	}

}

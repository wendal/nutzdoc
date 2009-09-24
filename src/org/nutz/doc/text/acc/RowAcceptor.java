package org.nutz.doc.text.acc;

import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.text.BlockAcceptor;

public class RowAcceptor implements BlockAcceptor {

	public boolean accept(char c) {
		return false;
	}

	public void init(int depth, String str) {}

	public void update(ZDoc doc) {}

	public int depth() {
		return 0;
	}

}

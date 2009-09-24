package org.nutz.doc.text.acc;

import org.nutz.doc.meta.ZBlock;
import org.nutz.doc.meta.ZDocs;
import org.nutz.lang.util.IntRange;

public abstract class LineAcceptor extends DefaultAcceptor {

	private StringBuilder sb;

	@Override
	public boolean accept(char c) {
		if (c == '\n')
			return false;
		sb.append(c);
		return true;
	}

	public void init(int depth, String str) {
		this.depth = depth;
		this.sb = new StringBuilder();
	}

	protected String getText() {
		return sb.toString();
	}

	@Override
	protected ZBlock makeBlock() {
		IntRange range = IntRange.make(getText());
		return ZDocs.p().setIndexRange(range);
	}

}

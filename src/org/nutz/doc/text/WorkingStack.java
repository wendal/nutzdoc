package org.nutz.doc.text;

import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.text.acc.*;
import org.nutz.lang.util.LinkedCharArray;

class WorkingStack implements ParagraphAcceptor {

	static char[] CS = { '\n', ':', '{', '<', '`', '[' };

	boolean isJudgeTime(char c) {
		for (char ch : CS)
			if (ch == c)
				return true;
		return false;
	}

	public WorkingStack() {
		cache = new LinkedCharArray();
	}

	LinkedCharArray cache;
	private ParagraphAcceptor acceptor;

	public boolean accept(char c) {
		if (null != acceptor)
			return acceptor.accept(c);
		cache.push(c);
		// Decide which ParagraphAcceptor should be created
		if (isJudgeTime(c)) {
			String s = cache.toString();
			acceptor = new DefaultAcceptor();
		}
		return true;
	}

	@Override
	public void update(ZDoc doc) {
		acceptor.update(doc);
	}

	@Override
	public void init(char[] cs) {
		cache.clear();
		acceptor = null;
	}

}

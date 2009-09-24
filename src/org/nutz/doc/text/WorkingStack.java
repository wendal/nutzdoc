package org.nutz.doc.text;

import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.meta.ZDocs;

import org.nutz.lang.util.LinkedCharArray;

class WorkingStack {

	public WorkingStack(ZDoc doc) {
		this.doc = doc;
		cache = new LinkedCharArray();
		acceptor = null;
		depth = 0;
	}

	private ZDoc doc;
	LinkedCharArray cache;
	private BlockAcceptor acceptor;
	private int depth;
	private int nextDepth;

	public void accept(char c) {
		if (null != acceptor) {
			if (c == '\n') {
				cache.push(c);
				return;
			} else if (c == '\t' && !cache.isEmpty()) {
				nextDepth++;
				return;
			}
			
			if (cache.isEmpty()) {
				if (!acceptor.accept(c)) {
					acceptor.update(doc);
					acceptor = null;
				}
			}
			return;
		}

		if (cache.isEmpty()) {
			if (c == '\t')
				depth++;
			return;
		}

		cache.push(c);
		// Decide which ParagraphAcceptor should be created
		if (Acceptors.isJudgeTime(cache, c)) {
			String s = cache.popAll();
			acceptor = Acceptors.evalBlockAcceptor(s);
			acceptor.init(depth, s);
			cache.clear();
		}
	}

	public void close() {
		if (null != acceptor)
			acceptor.update(doc);
		if (!cache.isEmpty())
			doc.last().add(ZDocs.p(cache.toString()));
	}
}

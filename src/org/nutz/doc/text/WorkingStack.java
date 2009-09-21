package org.nutz.doc.text;

import org.nutz.doc.ZDocs;
import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.meta.ZEle;
import org.nutz.doc.meta.ZParagraph;
import org.nutz.lang.util.LinkedCharArray;

public class WorkingStack extends CharAcceptor {

	WorkingStack() {
		doc = new ZDoc();
		cache = new LinkedCharArray(256);
	}

	private ZDoc doc;
	private LinkedCharArray cache;
	private CharAcceptor acceptor;

	boolean accept(char c) {
		// When end of stream, close whole stack
		if (-1 == c) {
			finish();
			return false;
		}
		// If acceptor is avaliable, use it.
		// util the accept is close, then update zdoc
		if (null != acceptor) {
			if (acceptor.accept(c))
				return true;
			updateDocAndRemoveAcceptor();
			return true;
		}
		// push char to cache and try eval out one acceptor
		else {
			cache.push(c);
			evalAcceptor();
		}
		return true;
	}

	private void finish() {
		// If some string in cache, append as last child of doc root
		if (cache.size() > 0) {
			doc.root().add(ZDocs.p(cache.toString()));
			cache.clear();
		}
		// then update the acceptor
		else {
			updateDocAndRemoveAcceptor();
		}
	}

	private void updateDocAndRemoveAcceptor() {
		Object re = acceptor.getResult();
		if (re instanceof ZEle) {
			doc.last().append((ZEle) re);
		} else if (re instanceof P) {
			P p = (P) re;
			ZParagraph last = doc.last();
			while (last.level() > p.level)
				last = last.getParent();
			if (!last.isCanBeParent())
				last = last.getParent();
			last.add(p.p);
		}
		acceptor = null;
	}

	private void evalAcceptor() {
		
	}

	Object getResult() {
		return doc;
	}

	void init(String s) {}
}

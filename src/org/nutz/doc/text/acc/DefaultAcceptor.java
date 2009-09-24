package org.nutz.doc.text.acc;

import java.util.LinkedList;
import java.util.List;

import org.nutz.doc.ZDocs;
import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.meta.ZEle;
import org.nutz.doc.meta.ZParagraph;
import org.nutz.doc.text.ParagraphAcceptor;

public class DefaultAcceptor implements ParagraphAcceptor {

	private List<ZEle> eles;
	private int depth;

	@Override
	public void init(char[] cs) {
		eles = new LinkedList<ZEle>();
		depth = 0;
		for (char c : cs)
			if (c == '\t')
				depth++;
			else
				break;
	}

	@Override
	public boolean accept(char c) {
		return false;
	}

	@Override
	public void update(ZDoc doc) {
		ZParagraph p = ZDocs.p();
		for (ZEle e : eles)
			p.append(e);

		// find the parent
		ZParagraph last = doc.last();
		while (depth < last.depth()) {
			if (last.isRoot())
				break;
			last = last.getParent();
		}
		// code, only OL,UL,Normal can append child
		while (!last.isCanBeParent())
			last = last.getParent();
		last.add(p);
	}

}

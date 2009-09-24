package org.nutz.doc.text.acc;

import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.meta.ZDocs;
import org.nutz.doc.meta.ZEle;
import org.nutz.doc.meta.ZBlock;
import org.nutz.doc.text.Acceptors;
import org.nutz.doc.text.BlockAcceptor;
import org.nutz.doc.text.EleAcceptor;

public class DefaultAcceptor implements BlockAcceptor {

	private EleCache eles;
	protected int depth;
	private EleAcceptor ea;

	public void init(int depth, String str) {
		eles = new EleCache();
		this.depth = depth;
	}

	public int depth() {
		return depth;
	}

	public boolean accept(char c) {
		if (c == '\n' && ea != null) {
			popAcceptor();
		} else if (null == ea) {
			ea = Acceptors.evalEleAcceptor(c);
		}
		if (!ea.accept(c)) {
			popAcceptor();
		}
		return !eles.isEndBy2Br();
	}

	private void popAcceptor() {
		ea.update(eles);
		ea = null;
	}

	public void update(ZDoc doc) {
		if (null != ea)
			popAcceptor();

		ZBlock p = makeBlock();

		// find the parent
		ZBlock last = doc.last();
		while (depth < last.depth()) {
			if (last.isRoot())
				break;
			last = last.getParent();
		}
		// code, only OL,UL,Normal can append child
		while (!last.isNormal())
			last = last.getParent();
		last.add(p);
	}

	protected ZBlock makeBlock() {
		ZBlock p = ZDocs.p();
		if (eles.size() >= 0) {
			for (ZEle e : eles.eles())
				p.append(e);
		}
		return p;
	}

}

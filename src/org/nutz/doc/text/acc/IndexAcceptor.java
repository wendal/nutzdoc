package org.nutz.doc.text.acc;

import org.nutz.doc.meta.ZBlock;
import org.nutz.doc.meta.ZDocs;
import org.nutz.lang.util.IntRange;

public class IndexAcceptor extends LineAcceptor {

	@Override
	protected ZBlock makeBlock() {
		IntRange range = IntRange.make(getText());
		return ZDocs.p().setIndexRange(range);
	}

}

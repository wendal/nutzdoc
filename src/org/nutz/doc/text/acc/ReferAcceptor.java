package org.nutz.doc.text.acc;

import org.nutz.doc.meta.ZParagraph;
import org.nutz.doc.text.EleAcceptor;

public class ReferAcceptor implements EleAcceptor {

	@Override
	public boolean accept(char c) {
		return false;
	}

	@Override
	public void update(ZParagraph p) {}

}

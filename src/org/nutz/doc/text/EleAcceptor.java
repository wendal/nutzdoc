package org.nutz.doc.text;

import org.nutz.doc.meta.ZParagraph;

public interface EleAcceptor {

	boolean accept(char c);

	void update(ZParagraph p);
	
}

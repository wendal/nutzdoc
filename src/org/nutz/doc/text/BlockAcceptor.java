package org.nutz.doc.text;

import org.nutz.doc.meta.ZDoc;

public interface BlockAcceptor {

	void init(int depth, String str);
	
	int depth();

	boolean accept(char c);

	void update(ZDoc doc);

}

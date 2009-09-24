package org.nutz.doc.text;

import org.nutz.doc.meta.ZDoc;

public interface ParagraphAcceptor {

	void init(char[] cs);

	boolean accept(char c);

	void update(ZDoc doc);

}

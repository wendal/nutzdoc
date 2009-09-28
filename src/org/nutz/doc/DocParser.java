package org.nutz.doc;

import org.nutz.doc.meta.ZDoc;

public interface DocParser {
	
	ZDoc parse(CharSequence cs);
	
}

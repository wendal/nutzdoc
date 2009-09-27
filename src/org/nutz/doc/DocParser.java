package org.nutz.doc;

import java.io.IOException;

import org.nutz.doc.meta.ZDoc;

public interface DocParser {
	
	ZDoc parse(CharSequence cs) throws IOException;
	
}

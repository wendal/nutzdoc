package org.nutz.doc;

import java.io.Reader;

import org.nutz.doc.meta.ZDoc;

public interface DocParser {
	
	ZDoc parse(Reader reader);
	
}

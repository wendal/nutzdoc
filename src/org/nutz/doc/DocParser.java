package org.nutz.doc;

import java.io.Reader;

public interface DocParser {
	
	Document parse(Reader reader);
	
}

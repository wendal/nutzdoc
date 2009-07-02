package org.nutz.doc;

import java.io.InputStream;

public interface DocParser {
	
	Doc parse(InputStream ins);
	
}

package org.nutz.doc;

import java.io.File;
import java.io.IOException;

import org.nutz.doc.meta.ZDoc;

public interface DocParser {
	
	ZDoc parse(File src) throws IOException;
	
}

package org.nutz.doc;

import java.io.File;
import java.io.IOException;

import org.nutz.doc.meta.ZFolder;
import org.nutz.lang.util.Node;

public interface FolderParser {

	Node<ZFolder> parse(File dir) throws IOException;
	
}

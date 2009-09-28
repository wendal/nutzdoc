package org.nutz.doc.zdoc;

import java.io.File;
import java.io.IOException;

import org.nutz.doc.FolderParser;
import org.nutz.doc.meta.ZFolder;
import org.nutz.lang.util.Node;

public class ZDocFolderParser implements FolderParser {

	public Node<ZFolder> parse(File dir) throws IOException {
		return new FolderParsing(dir).parse();
	}

}

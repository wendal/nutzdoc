package org.nutz.doc;

import java.io.File;
import java.io.IOException;

import org.nutz.doc.meta.ZFolder;

public interface FolderParser {

	ZFolder parse(File dir) throws IOException;
	
}

package org.nutz.doc;

import java.io.File;
import java.io.IOException;

import org.nutz.doc.meta.ZFolder;

public interface FolderRender {

	void render(File dest, ZFolder folder) throws IOException;
	
}

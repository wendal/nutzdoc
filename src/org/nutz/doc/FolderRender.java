package org.nutz.doc;

import java.io.File;
import java.io.IOException;

import org.nutz.doc.meta.ZDocSet;

public interface FolderRender {

	void render(File dest, ZDocSet set) throws IOException;

}

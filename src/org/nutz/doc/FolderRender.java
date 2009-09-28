package org.nutz.doc;

import java.io.File;
import java.io.IOException;

import org.nutz.doc.meta.ZFolder;
import org.nutz.lang.util.Node;

public interface FolderRender {

	void render(File dest, Node<ZFolder> root) throws IOException;

}

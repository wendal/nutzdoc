package org.nutz.doc;

import java.io.File;

public interface FileVisitor {

	void visit(File file, String title, int depth);

}

package org.nutz.doc;

import java.io.File;

public interface DocFileVisitor {

	void visit(File file, String title, int depth);

}

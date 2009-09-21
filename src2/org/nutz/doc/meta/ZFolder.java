package org.nutz.doc.meta;

import java.util.LinkedList;
import java.util.List;

public class ZFolder {

	private List<ZDoc> docs;
	private List<ZFolder> children;

	public ZFolder() {
		docs = new LinkedList<ZDoc>();
		children = new LinkedList<ZFolder>();
	}

	public ZFolder append(ZDoc doc) {
		docs.add(doc);
		return this;
	}

	public ZFolder addChildren(ZFolder folder) {
		children.add(folder);
		return this;
	}

	public ZFolder[] children() {
		return children.toArray(new ZFolder[children.size()]);
	}

	public ZDoc[] docs() {
		return docs.toArray(new ZDoc[docs.size()]);
	}

}

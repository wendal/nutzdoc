package org.nutz.doc.meta;

import java.util.LinkedList;
import java.util.List;

public class ZFolder {

	public static ZFolder create() {
		return new ZFolder();
	}

	private ZDoc folderDoc;
	private List<ZDoc> docs;
	private List<ZFolder> children;

	private ZFolder() {
		docs = new LinkedList<ZDoc>();
		children = new LinkedList<ZFolder>();
	}

	public ZDoc getFolderDoc() {
		return folderDoc;
	}

	public void setFolderDoc(ZDoc folderDoc) {
		this.folderDoc = folderDoc;
	}

	public ZFolder append(ZDoc doc) {
		docs.add(doc);
		return this;
	}

	public ZFolder add(ZFolder folder) {
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

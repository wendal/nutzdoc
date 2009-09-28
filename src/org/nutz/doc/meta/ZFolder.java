package org.nutz.doc.meta;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ZFolder {

	public static ZFolder create() {
		return new ZFolder();
	}

	private ZFolder() {
		folderDoc = new ZDoc();
		docs = new LinkedList<ZDoc>();
	}

	private Author author;
	private ZDoc folderDoc;
	private File dir;
	private List<ZDoc> docs;

	public String getTitle() {
		return folderDoc.getTitle();
	}

	public ZFolder setTitle(String title) {
		folderDoc.setTitle(title);
		return this;
	}

	public File getDir() {
		return dir;
	}

	public ZFolder setDir(File dir) {
		this.dir = dir;
		return this;
	}

	public File getSource() {
		return folderDoc.getSource();
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public boolean hasFolderDoc() {
		return null != folderDoc;
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

	public ZDoc[] docs() {
		return docs.toArray(new ZDoc[docs.size()]);
	}

}

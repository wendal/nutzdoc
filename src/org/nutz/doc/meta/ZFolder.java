package org.nutz.doc.meta;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.nutz.lang.util.Disks;
import org.nutz.lang.util.Node;
import org.nutz.lang.util.Nodes;

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

	public int countDocs() {
		return docs.size();
	}

	public boolean hasDoc() {
		return !docs.isEmpty();
	}

	public String toString() {
		return String.format("[%s] %d docs", getTitle(), docs.size());
	}

	public static Node<ZIndex> toIndex(Node<ZFolder> fnode) {
		// Render Self
		String text = fnode.get().getTitle();
		String href = null;
		File folderDocFile = fnode.get().getFolderDoc().getSource();
		if (null != folderDocFile && folderDocFile.isFile()) {
			href = Disks.getRelativePath(fnode.get().getDir(), folderDocFile);
		}
		Node<ZIndex> re = Nodes.create(ZDocs.index(href, null, text));
		// Render Docs
		for (ZDoc doc : fnode.get().docs) {
			text = doc.getTitle();
			href = Disks.getRelativePath(fnode.top().get().getDir(), doc.getSource());
			re.add(Nodes.create(ZDocs.index(href, null, text)));
		}
		// Render sub-folders
		for (Node<ZFolder> sub : fnode.getChildren()) {
			re.add(toIndex(sub));
		}
		return re;
	}

}

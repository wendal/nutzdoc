package org.nutz.doc.meta;

import java.io.File;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.LinkedIntArray;
import org.nutz.lang.util.Node;
import org.nutz.lang.util.Nodes;

public class ZDocSet {

	private Node<ZItem> root;

	private Node<ZItem> cursor;

	private String src;

	public ZDocSet(String name) {
		root = Nodes.create((ZItem) new ZFolder(name));
		cursor = root;
	}

	public Node<ZItem> add(Node<ZItem> node) {
		cursor.add(node);
		cursor = node;
		return cursor;
	}

	public Node<ZItem> cursor() {
		return cursor;
	}

	public Node<ZItem> root() {
		return root;
	}

	public boolean isRoot() {
		return root == cursor;
	}

	public boolean goParent() {
		if (isRoot())
			return false;
		cursor = cursor.parent();
		return true;
	}

	public String getSrc() {
		return src;
	}

	public ZDocSet setSrc(String src) {
		this.src = src;
		return this;
	}

	public File checkSrcDir() {
		File dir = Files.findFile(src);
		if (null == dir)
			throw Lang.makeThrow("Fail to find '%s'", src);
		if (!dir.isDirectory())
			throw Lang.makeThrow("'%s' should be directory", src);
		return dir;
	}

	public Node<ZIndex> createIndexTable() {
		LinkedIntArray numbers = new LinkedIntArray(10);
		return _createIndexTable(numbers, checkSrcDir(), root);
	}

	private static Node<ZIndex> _createIndexTable(	LinkedIntArray numbers,
													File rootDir,
													Node<ZItem> root) {
		// Render Self
		ZItem zi = root.get();
		String text = zi.getTitle();
		String href = null;
		if (zi instanceof ZDoc)
			href = Disks.getRelativePath(rootDir.getAbsolutePath(), ((ZDoc) zi).getSource());

		Node<ZIndex> re = Nodes.create(ZDocs.index(href, numbers.toArray(), text));

		// Render Children Nodes
		numbers.push(0);
		for (Node<ZItem> child : root.getChildren()) {
			re.add(_createIndexTable(numbers, rootDir, child));
			numbers.set(numbers.size() - 1, numbers.last() + 1);
		}
		numbers.popLast();
		return re;
	}
}

package org.nutz.doc.meta;

import org.nutz.lang.util.Node;
import org.nutz.lang.util.Nodes;

public class ZDocSet {

	private Node<ZItem> root;

	private Node<ZItem> cursor;

	private String src;

	public ZDocSet() {
		root = Nodes.create(new ZItem());
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

	public void setSrc(String src) {
		this.src = src;
	}

}

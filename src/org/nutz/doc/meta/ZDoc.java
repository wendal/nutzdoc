package org.nutz.doc.meta;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.util.Disks;

public class ZDoc extends ZItem {

	public ZDoc() {
		super();
		root = new ZBlock().setDoc(this);
		attrs = new HashMap<String, Object>();
		ids = new HashMap<Object, Integer>();
	}

	private int _ID_;
	private File source;
	private ZBlock root;
	private Map<String, Object> attrs;
	private Map<Object, Integer> ids;

	int getId(Object key) {
		Integer id = ids.get(key);
		if (null == id) {
			id = _ID_++;
			ids.put(key, id);
		}
		return id;
	}

	public Object getAttr(String name) {
		return attrs.get(name);
	}

	public boolean hasAttr(String name) {
		return attrs.containsKey(name);
	}

	public ZDoc setAttr(String name, Object value) {
		attrs.put(name, value);
		return this;
	}

	public String getTitle() {
		return root.getText();
	}

	public ZDoc setTitle(String title) {
		root.setText(title);
		return this;
	}

	public File getSource() {
		return source;
	}

	public ZDoc setSource(File source) {
		this.source = source;
		return this;
	}

	public ZBlock root() {
		return root;
	}

	public String getRelativePath(File file) {
		return Disks.getRelativePath(this.getSource(), file);
	}

}

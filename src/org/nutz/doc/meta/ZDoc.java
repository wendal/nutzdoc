package org.nutz.doc.meta;

import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Disks;

public class ZDoc extends ZItem {

	public ZDoc() {
		super();
		root = new ZBlock().setDoc(this);
		attrs = new HashMap<String, Object>();
		ids = new HashMap<Object, Integer>();
	}

	private int _ID_;
	private String source;
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

	@Override
	public String getName() {
		return Files.getName(source);
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
		String title = root.getText();
		return Strings.isBlank(title) ? null == source ? title : Files.getMajorName(source) : title;
	}

	public ZDoc setTitle(String title) {
		root.setText(title);
		return this;
	}

	public String getSource() {
		return source;
	}

	public ZDoc setSource(String source) {
		this.source = source;
		return this;
	}

	public ZBlock root() {
		return root;
	}

	public String getRelativePath(String filePath) {
		return Disks.getRelativePath(source, filePath);
	}

}

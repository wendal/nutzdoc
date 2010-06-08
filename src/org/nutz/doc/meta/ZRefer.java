package org.nutz.doc.meta;

import java.io.File;

import org.nutz.lang.Files;

public class ZRefer {

	private enum TYPE {
		HTTP, HTTPS, BOOKMARK, INNER, FILE, RELATIVE
	}

	private String value;
	private String path;
	private ZEle ele;
	private TYPE type;

	ZRefer(String path) {
		if (null != path) {
			this.path = path.replace('\\', '/');
			path = path.toLowerCase();
			if (path.startsWith("http://")) {
				type = TYPE.HTTP;
				value = this.path.substring(7);
			} else if (path.startsWith("https://")) {
				type = TYPE.HTTPS;
				value = this.path.substring(8);
			} else if (path.startsWith("file:///")) {
				type = TYPE.FILE;
				value = this.path.substring(8);
			} else if (path.length() > 0) {
				char c = path.charAt(0);
				if (c == '$') {
					type = TYPE.BOOKMARK;
					value = this.path.substring(1);
				} else if (c == '#') {
					type = TYPE.INNER;
					value = this.path.substring(1);
				} else {
					type = TYPE.RELATIVE;
					value = this.path;
				}
			}
		}
	}

	public ZEle getEle() {
		return ele;
	}

	public ZRefer setEle(ZEle ele) {
		this.ele = ele;
		return this;
	}

	public boolean isInner() {
		return type == TYPE.INNER;
	}

	public boolean isBookmark() {
		return type == TYPE.BOOKMARK;
	}

	public boolean isFile() {
		return type == TYPE.FILE;
	}

	public boolean isHttp() {
		return type == TYPE.HTTP;
	}

	public boolean isHttps() {
		return type == TYPE.HTTP;
	}

	public boolean isWWW() {
		return type == TYPE.HTTP || type == TYPE.HTTPS;
	}

	public boolean isRelative() {
		return type == TYPE.RELATIVE;
	}

	public boolean isAvailable() {
		return null != type;
	}

	public boolean isLocal() {
		return null != getFile();
	}

	public ZDoc getDoc() {
		if (null != ele)
			return ele.getDoc();
		return null;
	}

	public File getFile() {
		if (isFile()) {
			return Files.findFile(value);
		} else if (isRelative() && null != getDoc()) {
			String p = Files.findFile(getDoc().getSource()).getParent();
			return Files.findFile(p + "/" + value);
		}
		return null;
	}

	public String getValue() {
		return value;
	}

	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return path;
	}

}

package org.nutz.doc.meta;

import java.io.File;

import org.nutz.lang.Files;

public class ZRefer {

	private enum TYPE {
		HTTP, HTTPS, BOOKMARK, FILE, RELATIVE
	}

	/**
	 * 有效值
	 */
	private String value;
	/**
	 * 原始值
	 */
	private String path;
	private ZEle ele;
	private TYPE type;
	/**
	 * 业内链接名称 - 出现 #xxx
	 */
	private String inner;

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
				if (path.charAt(0) == '$') {
					type = TYPE.BOOKMARK;
					value = this.path.substring(1);
				} else {
					type = TYPE.RELATIVE;
					value = this.path;
				}
			}
			// 解析页内链接
			if (null != value && value.length() > 0) {
				int pos = value.indexOf('#');
				if (pos != -1) {
					try {
						inner = value.substring(pos + 1);
						value = value.substring(0, pos);
					}
					catch (Exception e) {}
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

	public boolean hasInner() {
		return null != inner;
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

	public String getInner() {
		return inner;
	}

	@Override
	public String toString() {
		return path;
	}

}

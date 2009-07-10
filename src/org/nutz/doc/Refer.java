package org.nutz.doc;

import java.io.File;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class Refer {

	private String path;

	private String base;

	Refer(String path) {
		if (Strings.isBlank(path))
			throw Lang.makeThrow("Path can not be null!!!");
		this.path = path.replace('\\', '/');
	}

	public String getBase() {
		return base;
	}

	void setBase(String base) {
		this.base = base;
	}

	public boolean isInner() {
		return path.startsWith("#");
	}

	public boolean isHttp() {
		return path.startsWith("http://") || path.startsWith("https://");
	}

	public boolean isRelative() {
		return path.matches("^([\\w.]+[/])*([\\w.]+)$");
	}

	public boolean isLocal() {
		return null != getFile();
	}

	public File getFile() {
		File f = Files.findFile(path);
		if (null == f && !Strings.isBlank(base))
			return Files.findFile(base + "/" + path);
		return f;
	}

	@Override
	public String toString() {
		return path;
	}

}

package org.nutz.doc;

import java.io.File;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class Refer {

	private String path;

	private DocBase base;

	Refer(DocBase base, String path) {
		this.base = base;
		if (Strings.isBlank(path))
			throw Lang.makeThrow("Path can not be null!!!");
		this.path = path.replace('\\', '/');
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

	public String getBasePath() {
		return base.getAbsolutePath();
	}

	public File getFile() {
		if (isHttp() || isInner())
			return null;
		if (!isRelative())
			return new File(path);
		File f = Files.findFile(path);
		if (null != f)
			return f;
		File bf = new File(base.getAbsolutePath());
		String fp = bf.isFile() ? bf.getParent() + "/" + path : bf.getAbsolutePath() + "/" + path;
		return Files.findFile(fp);
	}

	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return path;
	}

}

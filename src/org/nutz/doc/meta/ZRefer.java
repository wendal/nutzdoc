package org.nutz.doc.meta;

import java.io.File;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class ZRefer {

	private String path;

	private ZEle ele;

	ZRefer(ZEle ele) {
		this.ele = ele;
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
		return ele.getParagraph().getDoc().getSource().getAbsolutePath();
	}

	public File getFile() {
		if (isHttp() || isInner())
			return null;
		if (!isRelative())
			return new File(path);
		File f = Files.findFile(path);
		if (null != f)
			return f;
		File bf = new File(getBasePath());
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

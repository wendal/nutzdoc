package org.nutz.doc.meta;

import java.io.File;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class ZRefer {

	private String path;

	ZRefer(String path) {
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

	public boolean isLocal(ZEle ele) {
		return null != getFile(ele);
	}

	public String getBasePath(ZEle ele) {
		return ele.getParagraph().getDoc().getSource().getAbsolutePath();
	}

	public File getFile(ZEle ele) {
		if (isHttp() || isInner())
			return null;
		if (!isRelative())
			return new File(path);
		File f = Files.findFile(path);
		if (null != f)
			return f;
		File bf = new File(getBasePath(ele));
		String fp = bf.isFile() ? bf.getParent() + "/" + path : bf.getAbsolutePath() + "/" + path;
		return Files.findFile(fp);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return path;
	}

}

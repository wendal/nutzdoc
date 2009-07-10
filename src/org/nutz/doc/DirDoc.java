package org.nutz.doc;

import java.io.File;
import java.util.List;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.w3c.dom.Element;

public class DirDoc {

	private File docFile;
	private String title;
	private String author;

	private DirDoc parent;
	private List<DirDoc> children;

	public DirDoc(File home, Element ele) {
		String path = attr(ele, "path");
		String abpath = home.getAbsolutePath() + (null == path ? "" : "/" + path);
		docFile = Files.findFile(abpath);
		if (null == docFile)
			throw Lang.makeThrow("Fail to find '%s' in '%s'", path, home);
		docFile = docFile.getAbsoluteFile();
		this.title = attr(ele, "title");
		this.author = attr(ele, "author");
		children = Doc.LIST(DirDoc.class);
	}

	static String attr(Element ele, String name) {
		String s = ele.getAttribute(name);
		return Strings.isBlank(s) ? null : s;
	}

	public String getTitle() {
		if (!Strings.isBlank(title))
			return title;
		return docFile.getName();
	}

	public String getAuthor() {
		if (!Strings.isBlank(author))
			return author;
		if (null != parent)
			return parent.getAuthor();
		return null;
	}

	public DirDoc[] children() {
		return children.toArray(new DirDoc[children.size()]);
	}

	public File getDocFile() {
		return docFile;
	}

	public DirDoc parent() {
		return parent;
	}

	public void addChild(DirDoc dd) {
		dd.parent = this;
		children.add(dd);
	}

}

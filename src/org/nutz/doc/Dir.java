package org.nutz.doc;

import java.io.File;
import java.util.List;

public class Dir {

	private File file;

	private List<Doc> docs;

	private List<Dir> dirs;

	public Dir(File file) {
		this.file = file;
		docs = Doc.LIST(Doc.class);
		dirs = Doc.LIST(Dir.class);
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public List<Doc> docs() {
		return docs;
	}

	public List<Dir> dirs() {
		return dirs;
	}

}

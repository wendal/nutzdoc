package org.nutz.doc;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

public class DirSet {

	private DocParser parser;
	private File home;

	public DirSet(File home, DocParser parser) {
		this.parser = parser;
		this.home = home;
	}

	public void load(final String regex) {
		load(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.matches(regex);
			}
		});
	}

	public void load(FilenameFilter filter) {
		root = new Dir(home);
		load2(root, filter);
	}

	private Dir root;

	private void load2(Dir dir, FilenameFilter filter) {
		File[] fs = filter == null ? dir.getFile().listFiles() : dir.getFile().listFiles(filter);
		try {
			for (File f : fs) {
				if (f.isDirectory()) {
					Dir sub = new Dir(f);
					load2(sub, filter);
				} else {
					InputStream ins = Streams.fileIn(f);
					Doc doc = parser.parse(ins);
					ins.close();
					doc.setFile(f);
					dir.docs().add(doc);
				}
			}
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

}

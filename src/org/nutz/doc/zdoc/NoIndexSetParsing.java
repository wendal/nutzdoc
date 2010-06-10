package org.nutz.doc.zdoc;

import java.io.File;
import java.io.FilenameFilter;

import org.nutz.doc.meta.ZDocSet;
import org.nutz.doc.meta.ZFolder;
import org.nutz.doc.meta.ZItem;
import org.nutz.lang.Files;
import org.nutz.lang.util.Node;
import org.nutz.lang.util.Nodes;

public class NoIndexSetParsing {

	private File root;

	private String regex;

	private ZDocParser docParser;

	public NoIndexSetParsing(File root, String regex) {
		this.root = root;
		this.docParser = new ZDocParser();
		this.regex = regex;
	}

	public void doParse(ZDocSet set) throws Exception {
		set.root().get().setTitle(root.getName());
		parseChildren(root, set.root());
	}

	public void parseChildren(File file, Node<ZItem> parentNode) {
		if (file.isFile())
			return;

		File[] files = file.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (dir.isHidden())
					return false;
				return dir.isDirectory() || name.matches(regex);
			}
		});
		for (File f : files) {
			ZItem zi = parse(f);
			if (null != zi) {
				Node<ZItem> node = Nodes.create(zi);
				parseChildren(f, node);
				parentNode.add(node);
			}
		}
	}

	public ZItem parse(File f) {
		ZItem re = null;
		// 目录
		if (f.isDirectory()) {
			re = new ZFolder(f.getName());
			re.setTitle(f.getName());
		}
		// 文件
		else if (f.isFile() && f.getName().toLowerCase().matches(regex)) {
			re = docParser.parse(Files.read(f)).setSource(f.getAbsolutePath());
		}
		return re;
	}

}

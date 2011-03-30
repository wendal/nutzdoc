package org.nutz.doc.zdoc;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Calendar;

import org.nutz.doc.meta.ZDocSet;
import org.nutz.doc.meta.ZFolder;
import org.nutz.doc.meta.ZItem;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.util.Node;
import org.nutz.lang.util.Nodes;

public class NoIndexSetParsing {

	private File root;

	private String regex;

	private ZDocParser docParser;

	public NoIndexSetParsing(File root, String regex) {
		this.root = root;
		this.docParser = new ZDocParser(Lang.context().set("now", Calendar.getInstance()));
		this.regex = regex;
	}

	public void doParse(ZDocSet set) throws Exception {
		set.root().get().setTitle(root.getName());
		parseChildren(root, set.root());
	}

	public boolean parseChildren(File file, Node<ZItem> parentNode) {
		if (file.isFile())
			return false;

		File[] files = file.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (dir.isHidden())
					return false;
				if (name.startsWith("."))
					return false;
				return dir.isDirectory() || name.matches(regex);
			}
		});
		for (File f : files) {
			ZItem zi = parse(f);
			if (null != zi) {
				Node<ZItem> node = Nodes.create(zi);
				// 目录 - 如果是空目录，则忽略
				if (zi instanceof ZFolder) {
					if (parseChildren(f, node))
						parentNode.add(node);
				}
				// zDoc 文档
				else {
					parentNode.add(node);
				}
			}
		}
		return parentNode.countChildren() > 0;
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
			re = docParser.parse(Streams.fileInr(f)).setSource(f.getAbsolutePath());
		}
		return re;
	}

}

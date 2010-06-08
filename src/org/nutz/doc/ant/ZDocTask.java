package org.nutz.doc.ant;

import java.io.File;
import java.io.IOException;

import org.nutz.doc.FolderParser;
import org.nutz.doc.FolderRender;
import org.nutz.doc.RenderLogger;
import org.nutz.doc.googlewiki.GoogleWikiFolderRender;
import org.nutz.doc.html.HtmlFolderRender;
import org.nutz.doc.meta.ZDocSet;
import org.nutz.doc.zdoc.ZDocFolderParser;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;

public class ZDocTask {

	private File src;

	private File dest;

	private String suffix;

	private String indexName;

	private String imgAddress;

	private String indexXml;

	public void execute() throws IOException {
		if (src == null || dest == null)
			throw Lang.makeThrow("src or dest can't be null");
		if (!src.exists())
			throw Lang.makeThrow("src isn't exists!");
		if (indexXml == null)
			indexXml = "index.xml";
		if (!dest.exists())
			Files.makeDir(dest);
		if (suffix.equals("html") || suffix.equals("htm")) {
			FolderParser parser = new ZDocFolderParser(indexXml);
			FolderRender render = new HtmlFolderRender(suffix, new RenderLogger());
			ZDocSet folder = parser.parse(src.getAbsolutePath());
			render.render(dest, folder);
		} else if (suffix.equals("wiki")) {
			FolderParser parser = new ZDocFolderParser(indexXml);
			FolderRender render = new GoogleWikiFolderRender(	indexName,
																imgAddress,
																new RenderLogger());
			ZDocSet folder = parser.parse(src.getAbsolutePath());
			render.render(dest, folder);
		}
	}

}

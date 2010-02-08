package org.nutz.doc;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.nutz.doc.googlewiki.GoogleWikiFolderRender;
import org.nutz.doc.html.HtmlFolderRender;
import org.nutz.doc.meta.ZFolder;
import org.nutz.doc.zdoc.ZDocFolderParser;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Node;

import static java.lang.System.*;

public class Doc {

	public static void main(String[] args) throws IOException {
		Doc doc = new Doc();
		if (args.length > 2) {
			File src = Files.findFile(args[0]);
			if (null == src) {
				out.printf("src directory: %s didn't existed!\n", args[0]);
			}
			File dest = new File(args[1]);
			if (dest.exists())
				Files.makeDir(dest);

			// zdoc /zdoc /html .html index.xml
			if (args.length == 3) {
				String suffix = args[2];
				if (suffix.toLowerCase().matches("^[.]htm[l]?$")) {
					doc.toHtmlFolder(src, dest, suffix, "index.xml");
					return;
				}
			}
			// zdoc /zdoc /wiki IndexTable http://dtri.com/img
			// or
			// zdoc /zdoc /html .html index.xml
			else if (args.length == 4) {
				String indexName = args[2];
				String imgAddress = args[3];
				// To HTML
				if (imgAddress.endsWith(".xml")) {
					doc.toHtmlFolder(src, dest, indexName, imgAddress);
				}
				// To WIKI
				else {
					doc.toGoogleWikiFolder(src, dest, indexName, imgAddress, "index.xml");
				}
				return;
			}
			// zdoc /zdoc /wiki IndexTable http://dtri.com/img index.xml
			else if (args.length == 5) {
				String indexName = args[2];
				String imgAddress = args[3];
				doc.toGoogleWikiFolder(src, dest, indexName, imgAddress, args[4]);
				return;
			}
		}
		out.println("Wrong parameters!!!");
		out.println(Strings.dup('-', 80));
		out.println(Lang.readAll(Streams.fileInr("org/nutz/doc/hlp.man")));
		out.println(Strings.dup('-', 80));
	}

	private RenderLogger logger() {
		return new RenderLogger(new OutputStreamWriter(out));
	}

	private void toHtmlFolder(File src, File dest, String suffix, String indexXml)
			throws IOException {
		FolderParser parser = new ZDocFolderParser(indexXml);
		FolderRender render = new HtmlFolderRender(suffix, logger());
		Node<ZFolder> folder = parser.parse(src);
		render.render(dest, folder);
	}

	private void toGoogleWikiFolder(File src,
									File dest,
									String indexName,
									String imgAddress,
									String indexXml) throws IOException {
		FolderParser parser = new ZDocFolderParser(indexXml);
		FolderRender render = new GoogleWikiFolderRender(indexName, imgAddress, logger());
		Node<ZFolder> folder = parser.parse(src);
		render.render(dest, folder);
	}
}

package org.nutz.doc;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

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

			if (args.length == 3) {
				String suffix = args[2];
				if (suffix.toLowerCase().matches("^[.]htm[l]?$")) {
					doc.toHtmlFolder(src, dest, suffix);
					return;
				}
			} else if (args.length == 4) {
				String indexName = args[2];
				String imgAddress = args[3];
				doc.toGoogleWikiFolder(src, dest, indexName, imgAddress);
				return;
			}
		}
		out.println("Wrong parameters!!!");
		out.println(Strings.dup('-', 80));
		out.println(Lang.readAll(Streams.fileInr("org/nutz/doc/hlp.man")));
		out.println(Strings.dup('-', 80));
	}

	private void toHtmlFolder(File src, File dest, String suffix) throws IOException {
		FolderParser parser = new ZDocFolderParser();
		Writer writer = new OutputStreamWriter(out);
		FolderRender render = new HtmlFolderRender(suffix, writer);
		Node<ZFolder> folder = parser.parse(src);
		render.render(dest, folder);
	}

	private void toGoogleWikiFolder(File src, File dest, String indexName, String imgAddress)
			throws IOException {
		FolderParser parser = new ZDocFolderParser();
		FolderRender render = new GoogleWikiFolderRender(indexName, imgAddress);
		Node<ZFolder> folder = parser.parse(src);
		render.render(dest, folder);
	}
}

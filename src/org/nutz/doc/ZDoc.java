package org.nutz.doc;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.nutz.doc.html.HtmlDocRender;
import org.nutz.doc.html.Tag;
import org.nutz.doc.plain.PlainParser;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.segment.Segment;

import static java.lang.System.*;

public class ZDoc {

	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			error();
		}
		/*
		 * Check src
		 */
		File src = Files.findFile(args[0]);
		if (null == src) {
			err.println("Fail to find source : " + args[0]);
			error();
		}
		src = src.getAbsoluteFile();
		/*
		 * Check dest
		 */
		File dest = new File(args[1]);
		if (!dest.exists()) {
			if (dest.getName().toLowerCase().matches("^(.+[.])(gwiki|pdf|htm[l]?)$")) {
				out.println("Create file : " + dest.getAbsolutePath());
				Files.createNewFile(dest);
			} else {
				out.println("Create dir : " + dest.getAbsolutePath());
				Files.makeDir(dest);
			}
		}
		dest = dest.getAbsoluteFile();
		/*
		 * Do converting
		 */
		if (src.isFile() && dest.isFile()) {
			file2file(src, dest);
		} else if (src.isDirectory() && dest.isFile()) {
			dir2file(src, dest);
		} else if (src.isDirectory() && dest.isDirectory()) {
			if (args.length >= 3) {
				String ext = args[2];
				dir2dir(src, dest, ext);
			} else {
				throw Lang.makeThrow("Lack file extenstion");
			}
		} else {
			err.println("Can not convert file to folder!");
			error();
		}
		out.println("Done!");
	}

	private static class TagHolder {
		Tag tag;
		int depth;
		String home;
	}

	/**
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	private static void dir2dir(File src, final File dest, final String ext) throws IOException {
		final DirSet ds = new DirSet(src, new PlainParser());
		// If find all .html .htm .js .css file and, copy it
		copyResourceFiles(src, dest);
		writeIndexHtml(src, dest, ext, ds);
		final File docCss = Files.findFile(src.getAbsolutePath() + "/zdoc.css");
		final int pos = src.getAbsolutePath().length();
		ds.visitDocs(new DocVisitor() {
			public void visit(Doc doc) {
				if (!doc.hasAuthor())
					doc.setAuthor(ds.getDefaultAuthor());
				File f = new File(dest.getAbsolutePath() + "/"
						+ doc.getFile().getAbsolutePath().substring(pos));
				String path = f.getParent();
				f = new File(path + "/" + Files.getName(f) + ext);
				if (null != docCss)
					doc.attributes().put("css", docCss);
				try {
					doc2file(doc, f);
				} catch (IOException e) {
					throw Lang.wrapThrow(e);
				}
			}
		});
	}

	private static Pattern RSFS = Pattern.compile("^(.*[.])(html|htm|js|css)$",
			Pattern.CASE_INSENSITIVE);

	private static void copyResourceFiles(File src, final File dest) throws IOException {
		File[] rsfs = src.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.equalsIgnoreCase("index.html"))
					return false;
				if (name.equalsIgnoreCase("zdoc.css"))
					return false;
				return RSFS.matcher(name).find();
			}
		});
		for (File rsf : rsfs) {
			File drs = new File(dest.getAbsolutePath() + "/" + rsf.getName());
			Files.copyFile(rsf, drs);
		}
	}

	private static void writeIndexHtml(File src, final File dest, final String ext, DirSet ds)
			throws IOException {
		String html = renderHeadingHtml(src, ext, ds);
		Segment seg = new CharSegment(Lang.readAll(Streams.fileInr(src.getAbsolutePath()
				+ "/index.html")));
		File index = new File(dest.getAbsolutePath() + "/index" + ext);
		try {
			Files.createNewFile(index);
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
		seg.set("html", html);
		Lang.writeAll(Streams.fileOutw(index), seg.toString());
	}

	private static String renderHeadingHtml(File src, final String ext, DirSet ds) {
		final TagHolder th = new TagHolder();
		th.tag = Tag.tag("ul");
		th.depth = 0;
		th.home = src.getAbsolutePath();
		ds.visitFile(new DocFileVisitor() {
			public void visit(File file, String title, int depth) {
				if (th.depth == depth) {
					th.tag.add(createLi(file, title));
				} else if (th.depth < depth) {
					Tag ul = Tag.tag("ul");
					th.tag.add(ul);
					th.tag = ul;
					th.depth = depth;
					th.tag.add(createLi(file, title));
				} else if (th.depth > depth) {
					th.tag = th.tag.parent();
					th.depth = depth;
					th.tag.add(createLi(file, title));
				}
			}

			private Tag createLi(File file, String title) {
				Tag li = Tag.tag("li");
				if (null != file) {
					li.attr("title", file.getAbsolutePath().substring(th.home.length()).replace(
							'\\', '/'));
				}
				if (null != file && file.isFile()) {
					String href = file.getAbsolutePath().substring(th.home.length() + 1).replace(
							'\\', '/');
					int pos = href.lastIndexOf('.');
					href = href.substring(0, pos) + ext;
					li.add(Tag.tag("a").attr("href", href).add(Tag.text(title)));
				} else
					li.add(Tag.text(title)).attr("class", "heading");
				return li;
			}
		});
		Tag tag = th.tag.parent();
		while (null != tag.parent())
			tag = tag.parent();
		String html = tag.toString();
		return html;
	}

	/**
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	private static void dir2file(File src, File dest) throws IOException {
		final File docCss = Files.findFile(src.getAbsolutePath() + "/zdoc.css");
		DirSet ds = new DirSet(src, new PlainParser());
		Doc doc = ds.mergeDocSet();
		if (!doc.hasAuthor())
			doc.setAuthor(ds.getDefaultAuthor());
		if (null != docCss)
			doc.attributes().put("css", docCss);
		doc.setFile(src);
		doc2file(doc, dest);
	}

	/**
	 * @param src
	 * @param dest
	 * @throws IOException
	 * @throws IOException
	 */
	private static void file2file(File src, File dest) throws IOException {
		DocParser parser = new PlainParser();
		Doc doc = parser.parse(src);
		doc.setFile(src);
		doc2file(doc, dest);
	}

	/**
	 * @param doc
	 * @param dest
	 * @throws IOException
	 * @throws IOException
	 */
	private static void doc2file(Doc doc, File dest) throws IOException {
		DocRender render = evalDocRender(dest);
		if (dest.isDirectory())
			Files.deleteDir(dest);
		if (!dest.exists())
			Files.createNewFile(dest);
		// Copy all medias and update media @src
		List<Media> medias = doc.getMedias();
		System.out.printf("doc base: %s\n", doc.getFile());
		for (Media m : medias) {
			if (!m.src().isRelative())
				continue;
			System.out.printf("media: %s => %s\n", m.src().getBasePath(), m.src().getPath());
			File tar = m.src().getFile();
			if (null != tar) {
				String parent = dest.isDirectory() ? dest.getAbsolutePath() : dest.getParent();
				File d = new File(parent + "/" + m.getSrc());
				System.out.printf("copy to: %s\n", d);
				Files.copyFile(tar, d);
			}
		}
		// Trans to dest
		render.render(dest, doc);
	}

	/**
	 * @param dest
	 * @return
	 */
	private static HtmlDocRender evalDocRender(File dest) {
		if (dest.getName().matches("^.*[.]htm[l]?$"))
			return new HtmlDocRender();
		throw Lang.makeThrow("Dont know how to render '%s'", dest.getName());
	}

	/**
	 * Report error
	 */
	private static void error() {
		err.println("Usage:");
		err.println("zdoc [source file|folder] [destination pdf|html|folder] [.ext]");
		exit(0);
	}

}

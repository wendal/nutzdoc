package org.nutz.doc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.nutz.doc.html.HtmlDocRender;
import org.nutz.doc.plain.PlainParser;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

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

	/**
	 * @param src
	 * @param dest
	 */
	private static void dir2dir(File src, final File dest, final String ext) {
		DirSet ds = new DirSet(src, new PlainParser());
		ds.load(".*[.]man");
		final int pos = src.getAbsolutePath().length();
		ds.visitDocs(new DirVisitor() {
			public void visit(Doc doc) {
				File f = new File(dest.getAbsolutePath() + "/"
						+ doc.getFile().getAbsolutePath().substring(pos));
				String path = f.getParent();
				f = new File(path + "/" + Files.getName(f) + ext);
				doc2file(doc, f);
			}
		});
	}

	/**
	 * @param src
	 * @param dest
	 */
	private static void dir2file(File src, File dest) {
		DirSet ds = new DirSet(src, new PlainParser());
		ds.load(".*[.]man");
		Doc doc = ds.mergeDocSet();
		doc2file(doc, dest);
	}

	/**
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	private static void file2file(File src, File dest) throws IOException {
		DocParser parser = new PlainParser();
		InputStream ins = Streams.fileIn(src);
		Doc doc = parser.parse(ins);
		ins.close();
		doc.setFile(src);
		doc2file(doc, dest);
	}

	/**
	 * @param doc
	 * @param dest
	 * @throws IOException
	 */
	private static void doc2file(Doc doc, File dest) {
		DocRender render = evalDocRender(dest);
		try {
			if (dest.isDirectory())
				Files.deleteDir(dest);
			if (!dest.exists())
				Files.createNewFile(dest);
			// Copy all medias and update media @src
			List<Media> medias = doc.getMedias();
			File imgdir = dest.getParentFile();
			for (Media m : medias) {
				File f = m.src().getFile();
				if (null != f) {
					Files.copyFile(f, new File(imgdir.getAbsolutePath() + "/" + f.getName()));
					m.src(Doc.refer(f.getName()));
				}
			}
			// Trans to dest
			OutputStream ops = Streams.fileOut(dest);
			render.render(ops, doc);
			ops.close();
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
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

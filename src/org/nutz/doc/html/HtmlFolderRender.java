package org.nutz.doc.html;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.nutz.doc.FolderRender;
import org.nutz.doc.RenderLogger;
import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.meta.ZDocs;
import org.nutz.doc.meta.ZEle;
import org.nutz.doc.meta.ZFolder;
import org.nutz.doc.meta.ZIndex;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.Streams;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.segment.Segment;
import org.nutz.lang.util.Node;
import org.nutz.lang.util.Tag;

public class HtmlFolderRender implements FolderRender {

	private HtmlDocRender render;
	private String suffix;
	private RenderLogger L;

	public HtmlFolderRender(String suffix, RenderLogger L) {
		render = new HtmlDocRender();
		this.suffix = suffix;
		this.L = L;
	}

	private void copyResourceFiles(	final File dest,
									File src,
									final List<File> csss,
									final List<File> jss) throws IOException {
		L.log2("Check : %s", dest);
		if (Files.makeDir(dest))
			L.log2("[OK] It don't existed, create it!");
		else
			L.log2("[KO] It alread existed!");

		// File files
		L.log2("Finding resource file ...");
		File[] fs = src.listFiles(new FileFilter() {
			public boolean accept(File f) {
				if (f.isFile()) {
					String name = f.getName();
					if (name.matches("^.*[.]css$")) {
						csss.add(f);
						return true;
					}
					if (name.matches("^.*[.]js$")) {
						jss.add(f);
						return true;
					}
					return name.toLowerCase().matches("^(.*[.])(html|htm)$");
				}
				return false;
			}
		});
		L.log2("Found %d", fs.length);
		// Copy
		for (File f : fs) {
			File newFile = new File(dest.getAbsolutePath() + "/" + f.getName());
			L.log2("%s => %s", f, newFile);
			Files.copyFile(f, newFile);
		}
		// Find sub Folders
		fs = src.listFiles(new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory())
					if (f.getName().charAt(0) == '.')
						return false;
					else
						return true;
				return false;
			}
		});
		// Copy -^ recuring...
		for (File f : fs) {
			File newFile = new File(dest.getAbsolutePath() + "/" + f.getName());
			copyResourceFiles(newFile, f, csss, jss);
		}
	}

	/**
	 * @param dest
	 *            - show be a directory
	 */
	public void render(File dest, Node<ZFolder> root) throws IOException {
		if (dest.isFile())
			throw Lang.makeThrow("Dest: '%' should be a directory!", dest);
		Stopwatch sw = new Stopwatch();
		L.log1("Rending zdoc from : %s", dest);
		sw.start();
		// It just copy the html/js/css, for the image files, we will copy
		// them later. Only copy image files refered by ZDoc file.
		List<File> csss = new LinkedList<File>();
		List<File> jss = new LinkedList<File>();
		L.log1("Copy resource files...");
		copyResourceFiles(dest, root.get().getDir(), csss, jss);
		renderFolderNode(dest, root, csss, jss);
		renderIndexHtml(dest, root);
		sw.stop();
		L.log1("All finised in %s", sw.toString());
	}

	private void renderIndexHtml(File dest, Node<ZFolder> root) {
		// And then, let's check index.html existed in the source directory root
		Segment indexHtml = findIndexHtml(dest);
		// If has indexHtml, then try to fill it's ${html} by root folder node
		if (null != indexHtml && indexHtml.contains("html")) {
			L.log1("Rendering index.html ... ");
			Node<ZIndex> node = ZFolder.toIndex(root);
			// Update all links, make the extenstion to suffix
			Iterator<Node<ZIndex>> it = node.iterator();
			while (it.hasNext()) {
				Node<ZIndex> zi = it.next();
				if (zi.get().hasHref()) {
					zi.get().setHref(Files.renameSuffix(zi.get().getHref(), suffix));
				}
			}
			// rendering tags
			Tag tag = render.renderIndexTable(node);
			indexHtml.set("html", tag);
			File f = new File(dest.getAbsolutePath() + "/index.html");
			Lang.writeAll(Streams.fileOutw(f), indexHtml.toString());
		} else {
			L.log1("Without index.html !");
		}
	}

	private Segment findIndexHtml(File dest) {
		File f = new File(dest.getAbsolutePath() + "/index.html");
		if (f.exists()) {
			String s = Lang.readAll(Streams.fileInr(f));
			return new CharSegment(s);
		}
		return null;
	}

	private void renderFolderNode(File dest, Node<ZFolder> node, List<File> csss, List<File> jss)
			throws IOException {
		L.log1("<Folder: '%s'>", node.get().getDir());
		if (!node.hasChild() && !node.get().hasDoc()) {
			L.log2("Empty!");
			return;
		}
		File f;
		// Rendering all ZDoc under current folder
		for (ZDoc doc : node.get().docs()) {
			L.log2("[Doc: '%s']", doc.getSource().getName());
			doc.setAttr("css", csss);
			doc.setAttr("js", jss);
			File src = doc.getSource();
			int pos = src.getAbsolutePath().length() + 1;
			// Replace links
			List<ZEle> links = doc.root().getLinks();
			L.log3("Found %d links", links.size());
			for (ZEle link : links) {
				f = link.getHref().getFile();
				if (null != f)
					if (f.getAbsolutePath().length() > pos) {
						String path = f.getAbsolutePath().substring(pos);
						String newPath = Files.renameSuffix(path, suffix);
						L.log4(" %s => %s", path, newPath);
						link.setHref(ZDocs.refer(newPath));
					}
			}
			// Write HTML to file
			L.log3("write HTML");
			String s = render.render(doc).toString();
			String name = Files.renameSuffix(src, suffix).getName();
			File newDocFile = new File(dest.getAbsolutePath() + "/" + name);
			Lang.writeAll(Streams.fileOutw(newDocFile), s);
			// Copy Images && change the image to new path
			List<ZEle> images = doc.root().getImages();
			L.log3("Found %d images", images.size());
			for (ZEle img : images) {
				f = img.getSrc().getFile();
				if (null != f) {
					String path = doc.getRelativePath(f);
					File newImg = new File(newDocFile.getParent() + "/" + path);
					L.log4("Copy: %s => %s", f, newImg);
					Files.copyFile(f, newImg);
					img.setSrc(ZDocs.refer(doc.getRelativePath(f)));
					L.log4("update src to: %s", img.getSrc().toString());
				}
			}
		}
		// Recuring sub-folders
		for (Node<ZFolder> sub : node.getChildren()) {
			File dir = new File(dest.getAbsolutePath() + "/" + sub.get().getDir().getName());
			renderFolderNode(dir, sub, csss, jss);
		}
	}
}

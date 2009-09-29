package org.nutz.doc.html;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.nutz.doc.FolderRender;
import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.meta.ZDocs;
import org.nutz.doc.meta.ZEle;
import org.nutz.doc.meta.ZFolder;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.util.Node;

public class HtmlFolderRender implements FolderRender {

	private HtmlDocRender render;
	private String suffix;

	public HtmlFolderRender(String suffix) {
		render = new HtmlDocRender();
		this.suffix = suffix;
	}

	private static void copyResourceFiles(final File dest, File src, final List<File> csss, final List<File> jss)
			throws IOException {
		Files.makeDir(dest);
		// File files
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
		// Copy
		for (File f : fs) {
			File newFile = new File(dest.getAbsolutePath() + "/" + f.getName());
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
		if (dest.exists())
			Files.deleteDir(dest);
		// It just copy the html/js/css, for the image files, we will copy
		// them later. Only copy image files refered by ZDoc file.
		List<File> csss = new LinkedList<File>();
		List<File> jss = new LinkedList<File>();
		copyResourceFiles(dest, root.get().getDir(), csss, jss);
		renderFolderNode(dest, root, csss, jss);
	}

	private void renderFolderNode(File dest, Node<ZFolder> node, List<File> csss, List<File> jss) throws IOException {
		File f;
		// Rendering all ZDoc under current folder
		for (ZDoc doc : node.get().docs()) {
			doc.setAttr("css", csss);
			doc.setAttr("js", jss);
			File src = doc.getSource();
			int pos = src.getAbsolutePath().length() + 1;
			// Replace links
			List<ZEle> links = doc.root().getLinks();
			for (ZEle link : links) {
				f = link.getHref().getFile();
				if (null != f)
					if (f.getAbsolutePath().length() > pos) {
						String path = f.getAbsolutePath().substring(pos);
						String newPath = Files.renameSuffix(path, suffix);
						link.setHref(ZDocs.refer(newPath));
					}
			}
			// Write HTML to file
			String s = render.render(doc).toString();
			String name = Files.renameSuffix(src, suffix).getName();
			f = new File(dest.getAbsolutePath() + "/" + name);
			Lang.writeAll(Streams.fileOutw(f), s);
			// Copy Images && change the image to new path
			List<ZEle> images = doc.root().getImages();
			for (ZEle img : images) {
				f = img.getSrc().getFile();
				if (null != f) {
					if (f.getAbsolutePath().length() > pos) {
						String path = f.getAbsolutePath().substring(pos);
						File newImg = new File(dest.getAbsolutePath() + "/" + path);
						Files.copyFile(f, newImg);
					}
					img.setSrc(ZDocs.refer(doc.getRelativePath(f)));
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

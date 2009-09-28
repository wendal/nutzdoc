package org.nutz.doc.html;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

import org.nutz.doc.FolderRender;
import org.nutz.doc.meta.ZDoc;
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

	private static void copyResourceFiles(final File dest, File src) throws IOException {
		Files.makeDir(dest);
		// File files
		File[] fs = src.listFiles(new FileFilter() {
			public boolean accept(File f) {
				if (f.isFile())
					return f.getName().toLowerCase().matches("^(.*[.])(html|htm|js|css)$");
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
		for (File f : fs)
			copyResourceFiles(new File(dest.getAbsolutePath() + "/" + f.getName()), f);
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
		copyResourceFiles(dest, root.get().getDir());
		renderFolderNode(dest, root);
	}

	private void renderFolderNode(File dest, Node<ZFolder> node) throws IOException {
		// Rendering all ZDoc under current folder
		for (ZDoc doc : node.get().docs()) {
			File src = doc.getSource();
			// Replace links
			List<ZEle> links = doc.root().getLinks();

			// Write HTML to file
			String s = render.render(doc).toString();
			String name = Files.renameSuffix(src, suffix).getName();
			File f = new File(dest.getAbsolutePath() + "/" + name);
			Lang.writeAll(Streams.fileOutw(f), s);
			// Copy Image
			List<ZEle> images = doc.root().getImages();

		}
		// Recuring sub-folders
		for (Node<ZFolder> sub : node.getChildren()) {
			File dir = new File(dest.getAbsolutePath() + "/" + sub.get().getDir().getName());
			renderFolderNode(dir, sub);
		}
	}

}

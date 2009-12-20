package org.nutz.doc.googlewiki;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.nutz.doc.FolderRender;
import org.nutz.doc.RenderLogger;
import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.meta.ZDocs;
import org.nutz.doc.meta.ZEle;
import org.nutz.doc.meta.ZFolder;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.Node;

import static java.lang.String.*;

/**
 * 
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 */
public class GoogleWikiFolderRender implements FolderRender {

	public GoogleWikiFolderRender(String indexName, String imgAddress, RenderLogger L) {
		if (!indexName.endsWith(".wiki")) {
			this.indexName = indexName + ".wiki";
		} else {
			this.indexName = indexName;
		}
		this.imgAddress = imgAddress;
		this.L = L;
	}

	private RenderLogger L;
	private static final String INDENT = "  ";

	private String imgAddress;
	private String indexName;
	private File imgDir;
	StringBuilder indexStr;
	private File rootDir;

	private static final String INDEX_PTN = "%s * [%s %s]\n";
	private static final String INDEX_PTN_TXT = "%s * %s\n";

	public void render(File dest, Node<ZFolder> root) throws IOException {
		L.log1("Generate wiki => %s", dest);
		L.log2("from: %s", root.get().getDir());

		this.rootDir = root.get().getDir();

		Stopwatch sw = new Stopwatch();
		sw.start();

		// in dest directory, generate the "wiki_imgs" folder
		imgDir = new File(dest.getAbsolutePath() + "/wiki_imgs");
		Files.makeDir(imgDir);

		// Looping each folder to geneate wiki doc, and make index
		indexStr = new StringBuilder();

		renderFolder(dest, root.get(), 0);

		Iterator<Node<ZFolder>> it = root.iterator();
		while (it.hasNext()) {
			Node<ZFolder> node = it.next();
			ZFolder folder = node.get();
			int depth = node.depth();
			renderDirIndex(dest, folder, depth);
			renderFolder(dest, folder, depth);
		}

		// generate the index wiki page
		File indexFile = Files.getFile(dest, indexName);
		L.log2(" Index @ ", indexFile);
		Lang.writeAll(Streams.fileOutw(indexFile), indexStr.toString());

		sw.stop();

		L.log1("Done : %s", sw.toString());
	}

	private void renderFolder(File dest, ZFolder folder, int depth) throws IOException {
		L.log2("[%s] : %s", folder.getDir().getName(), folder.getDir().getParent());
		if (folder.hasFolderDoc()) {
			L.log3("=> Folder doc: ", folder.getFolderDoc().getSource());
			renderDoc(dest, folder.getFolderDoc());
		}
		for (ZDoc doc : folder.docs()) {
			String wikiName = renderDoc(dest, doc);
			indexStr
					.append(format(INDEX_PTN, Strings.dup(INDENT, depth), wikiName, doc.getTitle()));
		}
	}

	private void renderDirIndex(File dest, ZFolder folder, int depth) throws IOException {
		// Render folder doc
		if (folder.hasFolderDoc()) {
			File folderDoc = folder.getFolderDoc().getSource();
			String folderDocPath = getDocWikiFileName(folderDoc);
			renderDoc(dest, folder.getFolderDoc());
			// Generate String
			indexStr.append(format(INDEX_PTN, Strings.dup(INDENT, depth - 1), folderDocPath, folder
					.getTitle()));
		} else {
			indexStr
					.append(format(INDEX_PTN_TXT, Strings.dup(INDENT, depth - 1), folder.getTitle()));
		}
	}

	private String renderDoc(File dest, ZDoc doc) throws IOException {
		L.log3(" %s", Files.getMajorName(doc.getSource()));
		/*
		 * copy all availiable image to it change all image refer, to the new
		 * address
		 */
		for (ZEle ele : doc.root().getImages()) {
			File f = ele.getSrc().getFile();
			if (null != f && f.exists()) {
				// Copy image to "wiki_imgs" folder
				Files.copyFile(f, Files.getFile(imgDir, f.getName()));
				// update src
				ele.setSrc(ZDocs.refer(imgAddress + "/" + f.getName()));
			}
		}
		/*
		 * update all links, if it link to a zdoc, make it to wiki name
		 */
		for (ZEle ele : doc.root().getLinks()) {
			File f = ele.getHref().getFile();
			if (null != f && f.exists()) {
				ele.setHref(ZDocs.refer(getDocWikiFileName(f)));
			}
		}
		// render doc
		String wikiName = getDocWikiFileName(doc.getSource());
		GoogleWikiDocRender docRender = new GoogleWikiDocRender();
		String wikiContent = docRender.render(doc).toString();
		File wikiFile = Files.getFile(dest, wikiName + ".wiki");
		if (!wikiFile.exists())
			Files.createNewFile(wikiFile);
		Lang.writeAll(Streams.fileOutw(wikiFile), wikiContent);
		// Return the name
		return wikiName;
	}

	private String getDocWikiFileName(File f) {
		String name = Disks.getRelativePath(rootDir, f);
		int pos = name.lastIndexOf('.');
		if (pos > 0)
			name = name.substring(0, pos);
		return name.replace('/', '_');
	}
}

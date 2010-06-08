package org.nutz.doc.googlewiki;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.nutz.doc.FolderRender;
import org.nutz.doc.RenderLogger;
import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.meta.ZDocSet;
import org.nutz.doc.meta.ZDocs;
import org.nutz.doc.meta.ZEle;
import org.nutz.doc.meta.ZItem;
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

	public void render(File dest, ZDocSet set) throws IOException {
		L.log1("Generate wiki => %s", dest);
		L.log2("from: %s", set.getSrc());

		this.rootDir = set.checkSrcDir();

		Stopwatch sw = new Stopwatch();
		sw.start();

		// in dest directory, generate the "wiki_imgs" folder
		imgDir = new File(dest.getAbsolutePath() + "/wiki_imgs");
		Files.makeDir(imgDir);

		// Looping each folder to geneate wiki doc, and make index
		indexStr = new StringBuilder();

		Iterator<Node<ZItem>> it = set.root().iterator();
		while (it.hasNext()) {
			Node<ZItem> node = it.next();
			ZItem item = node.get();
			int depth = node.depth();
			renderDirIndex(dest, item, depth);
			if (item instanceof ZDoc)
				renderDoc(dest, (ZDoc) item);
		}

		// generate the index wiki page
		File indexFile = Files.getFile(dest, indexName);
		L.log2(" Index @ ", indexFile);
		Lang.writeAll(Streams.fileOutw(indexFile), indexStr.toString());

		sw.stop();

		L.log1("Done : %s", sw.toString());
	}

	private void renderDirIndex(File dest, ZItem zi, int depth) throws IOException {
		// Render folder doc
		if (zi instanceof ZDoc) {
			String folderDocPath = getDocWikiFileName(((ZDoc) zi).getSource());
			renderDoc(dest, (ZDoc) zi);
			// Generate String
			indexStr.append(format(	INDEX_PTN,
									Strings.dup(INDENT, depth - 1),
									folderDocPath,
									zi.getTitle()));
		} else {
			indexStr.append(format(INDEX_PTN_TXT, Strings.dup(INDENT, depth - 1), zi.getTitle()));
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
				ele.setHref(ZDocs.refer(getDocWikiFileName(f.getAbsolutePath())));
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

	private String getDocWikiFileName(String filePath) {
		String name = Disks.getRelativePath(rootDir, Files.findFile(filePath));
		int pos = name.lastIndexOf('.');
		if (pos > 0)
			name = name.substring(0, pos);
		return name.replace('/', '_');
	}
}

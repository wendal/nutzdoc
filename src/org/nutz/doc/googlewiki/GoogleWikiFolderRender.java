package org.nutz.doc.googlewiki;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.nutz.doc.FolderRender;
import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.meta.ZDocs;
import org.nutz.doc.meta.ZEle;
import org.nutz.doc.meta.ZFolder;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Node;

import static java.lang.String.*;

/**
 * 
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 */
public class GoogleWikiFolderRender implements FolderRender {

	private static final String INDENT = "  ";

	private String imgAddress;
	private String indexName;
	private File imgDir;
	StringBuilder indexStr;

	public GoogleWikiFolderRender(String indexName, String imgAddress) {
		if (!indexName.endsWith(".wiki")) {
			this.indexName = indexName + ".wiki";
		} else {
			this.indexName = indexName;
		}
		this.imgAddress = imgAddress;
	}

	private static final String INDEX_PTN = "%s * [%s %s]\n";
	private static final String INDEX_PTN_TXT = "%s * %s\n";

	public void render(File dest, Node<ZFolder> root) throws IOException {
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
		Lang.writeAll(Streams.fileOutw(Files.getFile(dest, indexName)), indexStr.toString());
	}

	private void renderFolder(File dest, ZFolder folder, int depth) throws IOException {
		if (folder.hasFolderDoc()) {
			renderDoc(dest, folder.getFolderDoc());
		}
		for (ZDoc doc : folder.docs()) {
			renderDoc(dest, doc);
			indexStr.append(format(INDEX_PTN, Strings.dup(INDENT, depth), Files.getMajorName(doc
					.getSource()), doc.getTitle()));
		}
	}

	private void renderDirIndex(File dest, ZFolder folder, int depth) throws IOException {
		// Render folder doc
		if (folder.hasFolderDoc()) {
			String folderDocName = folder.getFolderDoc().getSource().getName();
			folderDocName = Files.getMajorName(folderDocName);
			renderDoc(dest, folder.getFolderDoc());
			// Generate String
			indexStr.append(format(INDEX_PTN, Strings.dup(INDENT, depth - 1), folderDocName, folder
					.getTitle()));
		} else {
			indexStr
					.append(format(INDEX_PTN_TXT, Strings.dup(INDENT, depth - 1), folder.getTitle()));
		}
	}

	private void renderDoc(File dest, ZDoc doc) throws IOException {
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
				ele.setHref(ZDocs.refer(Files.getMajorName(f)));
			}
		}
		// render doc
		GoogleWikiDocRender docRender = new GoogleWikiDocRender();
		String wiki = docRender.render(doc).toString();
		String docName = Files.getMajorName(doc.getSource());
		File wikiFile = Files.getFile(dest, docName + ".wiki");
		if (!wikiFile.exists())
			Files.createNewFile(wikiFile);
		Lang.writeAll(Streams.fileOutw(wikiFile), wiki);
	}
}

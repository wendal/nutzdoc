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

	private String imgAddress;
	private String indexName;
	private File imgDir;

	public GoogleWikiFolderRender(String indexName, String imgAddress) {
		if (!indexName.endsWith(".wiki")) {
			this.indexName = indexName + ".wiki";
		} else {
			this.indexName = indexName;
		}
		this.imgAddress = imgAddress;
	}

	private static final String INDEX_PTN = "%s # [%s %s]\n";
	private static final String INDEX_PTN_TXT = "%s # [%s]\n";

	public void render(File dest, Node<ZFolder> root) throws IOException {
		// in dest directory, generate the "wiki_imgs" folder
		imgDir = new File(dest.getAbsolutePath() + "/wiki_imgs");
		Files.makeDir(imgDir);
		Iterator<Node<ZFolder>> it = root.iterator();
		// Looping each folder to geneate wiki doc, and make index
		StringBuilder indexStr = new StringBuilder();
		String indent = "  ";
		while (it.hasNext()) {
			Node<ZFolder> node = it.next();
			int depth = node.depth();
			ZFolder folder = node.get();
			// Render folder doc
			if (folder.getFolderDoc() != null) {
				String folderDocName = "_" + folder.getDir().getName();
				renderDoc(dest, folder.getFolderDoc(), folderDocName);
				// Generate String
				indexStr.append(format(INDEX_PTN, Strings.dup(indent, depth - 1), folderDocName,
						folder.getTitle()));
			} else {
				indexStr.append(format(INDEX_PTN_TXT, Strings.dup(indent, depth - 1), folder
						.getTitle()));
			}

			for (ZDoc doc : folder.docs()) {
				String docName = Files.getMajorName(doc.getSource());
				renderDoc(dest, doc, docName);
				indexStr.append(format(INDEX_PTN, Strings.dup(indent, depth), Files
						.getMajorName(doc.getSource()), doc.getTitle()));
			}
		}
		// generate the index wiki page
		Lang.writeAll(Streams.fileOutw(dest.getAbsolutePath() + "/" + indexName), indexStr
				.toString());
	}

	private void renderDoc(File dest, ZDoc doc, String docName) throws IOException {
		/*
		 * copy all availiable image to it change all image refer, to the new
		 * address
		 */
		for (ZEle ele : doc.root().getImages()) {
			File f = ele.getSrc().getFile();
			if (null != f && f.exists()) {
				// Copy image to "wiki_imgs" folder
				Files.copyFile(f, new File(imgDir.getAbsolutePath() + "/" + f.getName()));
				// update src
				ele.setSrc(ZDocs.refer(imgAddress + "/" + f.getName()));
			}
		}
		/*
		 * update all links, if it link to a zdoc, make it to wiki name
		 */
		for (ZEle ele : doc.root().getLinks()) {
			File f = ele.getSrc().getFile();
			if (null != f && f.exists()) {
				ele.setHref(ZDocs.refer(Files.getMajorName(f)));
			}
		}
		// render doc
		GoogleWikiDocRender docRender = new GoogleWikiDocRender();
		String wiki = docRender.render(doc).toString();
		File wikiFile = new File(getDocPath(dest, docName));
		if (!wikiFile.exists())
			Files.createNewFile(wikiFile);
		Lang.writeAll(Streams.fileOutw(wikiFile), wiki);
	}

	private String getDocPath(File dest, String docName) {
		return dest.getAbsolutePath() + "/" + docName + ".wiki";
	}
}

package org.nutz.doc.zdoc;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;

import org.nutz.doc.DocParser;
import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.meta.ZDocs;
import org.nutz.doc.meta.ZFolder;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.util.Node;
import org.nutz.lang.util.Nodes;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class FolderParsing {

	private File home;
	private DocParser parser;

	FolderParsing(File home) {
		this.home = home;
		this.parser = new ZDocFileParser();
	}

	Node<ZFolder> parse() throws IOException {
		try {
			return eval(Nodes.create(ZFolder.create().setDir(home)));
		} catch (SAXException e) {
			throw Lang.wrapThrow(e);
		} catch (ParserConfigurationException e) {
			throw Lang.wrapThrow(e);
		}
	}

	private Node<ZFolder> eval(Node<ZFolder> node) throws SAXException, IOException,
			ParserConfigurationException {
		File xml = new File(node.get().getDir().getAbsolutePath() + "/index.xml");
		/*
		 * Current folder had index.xml, we need parse the folder under it's
		 * configuration
		 */
		if (xml.exists()) {
			Element ele = Lang.xmls().parse(xml).getDocumentElement();
			// set the attributes
			node.get().setTitle(ele.getAttribute("title"));
			node.get().setAuthor(ZDocs.author(ele.getAttribute("author")));
			// load children
			NodeList nl = ele.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				org.w3c.dom.Node item = nl.item(i);
				if (item instanceof Element)
					evalWithElement((Element) item, node);
			}
		}
		/*
		 * Just read all.man or.zdoc file and then recur all its sub folder
		 */
		else {
			File[] files = node.get().getDir().listFiles();
			Arrays.sort(files);
			for (File f : files) {
				if (f.isDirectory()) {
					if (f.getName().startsWith("."))
						continue;
					node.add(eval(Nodes.create(ZFolder.create().setDir(f))));
				} else if (f.isFile()) {
					if (f.getName().toLowerCase().matches("^(.+[.])(man|zdoc)")) {
						String s = Lang.readAll(Streams.fileInr(f));
						ZDoc doc = parser.parse(s).setSource(f);
						node.get().append(doc);
					}
				}
			}
		}
		return node;
	}

	private void evalWithElement(Element ele, Node<ZFolder> node) {
		String path = ele.getAttribute("path");
		if (null != path) {
			File dir = node.get().getDir();
			File f = new File(dir.getAbsolutePath() + "/" + path);
			if (f.exists()) {
				if (f.isFile()) {
					// Virtual Folder
					if (ele.hasChildNodes()) {
						Node<ZFolder> nd = toFolderNode(dir);
						nd.get().setFolderDoc(toZDoc(f));
						walkingOnChildren(ele, node, nd);
					}
					// Just append ZDoc
					else {
						node.get().append(toZDoc(f));
					}
				} else if (f.isDirectory()) {
					if (ele.hasChildNodes()) {
						Node<ZFolder> nd = toFolderNode(f.getAbsoluteFile());
						nd.get().setTitle(f.getName());
						walkingOnChildren(ele, node, nd);
					}
				}
			}
		}
	}

	private Node<ZFolder> toFolderNode(File dir) {
		Node<ZFolder> nd = Nodes.create(ZFolder.create().setDir(dir));
		return nd;
	}

	private ZDoc toZDoc(File f) {
		String s = Lang.readAll(Streams.fileInr(f));
		ZDoc folderDoc = parser.parse(s).setSource(f.getAbsoluteFile());
		return folderDoc;
	}

	private void walkingOnChildren(Element ele, Node<ZFolder> node, Node<ZFolder> newNode) {
		node.add(newNode);
		NodeList nl = ele.getChildNodes();
		// eval all children
		for (int i = 0; i < nl.getLength(); i++) {
			org.w3c.dom.Node item = nl.item(i);
			if (item instanceof Element)
				if (((Element) item).getTagName() == "doc")
					evalWithElement((Element) item, newNode);
		}
	}

}

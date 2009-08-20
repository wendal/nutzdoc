package org.nutz.doc;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DirSet {

	private static final String ZDOC_NAME_REG = "(.*[.])([mM][aA][nN]|[zZ][dD][oO][cC])";

	private DocParser parser;
	private File home;
	private Dir root;
	private DirDoc dirdoc;
	private Map<File, DirDoc> mapDDs;
	private Map<File, Doc> mapDocs;
	private String indexTable;
	private Document xml;

	public DirSet(File home, DocParser parser) {
		mapDDs = new HashMap<File, DirDoc>();
		mapDocs = new HashMap<File, Doc>();
		this.parser = parser;
		this.home = home;
		File dd = new File(home.getAbsolutePath() + "/index.xml");
		if (dd.exists() && dd.isFile()) {
			try {
				xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(dd);
				Element ele = xml.getDocumentElement();
				indexTable = DirDoc.attr(ele, "index");
				dirdoc = parserDirDoc(home, ele);
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		}
		this.root = new Dir(this.home);
		this.load2(this.root, ZDOC_NAME_REG);
	}

	public String getDefaultAuthor() {
		return dirdoc.getAuthor();
	}

	private DirDoc parserDirDoc(File dir, Element ele) {
		DirDoc dd = new DirDoc(dir, ele);
		File file = dd.getDocFile();
		mapDDs.put(file, dd);
		NodeList list = ele.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node instanceof Element) {
				DirDoc sdd = parserDirDoc(file, (Element) node);
				dd.addChild(sdd);
			}
		}
		return dd;
	}

	private void load2(Dir dir, String regex) {
		File[] fs = dir.getFile().listFiles();
		for (File f : fs) {
			if (f.isDirectory() && !f.getName().startsWith(".")) {
				Dir sub = new Dir(f);
				dir.dirs().add(sub);
				load2(sub, regex);
			} else if (f.isFile() && f.getName().matches(regex)) {
				Doc doc = parser.parse(f);
				doc.setFile(f.getAbsoluteFile());
				DirDoc dd = mapDDs.get(f);
				if (null != dd) {
					if (doc.hasAuthor())
						doc.setAuthor(dd.getAuthor());
					if (Strings.isBlank(doc.getTitle()))
						doc.setTitle(dd.getTitle());
				}
				dir.docs().add(doc);
				mapDocs.put(f, doc);
			}
		}
	}

	private void updateDocMediaToBasePath(Doc doc) {
		for (Media media : doc.getMedias()) {
			if (!media.src().isRelative())
				continue;
			File mediaFile = media.src().getFile();
			if (null == mediaFile)
				continue;
			String relativePath = mediaFile.getAbsolutePath().substring(
					home.getAbsolutePath().length() + 1);
			media.src(relativePath);
		}
	}

	public Doc mergeDocSet() {
		if (null == dirdoc)
			throw Lang.makeThrow("Lack 'index.xml' in '%s'", home.getAbsolutePath());
		Doc doc = mergeDocSet(dirdoc);
		doc.removeIndexTable();
		if (!Strings.isBlank(indexTable))
			doc.root().addChild(0, Doc.indexTable(indexTable));
		return doc;
	}

	public Doc mergeDocSet(DirDoc dd) {
		File file = dd.getDocFile();
		Doc doc = mapDocs.get(file);
		if (null != doc) {
			updateDocMediaToBasePath(doc);
			return doc;
		}
		doc = new Doc();
		doc.setAuthor(dd.getAuthor());
		doc.setTitle(dd.getTitle());
		for (DirDoc d : dd.children()) {
			Doc sub = mergeDocSet(d);
			Line line = doc2line(sub);
			doc.root().addChild(line);
		}
		return doc;
	}

	private static Line doc2line(Doc doc) {
		List<Inline> inlines = Doc.LIST(Inline.class);
		inlines.add(Doc.inline(doc.getTitle()));
		Line root = Doc.line(inlines);
		if (doc.root().size() > 0)
			for (Line l : doc.root().children) {
				root.addChild(l);
			}
		else
			root.addChild(Doc.line("..."));
		return root;
	}

	public void visitDocs(DocVisitor dv) {
		visitDocs(root, dv);
	}

	private void visitDocs(Dir dir, DocVisitor dv) {
		for (Doc doc : dir.docs()) {
			if(!doc.hasAuthor())
				doc.setAuthor(this.getDefaultAuthor());
			dv.visit(doc);
		}
		for (Dir d : dir.dirs())
			visitDocs(d, dv);
	}

	public void visitXml(ElementVisitor xv) {
		visitXml(xml.getDocumentElement(), xv);
	}

	private void visitXml(Element ele, ElementVisitor xv) {
		xv.visit(ele);
		NodeList nl = ele.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++)
			if (nl.item(i) instanceof Element)
				visitXml((Element) nl.item(i), xv);
	}

	public void visitFile(final DocFileVisitor fv) {
		final File home = this.home;
		visitXml(new ElementVisitor() {
			public void visit(Element ele) {
				String title = DirDoc.attr(ele, "title");
				String path = DirDoc.attr(ele, "path");
				int depth = 0;
				while (ele.getParentNode() != ele.getOwnerDocument()) {
					depth++;
					ele = (Element) ele.getParentNode();
					String myPath = DirDoc.attr(ele, "path");
					if (null != myPath)
						path = myPath + "/" + path;
				}
				path = home.getAbsolutePath() + "/" + path;
				File file = Files.findFile(path);
				Doc doc = null;
				if (Strings.isBlank(title))
					if (null != file) {
						doc = mapDocs.get(file);
						title = null == doc ? file.getName() : doc.getTitle();
					}
				fv.visit(file, title, depth);
			}
		});
	}

}

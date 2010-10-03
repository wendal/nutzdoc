package org.nutz.doc.zdoc;

import java.io.File;

import org.nutz.doc.meta.ZDocSet;
import org.nutz.doc.meta.ZFolder;
import org.nutz.doc.meta.ZItem;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Node;
import org.nutz.lang.util.Nodes;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class IndexXmlSetParing {

	private File indexml;
	private File root;
	private File workDir;
	private ZDocParser docParser;

	public IndexXmlSetParing(File indexml, File root) {
		this.indexml = indexml;
		this.root = root;
		this.docParser = new ZDocParser();
	}

	public void doParse(ZDocSet set) throws Exception {
		// 生成 XML
		Element rootEle = Lang.xmls().parse(indexml).getDocumentElement();
		set.root().get().setTitle(Strings.sNull(rootEle.getAttribute("title"), root.getName()));
		workDir = root;

		// 解析字节点
		parseChildren(rootEle, set.root());
	}

	/**
	 * 根据一个 <b>doc</b> 解析其所有子元素
	 * 
	 * @param ele
	 * @param parentNode
	 */
	private void parseChildren(Element ele, Node<ZItem> parentNode) {
		NodeList children = ele.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			org.w3c.dom.Node childEle = children.item(i);
			if (childEle instanceof Element) {
				if (("doc".equalsIgnoreCase(((Element) childEle).getTagName()))) {
					// 记住就的工作目录，以便恢复
					File oldWorkDir = workDir;
					ZItem zi = parse((Element) childEle);
					Node<ZItem> node = null == zi ? null : Nodes.create(zi);
					// 解析字节点
					parseChildren((Element) childEle, null == node ? parentNode : node);

					// 恢复旧的工作目录
					workDir = oldWorkDir;
					if (null != node)
						parentNode.add(node);
				}
			}
		}
	}

	/**
	 * <ul>
	 * <li>如果没有 path，忽略
	 * <li>如果 path 是目录，生成 ZItem，并改变 workDir
	 * <li>如果 path 是文件，生成 ZDoc
	 * </ul>
	 * 
	 * @param ele
	 * @return
	 */
	private ZItem parse(Element ele) {
		String path = ele.getAttribute("path");
		if (Strings.isBlank(path))
			return null;
		path = workDir.getAbsolutePath() + "/" + path.replace('\\', '/');
		File f = Files.findFile(path);
		// 不存在，抛错
		if (null == f)
			throw Lang.makeThrow("Fail to find '%s'", path);

		ZItem zi;

		// 嗯，这是目录
		if (f.isDirectory()) {
			workDir = f;
			// 仅仅跳过
			if ("true".equalsIgnoreCase(ele.getAttribute("skip"))) {
				zi = null;
			}
			// 生成节点
			else {
				zi = new ZFolder(f.getName());
				zi.setTitle(Strings.sBlank(ele.getAttribute("title"), f.getName()));
			}
		}
		// 这是文件，解析成 ZDoc
		else {
			zi = this.docParser.parse(Files.read(f)).setSource(f.getAbsolutePath());
			appendAuthors(ele, zi);
		}
		return zi;
	}

	private void appendAuthors(Element ele, ZItem zi) {
		// 如果对象没有 Author
		if (!zi.hasAuthor()) {
			// 如果 <doc> 里有 author 属性
			String authors = ele.getAttribute("author");
			if (!Strings.isBlank(authors)) {
				String[] authorArray = Strings.splitIgnoreBlank(authors);
				for (String au : authorArray) {
					zi.addAuthor(au);
				}
			}
			// 如果不是根元素，递归
			else if (ele.getOwnerDocument().getDocumentElement() != ele) {
				appendAuthors((Element) ele.getParentNode(), zi);
			}
		}

	}
}

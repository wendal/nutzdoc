package org.nutz.doc.zdoc;

import java.io.File;

import org.nutz.doc.meta.ZDocSet;
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
		docParser = new ZDocParser();
	}

	public void doParse(ZDocSet set) throws Exception {
		// 生成 XML
		Element rootEle = Lang.xmls().parse(indexml).getDocumentElement();
		set.root().get().setTitle(Strings.sNull(rootEle.getAttribute("title"), root.getName()));
		workDir = root;

		Node<ZItem> node = set.root();

		// 解析
		parseChildren(rootEle, node);
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

		ZItem re;

		// 嗯，这是目录
		if (f.isDirectory()) {
			// 仅仅跳过
			if ("true".equalsIgnoreCase(ele.getAttribute("skip"))) {
				re = null;
			}
			// 生成节点
			else {
				re = new ZItem();
				re.setTitle(Strings.sNull(ele.getAttribute("title"), f.getName()));
			}
		}
		// 这是文件，解析成 ZDoc
		else {
			re = this.docParser.parse(Files.read(f));
		}
		return re;
	}

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

}

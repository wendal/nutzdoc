package org.nutz.doc.pdf;

import java.io.File;
import java.io.IOException;

import org.nutz.doc.DocSetRender;
import org.nutz.doc.meta.ZBlock;
import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.meta.ZDocSet;
import org.nutz.doc.meta.ZEle;
import org.nutz.doc.meta.ZItem;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.Node;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Section;
import com.lowagie.text.pdf.PdfWriter;

/**
 * 它将将把一个 ZDoc 的目录，变成一个 PDF 文件
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class PdfFolderRender implements DocSetRender {

	private PdfHelper helper;

	@Override
	public void render(String dest, ZDocSet set) throws IOException {
		File pdfFile = new File(Disks.normalize(dest));
		if (!pdfFile.exists())
			Files.createNewFile(pdfFile);

		try {
			// 创建 PDF 文档
			Document doc = new Document();
			PdfWriter.getInstance(doc, Streams.fileOut(dest));
			doc.open();

			// 循环遍历所有的文档节点
			int i = 1;
			for (Node<ZItem> child : set.root().getChildren())
				renderToPdf(doc, child, i++);

			// 关闭 PDF 文档
			doc.close();
		}
		catch (DocumentException e) {
			throw Lang.wrapThrow(e, IOException.class);
		}

	}

	/**
	 * 第一层节点的渲染
	 * 
	 * @param pdfdoc
	 * @param node
	 * @param num
	 */
	private void renderToPdf(Document pdfdoc, Node<ZItem> node, int num) {
		ZItem zi = node.get();
		// 渲染自己
		Section section = helper.createSection(num, zi.getTitle(), 1);
		// 如果自己是一个 ZDoc，那么继续渲染这个 Doc 内部
		if (zi instanceof ZDoc)
			renderToSection(section, (ZDoc) zi, node.depth());
		// 渲染自己的子节点
		for (Node<ZItem> child : node.getChildren())
			renderToSection(section, child);
	}

	/**
	 * 第二至 N 层的节点渲染
	 * 
	 * @param section
	 * @param node
	 */
	private void renderToSection(Section section, Node<ZItem> node) {

	}

	/**
	 * 将一个 ZDoc 对象渲染至一个 Section
	 * 
	 * @param section
	 * @param doc
	 */
	private void renderToSection(Section section, ZDoc doc, int depth) {
		for (ZBlock block : doc.root().children()) {
			renderBlockToSection(section, block, depth);
		}
	}

	/**
	 * @param section
	 *            目标段落
	 * @param block
	 *            要渲染的块
	 * @param depth
	 *            当前的 ZDoc 在整个在 DocSet 的深度，每一个 ZBlock 可以计算自己在 ZDoc 中的深度
	 */
	private void renderBlockToSection(Section section, ZBlock block, int depth) {
		/*
		 * 空行
		 */
		if (block.isBlank()) {}
		/*
		 * 分隔线
		 */
		else if (block.isHr()) {}
		/*
		 * 标题
		 */
		else if (block.isHeading()) {
			// 渲染自己
			Section me = helper.addSection(section, block.getText(), block.depth() + depth);
			// 渲染自己的子节点
			for (ZBlock sub : block.children()) {
				renderBlockToSection(me, sub, depth);
			}
		}
		/*
		 * 列表
		 */
		else if (block.isUL() || block.isUL() || block.isCode()) {
			Paragraph p = helper.createP();
			renderBlockToParagraph(p, block);
			section.add(p);
		}
		/*
		 *  代码块
		 */
		else if(block.isCode()){}
		/*
		 * 表格
		 */
		else if (block.isTable()) {}
		/*
		 * 普通段落
		 */
		else if (block.isNormal()) {}
	}

	private static final String[] ULTYPES = {"*", ">", "-"};
	private static final String[] OLTYPES = {null, ")", "]"};

	/**
	 * @param paragraph
	 *            目标段
	 * @param block
	 *            要渲染的块
	 * @param depth
	 *            当前的 ZDoc 在整个在 DocSet 的深度，每一个 ZBlock 可以计算自己在 ZDoc 中的深度
	 */
	private void renderBlockToParagraph(Paragraph paragraph, ZBlock block) {
		/*
		 * 无序列表
		 */
		if (block.isUL()) {
			// 生成 UL 对象
			List ul = helper.createUL();

			// 设置序号样式
			int liDeep = block.countMyTypeInAncestors();
			String symbol = ULTYPES[liDeep >= ULTYPES.length ? ULTYPES.length - 1 : liDeep];
			ul.setListSymbol(symbol);

			// 循环所有的子项目
			renderLiTo(ul, block);

			// 加至段落
			paragraph.add(ul);
		}
		/*
		 * 有序列表
		 */
		else if (block.isUL()) {
			// 生成 OL 对象
			List ol = helper.createOL();

			// 设置序号样式
			int liDeep = block.countMyTypeInAncestors();
			String symbol = OLTYPES[liDeep >= OLTYPES.length ? OLTYPES.length - 1 : liDeep];
			if (null != symbol)
				ol.setPostSymbol(symbol);

			// 循环所有的子项目
			renderLiTo(ol, block);

			// 加至段落
			paragraph.add(ol);
		}
		
	}

	/**
	 * @param paragraph
	 *            目标段
	 * @param block
	 *            要渲染的块
	 * @param depth
	 *            当前的 ZDoc 在整个在 DocSet 的深度，每一个 ZBlock 可以计算自己在 ZDoc 中的深度
	 */
	private void renderLiTo(List list, ZBlock listBlock) {
		for (ZBlock liBlock : listBlock.children()) {
			ListItem li = helper.createLi();
			// 渲染自己
			for (ZEle ele : liBlock.eles())
				renderEleToParagraph(li, ele);
			// 渲染自己的子节点
			for (ZBlock sub : liBlock.children())
				renderBlockToParagraph(li, sub);
		}
	}

	/**
	 * @param paragraph
	 *            目标段
	 * @param ele
	 *            要渲染的元素
	 * @param depth
	 *            当前的 ZDoc 在整个在 DocSet 的深度，每一个 ZBlock 可以计算自己在 ZDoc 中的深度
	 */
	private void renderEleToParagraph(Paragraph paragraph, ZEle ele) {

	}
}

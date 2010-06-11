package org.nutz.doc.pdf;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.nutz.doc.DocSetRender;
import org.nutz.doc.RenderLogger;
import org.nutz.doc.ZDocException;
import org.nutz.doc.meta.ZBlock;
import org.nutz.doc.meta.ZColor;
import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.meta.ZDocSet;
import org.nutz.doc.meta.ZEle;
import org.nutz.doc.meta.ZFont;
import org.nutz.doc.meta.ZItem;
import org.nutz.doc.meta.ZRefer;
import org.nutz.doc.util.Funcs;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.Node;

import com.lowagie.text.Anchor;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Section;
import com.lowagie.text.Table;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.pdf.PdfWriter;

/**
 * 它将将把一个 ZDoc 的目录，变成一个 PDF 文件
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class PdfDocSetRender implements DocSetRender {

	private PdfHelper helper;

	private int maxImgWidth;
	private int maxImgHeight;

	private RenderLogger L;

	public PdfDocSetRender(int maxImgWidth, int maxImgHeight, RenderLogger L) throws ZDocException {
		this.maxImgWidth = maxImgWidth;
		this.maxImgHeight = maxImgHeight;
		this.helper = new PdfHelper();
		this.L = L;
	}

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

			// 创建封面
			Paragraph p = helper.p();
			Font font = helper.font();
			font.setColor(new Color(0, 0, 140));
			font.setSize(30);
			p.add(helper.chunk(set.root().get().getTitle(), font));
			p.setAlignment(Paragraph.ALIGN_MIDDLE | Paragraph.ALIGN_CENTER);
			doc.add(p);
			doc.newPage();

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
	 * @throws DocumentException
	 */
	private void renderToPdf(Document pdfdoc, Node<ZItem> node, int num) throws DocumentException {
		ZItem zi = node.get();
		// 渲染自己
		Section section = helper.section(num, zi.getTitle(), 1);
		// 如果自己是一个 ZDoc，那么继续渲染这个 Doc 内部
		if (zi instanceof ZDoc)
			renderToSection(section, (ZDoc) zi, node.depth());
		// 渲染自己的子节点
		for (Node<ZItem> child : node.getChildren())
			renderToSection(section, child);
		// 添加到 PDF 文档
		pdfdoc.add(section);
	}

	/**
	 * 第二至 N 层的节点渲染
	 * 
	 * @param section
	 * @param node
	 */
	private void renderToSection(Section section, Node<ZItem> node) {
		ZItem zi = node.get();
		// 一个 ZDoc 文本
		if (zi instanceof ZDoc) {
			renderToSection(section, (ZDoc) zi, node.depth());
		}
		// 一个目录
		else {
			Section mySection = helper.addSection(section, zi.getTitle(), node.depth());
			for (Node<ZItem> myNode : node.getChildren())
				renderToSection(mySection, myNode);
		}
	}

	/**
	 * 将一个 ZDoc 对象渲染至一个 Section
	 * 
	 * @param section
	 * @param doc
	 * @param depth
	 *            当前的 ZDoc 在整个在 DocSet 的深度，每一个 ZBlock 可以计算自己在 ZDoc 中的深度
	 */
	private void renderToSection(Section section, ZDoc doc, int depth) {
		// 增加自己
		Section docSection = helper.addSection(section, doc.getTitle(), depth);
		// 增加锚点
		Anchor an = helper.anchor(Funcs.evalAnchorName(Files.getMajorName(doc.getSource())));
		docSection.add(an);
		// 增加自己的字节点
		for (ZBlock block : doc.root().children()) {
			renderBlockToSection(docSection, block, depth);
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
		 * 索引表
		 */
		if (block.hasIndexRange()) {}
		/*
		 * 分隔线
		 */
		else if (block.isHr()) {}
		/*
		 * 标题
		 */
		else if (block.isHeading()) {
			// 渲染自己
			String text = block.getText();
			Section me = helper.addSection(section, text, block.depth() + depth);
			// 渲染自己的子节点
			for (ZBlock sub : block.children()) {
				renderBlockToSection(me, sub, depth);
			}
		}
		/*
		 * 列表 | 代码块
		 */
		else if (block.isUL() || block.isOL() || block.isCode()) {
			Paragraph p = helper.p();
			p.setSpacingBefore(10);
			p.setSpacingAfter(10);
			renderBlockToParagraph(p, block);
			section.add(p);
		}
		/*
		 * 表格
		 */
		else if (block.isTable()) {
			if (block.hasChildren() && block.child(0).hasChildren()) {
				Table table = helper.table(block.child(0).childCount());
				// 循环行
				for (ZBlock row : block.children()) {
					// 循环单元格
					for (ZBlock cell : row.children()) {
						Cell cellObj = helper.cell();
						for (ZEle ele : cell.eles())
							this.renderEleTo(cellObj, ele);
						table.addCell(cellObj);
					}
				}
				// 加至段落
				section.add(table);
			}
		}
		/*
		 * 空行
		 */
		else if (block.isBlank()) {}
		/*
		 * 普通段落
		 */
		else if (block.isNormal()) {
			Paragraph p = helper.normal();
			for (ZEle ele : block.eles())
				renderEleTo(p, ele);
			section.add(p);
		}
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
			List ul = helper.UL();

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
		else if (block.isOL()) {
			// 生成 OL 对象
			List ol = helper.OL();

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
		/*
		 * 代码块
		 */
		else if (block.isCode()) {
			// 创建标题
			Table codeTable = helper.codeTable();
			Cell code = helper.codeCell(block.getTitle());

			// 格式化字符串
			String[] lines = block.getText().split("(\\r)?(\\n)");
			for (String line : lines) {
				// 寻找第一个不是 '\t' 的字符
				int pos = 0;
				for (; pos < line.length(); pos++)
					if (line.charAt(pos) != '\t')
						break;
				// 执行替换
				if (pos > 0)
					line = Strings.dup(' ', 4 * pos) + line.substring(pos);

				// 将代码块格加至段落
				code.add(helper.codeLine(line));

			}

			// 加入段落
			codeTable.addCell(code);
			paragraph.add(codeTable);
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
			ListItem li = helper.LI();
			// 渲染自己
			for (ZEle ele : liBlock.eles())
				renderEleTo(li, ele);
			// 渲染自己的子节点
			for (ZBlock sub : liBlock.children())
				renderBlockToParagraph(li, sub);
			// 加入列表
			list.add(li);
		}
	}

	/**
	 * @param parent
	 *            目标段
	 * @param ele
	 *            要渲染的元素
	 * @param depth
	 *            当前的 ZDoc 在整个在 DocSet 的深度，每一个 ZBlock 可以计算自己在 ZDoc 中的深度
	 */
	private void renderEleTo(TextElementArray parent, ZEle ele) {
		/*
		 * 图片
		 */
		if (ele.isImage()) {
			try {
				ZRefer imgsrc = ele.getSrc();
				// 本地图片
				File imgf = imgsrc.getFile();
				Image img;
				if (null != imgf) {
					img = Image.getInstance(imgf.getAbsolutePath());
				}
				// 外部图片
				else {
					try {
						URL imgurl = new URL(imgsrc.getValue());
						img = Image.getInstance(imgurl);
					}
					catch (Exception e) {
						L.log1("Shit!", e.getMessage());
						return;
					}
				}
				// 设置图片位置
				// img.setAlignment(Image.LEFT | Image.ALIGN_MIDDLE);
				// 如果图片太大，缩小图片
				if (ele.getWidth() > 0) {
					img.scaleAbsolute(ele.getWidth(), ele.getHeight());
				} else if (img.getWidth() > maxImgWidth || img.getHeight() > maxImgHeight)
					img.scaleToFit(maxImgWidth, maxImgHeight);
				parent.add(img);
				return;
			}
			catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		}

		// ~ 那么一定是文字喽 ...
		Font font;
		/*
		 * 链接
		 */
		if (ele.hasHref()) {
			String href = ele.getHref().getPath();
			parent.add(helper.anchor(ele.getText(), href));
			return;
		}
		/*
		 * 普通文字
		 */
		else {
			font = helper.font();
			if (ele.hasStyle() && ele.getStyle().hasFont()) {
				ZFont zfont = ele.getStyle().getFont();
				// 颜色
				if (zfont.hasColor()) {
					ZColor zcolor = zfont.getColor();
					font.setColor(new Color(zcolor.getRed(), zcolor.getGreen(), zcolor.getBlue()));
				}
				// 风格
				int fs = 0;
				if (zfont.isBold())
					fs = fs | Font.BOLD;
				if (zfont.isItalic())
					fs = fs | Font.ITALIC;
				if (zfont.isStrike())
					fs = fs | Font.STRIKETHRU;
				if (zfont.isUnderline())
					fs = fs | Font.UNDERLINE;
				if (fs > 0)
					font.setStyle(fs);
				// SUB 和 SUP
				// TODO 考虑一下
			}
			parent.add(helper.chunk(ele.getText(), font));
		}

	}
}

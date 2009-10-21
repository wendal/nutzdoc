package org.nutz.doc.googlewiki;

import org.nutz.doc.DocRender;
import org.nutz.doc.meta.Author;
import org.nutz.doc.meta.ZBlock;
import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.meta.ZEle;
import org.nutz.doc.meta.ZFont;
import org.nutz.doc.meta.ZIndex;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Node;

/**
 * zDoc to GoogleWiki
 * 
 * @author wendal(wendal1985@gamil.com)
 * @author zozoh(zozohtnt@gmail.com)
 */
public class GoogleWikiDocRender implements DocRender {

	public CharSequence render(ZDoc doc) {
		if (doc == null)
			throw new NullPointerException();
		GoogleWikiBuilder wikiBuilder = new GoogleWikiBuilder();
		wikiBuilder.appendAppInfo();
		wikiBuilder.appendSummary(doc.getTitle());

		// Add Authors
		if (doc.hasAuthor())
			appendAuthors("By:", doc.authors(), wikiBuilder);
		if (doc.hasVierifier())
			appendAuthors("Verify by:", doc.verifiers(), wikiBuilder);

		// Render doc contents
		ZBlock[] ps = doc.root().children();
		for (ZBlock p : ps)
			renderBlock(p, wikiBuilder);

		return wikiBuilder.toString();
	}

	private void renderBlock(ZBlock block, GoogleWikiBuilder wikiBuilder) {
		if (block.isTable()) {
			appendTable(block, wikiBuilder);
		}
		if (block.isHr()) {
			// Unknown
		}
		// #index:
		if (block.hasIndexRange()) {
			// 未知
			Node<ZIndex> indexRoot = block.getDoc().root().buildIndex(block.getIndexRange());
			if (!indexRoot.hasChild())
				return;
			// Tag tag = null;
			// if (indexRoot.firstChild().get().hasNumbers())
			// tag = tag("ul");
			// else
			// tag = tag("ol");
			// tag.attr("class", "zdoc_index_table");
			for (Node<ZIndex> indexNode : indexRoot.getChildren()) {
				renderIndex(indexNode);
			}
		}

		// <OL>
		else if (block.isOL()) {
			for (ZBlock li : block.children()) {
				handleListItem(li, wikiBuilder);
			}
		}
		// <UL>
		else if (block.isUL()) {
			for (ZBlock li : block.children()) {
				handleListItem(li, wikiBuilder);
			}
		}
		// <Pre>
		else if (block.isCode()) {
			wikiBuilder.appendCode2(block.getText());
		}
		// <H1~6>
		else if (block.isHeading()) {
			wikiBuilder.appendHeading(parseEles(block.eles()), block.depth() - 1);
			ZBlock[] ps = block.children();
			for (ZBlock p : ps)
				renderBlock(p, wikiBuilder);
		}
		// <P>
		else {
			wikiBuilder.appendRaw(parseEles(block.eles()));
		}

	}

	private void renderIndex(Node<ZIndex> indexNode) {
		ZIndex index = indexNode.get();
		String str = "";
		// Nubmers
		if (index.hasNumbers()) {
			str = index.getNumberString();
		}
		// Text & Href
		if (index.getHref() != null)
			str += GoogleWikiBuilder.makeURLLink(index.getText(), index.getHref());
		else
			str += index.getText();
		// Children
		if (indexNode.hasChild()) {
			for (Node<ZIndex> sub : indexNode.getChildren())
				renderIndex(sub);
		}
	}

	private void handleListItem(ZBlock li, GoogleWikiBuilder wikiBuilder) {
		int liDeep = li.countMyTypeInAncestors();
		// if(li.isULI()){
		// ;
		// }else{
		//			
		// }
		wikiBuilder.nextLine();
		wikiBuilder.appendRaw(GoogleWikiBuilder.makeListItem(parseEles(li.eles()), liDeep))
				.nextLine();
		if (li.hasChildren()) {
			ZBlock[] ps = li.children();
			for (ZBlock p : ps)
				renderBlock(p, wikiBuilder);
		}

	}

	private static void appendTable(ZBlock p, GoogleWikiBuilder wikiBuilder) {
		ZBlock[] rows = p.children();
		String[][] datas = new String[rows.length][];
		for (int i = 0; i < rows.length; i++) {
			ZBlock row = rows[i];
			ZEle[] con = row.eles();
			String[] rowData = new String[con.length];
			datas[i] = rowData;
			for (int j = 0; j < con.length; j++) {
				rowData[j] = paserEle(con[i]);
			}
		}
		wikiBuilder.appendTable(datas);
	}

	private static String parseEles(ZEle[] els) {
		StringBuilder sb = new StringBuilder();
		for (ZEle ele : els) {
			sb.append(paserEle(ele));
		}
		return sb.toString();
	}

	private static String paserEle(ZEle ele) {
		if (ele.isImage()) {
			return GoogleWikiBuilder.makeImage(ele.getSrc().getValue());
		}
		if (!Strings.isEmpty(ele.getText())) {
			return ele2String(ele);
		}
		return "";
	}

	private static String ele2String(ZEle ele) {
		String text = ele.getText();
		if (ele.hasStyle() && ele.getStyle().hasFont()) {
			ZFont font = ele.getStyle().getFont();
			if (font.isBold())
				text = GoogleWikiBuilder.wrapFont_Bold(text);
			if (font.isItalic())
				text = GoogleWikiBuilder.wrapFont_Italic(text);
			if (font.isSup())
				text = GoogleWikiBuilder.wrapFont_SuperScript(text);
			if (font.isSub())
				text = GoogleWikiBuilder.wrapFont_SubScript(text);
			if (font.isStrike())
				text = GoogleWikiBuilder.wrapFont_Strike(text);
		}
		if (ele.hasHref()) {
			if (ele.getHref().isBookmark()) {
				// 未实现
			} else if (ele.getHref().isAvailable()) {
				text = GoogleWikiBuilder.makeURLLink(text, ele.getHref().getPath());
			}
		}
		return text;
	}

	private static void appendAuthors(String prefix, Author[] authors, GoogleWikiBuilder wikiBuilder) {
		if (authors.length > 0) {
			StringBuilder sb = new StringBuilder();
			for (Author au : authors) {
				sb.append(prefix).append(au.getName()).append(" ");
				String email = au.getEmailString();
				if (!Strings.isBlank(email)) {
					sb.append(email);
				}
				sb.append("\n");
			}
			wikiBuilder.appendCode2(sb.toString());
		}
	}
}

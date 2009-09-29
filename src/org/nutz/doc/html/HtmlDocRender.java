package org.nutz.doc.html;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.nutz.doc.DocRender;
import org.nutz.doc.meta.*;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Node;
import org.nutz.lang.util.Tag;

import static org.nutz.lang.util.Tag.*;

public class HtmlDocRender implements DocRender {

	private static Tag appendAuthorTag(ZDoc doc, Tag ele) {
		appendAuthors(ele, "By:", doc.authors());
		appendAuthors(ele, "Verify by:", doc.verifiers());
		return ele;
	}

	private static void appendAuthors(Tag ele, String prefix, Author[] authors) {
		if (authors.length > 0) {
			ele.add(Tag.tag("em").add(Tag.text(prefix)));
			for (Author au : authors) {
				String email = au.getEmailString();
				ele.add(Tag.tag("b").add(Tag.text(au.getName())));
				if (!Strings.isBlank(email))
					ele.add(Tag.tag("a").attr("href", "mailto:" + email).add(Tag.text("<" + email + ">")));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public CharSequence render(ZDoc doc) throws IOException {
		Tag html = tag("html");
		Tag head = tag("head");
		html.add(head);
		head.add(tag("meta").attr("HTTP-EQUIV", "Content-Type").attr("CONTENT", "text/html; charset=UTF-8"));
		if (!Strings.isBlank(doc.getTitle()))
			head.add(tag("title").add(text(doc.getTitle())));
		// <link rel="stylesheet" type="text/css">
		if (doc.hasAttr("css")) {
			List<File> csss = (List<File>) doc.getAttr("css");
			for (File css : csss) {
				String path = doc.getRelativePath(css);
				head.add(Tag.tag("link").attr("href", path).attr("rel", "stylesheet").attr("type", "text/css"));
			}
		}
		// <script language="javascript">
		if (doc.hasAttr("js")) {
			List<File> jss = (List<File>) doc.getAttr("js");
			for (File js : jss) {
				String path = doc.getRelativePath(js);
				head.add(Tag.tag("script").attr("src", path).attr("language", "Javascript"));
			}
		}
		Tag body = tag("body");
		// Add doc header
		body.add(Tag.tag("div").attr("class", "zdoc_header").add(Tag.text(doc.getTitle())));
		// Add author
		if (doc.hasAuthor())
			body.add(appendAuthorTag(doc, Tag.tag("div").attr("class", "zdoc_author")));

		html.add(body);
		Tag container = tag("div").attr("class", "zdoc_body");
		body.add(container);

		// Add doc footer
		if (doc.hasAuthor())
			body.add(appendAuthorTag(doc, Tag.tag("div").attr("class", "zdoc_footer")));

		// Render doc contents
		ZBlock[] ps = doc.root().children();
		for (ZBlock p : ps)
			renderBlock(container, p);
		/*
		 * At last, we render HTML as string
		 */
		return html.toString();
	}

	void renderBlock(Tag parent, ZBlock block) {
		// <Table>
		if (block.isTable()) {
			Tag tab = tag("table").attr("border", "1");
			tab.attr("cellspacing", "2").attr("cellpadding", "4");
			ZBlock[] rows = block.children();
			for (ZBlock row : rows) {
				Tag tagTr = tag("tr");
				for (ZBlock td : row.children()) {
					tagTr.add(renderToHtmlBlockElement(tag("td"), td.eles()));
				}
				tab.add(tagTr);
			}
			parent.add(tab);
		}
		// <Hr>
		else if (block.isHr()) {
			// parent.add(tag("hr"));
			parent.add(Tag.tag("div").attr("class", "hr"));
		}
		// #index:
		else if (block.hasIndexRange()) {
			// parent.add(renderIndexTable(block.getDoc().buildIndex(block.getIndexRange())));
			Node<ZIndex> indexRoot = block.getDoc().root().buildIndex(block.getIndexRange());
			parent.add(renderIndexTable(indexRoot));
		}
		// <OL>
		else if (block.isOL()) {
			Tag tag = tag("ol");
			for (ZBlock li : block.children()) {
				renderListItem(tag, li);
			}
			parent.add(tag);
		}
		// <UL>
		else if (block.isUL()) {
			Tag tag = tag("ul");
			for (ZBlock li : block.children()) {
				renderListItem(tag, li);
			}
			parent.add(tag);
		}
		// <Pre>
		else if (block.isCode()) {
			parent.add(tag("pre").add(text(block.getText())));
		}
		// <H1~6>
		else if (block.isHeading()) {
			renderHeading(parent, block);
			ZBlock[] ps = block.children();
			for (ZBlock p : ps)
				renderBlock(parent, p);
		}
		// <P>
		else {
			parent.add(renderToHtmlBlockElement(tag("p"), block.eles()));
		}
	}

	Tag renderIndexTable(Node<ZIndex> indexRoot) {
		Tag tag = tag("ul").attr("class", "zdoc_index_table");
		for (Node<ZIndex> indexNode : indexRoot.getChildren()) {
			tag.add(renderIndex(indexNode));
		}
		return tag;
	}

	Tag renderIndex(Node<ZIndex> node) {
		ZIndex index = node.get();
		Tag tag = tag("li");
		tag.add(tag("span").attr("css", "num")).add(text(index.getNumberString()));
		tag.add(tag("a").attr("href", "#" + index.getHeadingId()).add(text(index.getText())));
		if (node.hasChild()) {
			Tag ul = tag("ul");
			for (Node<ZIndex> sub : node.getChildren())
				ul.add(renderIndex(sub));
			tag.add(ul);
		}
		return tag;
	}

	private static final String[] OLTYPES = { "1", "a", "i" };
	private static final String[] ULTYPES = { "disc", "circle", "square" };

	private void renderListItem(Tag tag, ZBlock li) {
		int liDeep = li.countMyTypeInAncestors();
		String[] ss = li.isULI() ? ULTYPES : OLTYPES;
		tag.attr("type", ss[liDeep % ss.length]);
		Tag liTag = renderToHtmlBlockElement(tag("li"), li.eles());
		if (li.hasChildren()) {
			ZBlock[] ps = li.children();
			for (ZBlock p : ps)
				renderBlock(liTag, p);
		}
	}

	private void renderHeading(Tag parent, ZBlock block) {
		Tag hn = tag("h" + (block.depth() + 1));
		hn.add(tag("a").attr("name", block.getId()));
		parent.add(renderToHtmlBlockElement(hn, block.eles()));
	}

	Tag renderParagraph(ZBlock p) {
		return renderToHtmlBlockElement(tag("p"), p.eles());
	}

	Tag renderToHtmlBlockElement(Tag tag, ZEle[] eles) {
		for (ZEle ele : eles)
			tag.add(renderEle(ele));
		return tag;
	}

	Tag renderEle(ZEle ele) {
		Tag tag = null;
		if (ele.isImage()) {
			tag = tag("img").attr("src", ele.getSrc().getValue());
			if (ele.getWidth() > 0)
				tag.attr("width", ele.getWidth());
			if (ele.getHeight() > 0)
				tag.attr("height", ele.getHeight());
			return tag;
		} else if (!Strings.isEmpty(ele.getText())) {
			tag = text(ele.getText());
			if (ele.hasStyle() && ele.getStyle().hasFont()) {
				ZFont font = ele.getStyle().getFont();
				wrapFont(tag, "b", font.isBold());
				wrapFont(tag, "i", font.isItalic());
				wrapFont(tag, "s", font.isStrike());
				wrapFont(tag, "sub", font.isSub());
				wrapFont(tag, "sup", font.isSup());
				wrapFontColor(tag, font);
			}
		}
		if (null == tag) {
			if (ele.hasHref() && ele.getHref().isBookmark())
				return tag("a").attr("name", ele.getHref().getValue());
			return tag;
		}

		if (ele.hasHref())
			if (ele.getHref().isAvailable())
				tag = (Tag) tag("a").attr("href", ele.getHref().getPath()).add(tag);
		return tag;
	}

	private static void wrapFont(Tag tag, String tagName, boolean yes) {
		if (yes)
			tag(tagName).add(tag);
	}

	private static void wrapFontColor(Tag tag, ZFont font) {
		if (font.hasColor())
			tag("span").attr("style", "color:" + font.getColor() + ";").add(tag);
	}
}

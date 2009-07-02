package org.nutz.doc.html;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.nutz.doc.*;
import org.nutz.doc.style.Font;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

import static org.nutz.doc.html.Tag.*;

public class HtmlDocRender implements DocRender {

	@Override
	public void render(Writer writer, Doc doc) {
		new InnerRender(writer, doc).render();
	}

	private static class InnerRender {
		private Writer writer;
		private Doc doc;

		InnerRender(Writer writer, Doc doc) {
			this.writer = writer;
			this.doc = doc;
		}

		void render() {
			Tag html = tag("html");
			if (Strings.isBlank(doc.getTitle()))
				html.add(tag("title").add(text(doc.getTitle())));
			Tag body = tag("body");
			html.add(body);
			Paragraph[] ps = doc.root().getParagraphs();
			for (Paragraph p : ps)
				renderParagraph(body, p);
			try {
				writer.write(html.toString());
			} catch (IOException e) {
				throw Lang.wrapThrow(e);
			}
		}

		void renderParagraph(Tag parent, Paragraph p) {
			if (p.isIndexTable()) {
				parent.add(renderIndexTable((IndexTable) p.line(0)));
			} else if (p.isOrderedList()) {
				Tag tag = tag("ol");
				for (Line l : p.lines()) {
					Tag li = tag("li");
					tag.add(li.add(renderLine(l)));
				}
				parent.add(tag);
			} else if (p.isUnorderedList()) {
				Tag tag = tag("ul");
				for (Line l : p.lines()) {
					Tag li = tag("li");
					tag.add(li.add(renderLine(l)));
				}
				parent.add(tag);
			} else if (p.isCode()) {
				parent.add(tag("pre").add(text(p.line(0).getText())));
			} else if (p.isHeading()) {
				for (Line h : p.lines()) {
					Tag hn = tag("h" + h.deep());
					hn.add(tag("a").attr("name", h.UID()));
					hn.add(text(h.getText()));
					parent.add(hn);
					Tag pTag = tag("p");
					for (Line l : h.children())
						pTag.add(renderLine(l));
					parent.add(pTag);
				}
			} else {
				Tag tag = tag("p");
				for (Line l : p.lines())
					tag.add(renderLine(l));
				parent.add(tag);
			}
		}

		Tag[] renderLine(Line line) {
			List<Tag> tags = Doc.LIST(Tag.class);
			for (Inline inline : line.inlines())
				tags.add(renderInline(inline));
			return tags.toArray(new Tag[tags.size()]);
		}

		Tag renderInline(Inline inline) {
			Tag tag;
			if (inline instanceof Media) {
				tag = tag("img").attr("src", ((Media) inline).getSrc());
			} else {
				tag = text(inline.getText());
				if (inline.hasStyle() && inline.getStyle().hasFont()) {
					Font font = inline.getStyle().getFont();
					tag = wrapFont(tag, "b", font.isBold());
					tag = wrapFont(tag, "i", font.isItalic());
					tag = wrapFont(tag, "s", font.isStrike());
					tag = wrapFont(tag, "sub", font.isSub());
					tag = wrapFont(tag, "sup", font.isSup());
				}
			}
			if (null != inline.getHref())
				tag = wrapAnchor(tag, inline.getHref());
			return tag;
		}

		private static Tag wrapFont(Tag tag, String tagName, boolean yes) {
			if (yes)
				return tag(tagName).add(tag);
			return tag;
		}

		private static Tag wrapAnchor(Tag tag, Refer href) {
			return tag("a").attr("href", href.toString()).add(tag);
		}

		Tag renderIndexTable(IndexTable table) {
			Line indexes = doc.getIndex(table.getLevel());
			Tag tag = tag("div").attr("style", "padding:10pt");
			for (Line l : indexes.children()) {
				tag.add(renderIndex(l));
			}
			return tag;
		}

		Tag renderIndex(Line index) {
			Tag tag = tag("div");
			if (index.deep() > 0)
				tag.attr("style", "margin-left:14pt");
			tag.add(text(index.getText()));
			for (Line sub : index.children())
				tag.add(renderIndex(sub));
			return tag;
		}
	}

}

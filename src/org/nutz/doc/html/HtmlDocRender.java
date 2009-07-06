package org.nutz.doc.html;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

import org.nutz.doc.*;
import org.nutz.doc.style.FontStyle;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

import static org.nutz.doc.html.Tag.*;

public class HtmlDocRender implements DocRender {

	@Override
	public void render(OutputStream ops, Doc doc) {
		try {
			Writer w = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
			new InnerRender(w, doc).render();
		} catch (UnsupportedEncodingException e) {
			throw Lang.wrapThrow(e);
		}
	}

	private static final String[] OLTYPES = { "1", "a", "i" };
	private static final String[] ULTYPES = { "square", "disc", "circle" };

	private static class InnerRender {
		private Writer writer;
		private Doc doc;

		InnerRender(Writer writer, Doc doc) {
			this.writer = writer;
			this.doc = doc;
		}

		void render() {
			Tag html = tag("html");
			if (!Strings.isBlank(doc.getTitle()))
				html.add(tag("title").add(text(doc.getTitle())));
			Tag body = tag("body");
			html.add(body);
			Block[] ps = doc.root().getBlocks();
			for (Block p : ps)
				renderParagraph(body, p);
			try {
				writer.write(html.toString());
				writer.flush();
			} catch (IOException e) {
				throw Lang.wrapThrow(e);
			}
		}

		void renderParagraph(Tag parent, Block p) {
			if (p.isHr()) {
				parent.add(tag("hr"));
			} else if (p.isIndexTable()) {
				parent.add(renderIndexTable((IndexTable) p.line(0)));
			} else if (p.isOrderedList()) {
				Tag tag = tag("ol");
				for (Line l : p.lines()) {
					renderListItem(tag, l);
				}
				parent.add(tag);
			} else if (p.isUnorderedList()) {
				Tag tag = tag("ul");
				for (Line l : p.lines()) {
					renderListItem(tag, l);
				}
				parent.add(tag);
			} else if (p.isCode()) {
				parent.add(tag("pre").add(text(p.line(0).getText())));
			} else if (p.isHeading()) {
				for (Line h : p.lines()) {
					renderHeading(parent, h);
					Block[] pps = h.getBlocks();
					for (Block pp : pps)
						renderParagraph(parent, pp);
				}
			} else {
				Tag tag = tag("p");
				for (Line l : p.lines())
					tag.add(renderLine(l));
				parent.add(tag);
			}
		}

		private void renderListItem(Tag tag, Line l) {
			int liDeep = l.parent().countMyTypes();
			String[] ss = tag.name().equalsIgnoreCase("UL") ? ULTYPES : OLTYPES;
			tag.attr("type", ss[liDeep % ss.length]);
			Tag li = tag("li");
			tag.add(li.add(renderLine(l)));
			if (l.size() > 0) {
				Block[] ps = l.getBlocks();
				for (Block p : ps)
					renderParagraph(li, p);
			}
		}

		private void renderHeading(Tag parent, Line h) {
			Tag hn = tag("h" + h.deep());
			if (h.getDoc().contains(IndexTable.class))
				hn.add(tag("a").attr("name", h.UID()));
			hn.add(text(h.getText()));
			parent.add(hn);
		}

		Tag[] renderLine(Line line) {
			List<Tag> tags = Doc.LIST(Tag.class);
			for (Inline inline : line.inlines()) {
				tags.add(renderInline(inline));
			}
			return tags.toArray(new Tag[tags.size()]);
		}

		Tag renderInline(Inline inline) {
			Tag tag;
			if (inline instanceof Media) {
				tag = tag("img").attr("src", ((Media) inline).getSrc());
			} else {
				tag = text(inline.getText());
				if (inline.hasStyle() && inline.getStyle().hasFont()) {
					FontStyle font = inline.getStyle().getFont();
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
			Line indexes = doc.getIndex(table);
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
			tag.add(tag("a").attr("href", "#" + index.UID()).add(text(index.getText())));
			for (Line sub : index.children())
				tag.add(renderIndex(sub));
			return tag;
		}
	}

}

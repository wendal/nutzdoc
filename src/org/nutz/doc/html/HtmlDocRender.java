package org.nutz.doc.html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.nutz.doc.*;
import org.nutz.doc.style.FontStyle;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;

import static org.nutz.doc.html.Tag.*;

public class HtmlDocRender implements DocRender {

	@Override
	public void render(File dest, Doc doc) {
		Writer w = null;
		try {
			w = new BufferedWriter(Streams.fileOutw(dest));
			new InnerRender(w, doc).render();
			copyCss(dest, doc);
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		} finally {
			if (null != w)
				try {
					w.close();
				} catch (IOException e) {
					throw Lang.wrapThrow(e);
				}
		}
	}

	private void copyCss(File dest, Doc doc) throws IOException {
		if (doc.attributes().get("css") instanceof File) {
			File css = (File) doc.attributes().get("css");
			String cssPath = doc.getRelativePath(css);
			File newCss = new File(dest.getParent() + "/" + cssPath);
			if (!newCss.exists() || newCss.lastModified() != css.lastModified()) {
				Files.copyFile(css, newCss);
			}
		}
	}

	private static final String[] OLTYPES = { "1", "a", "i" };
	private static final String[] ULTYPES = { "disc", "circle", "square" };

	private static class InnerRender {
		private Writer writer;
		private Doc doc;

		InnerRender(Writer writer, Doc doc) {
			this.writer = writer;
			this.doc = doc;
		}

		void render() {
			Tag html = tag("html");
			Tag head = tag("head");
			html.add(head);
			head.add(tag("meta").attr("HTTP-EQUIV", "Content-Type").attr("CONTENT",
					"text/html; charset=UTF-8"));
			if (!Strings.isBlank(doc.getTitle()))
				head.add(tag("title").add(text(doc.getTitle())));
			if (doc.attributes().get("css") instanceof File) {
				File css = (File) doc.attributes().get("css");
				String cssHref = doc.getRelativePath(css);
				head.add(Tag.tag("link").attr("href", cssHref).attr("rel", "stylesheet").attr(
						"type", "text/css"));
			}
			Tag body = tag("body");
			// Add doc header
			body.add(Tag.tag("div").attr("class", "zdoc_header").add(Tag.text(doc.getTitle())));
			// Add author
			if (doc.hasAuthor())
				body.add(appendAuthorTag(Tag.tag("div").attr("class", "zdoc_author")));

			html.add(body);
			Tag container = tag("div").attr("class", "zdoc_body");
			body.add(container);

			// Add doc footer
			if (doc.hasAuthor())
				body.add(appendAuthorTag(Tag.tag("div").attr("class", "zdoc_footer")));

			// Render doc contents
			Block[] ps = doc.root().getBlocks();
			for (Block p : ps)
				renderBlock(container, p);
			try {
				writer.write(html.toString());
				writer.flush();
			} catch (IOException e) {
				throw Lang.wrapThrow(e);
			}
		}

		private Tag appendAuthorTag(Tag ele) {
			String email = doc.getAuthor().getEmailString();
			ele.add(Tag.text("by"));
			ele.add(Tag.tag("b").add(Tag.text(doc.getAuthor().getName())));
			if (!Strings.isBlank(email))
				ele.add(Tag.tag("a").attr("href", "mailto:" + email).add(
						Tag.text("<" + email + ">")));
			return ele;
		}

		void renderBlock(Tag parent, Block block) {
			if (block instanceof Shell) {
				Tag tab = tag("table").attr("border", "1");
				tab.attr("cellspacing", "2").attr("cellpadding", "4");
				ZRow[] rows = ((Shell) block).rows();
				for (ZRow row : rows) {
					Tag tagTr = tag("tr");
					for (Line td : row.children()) {
						tagTr.add(tag("td").add(renderLine(td)));
					}
					tab.add(tagTr);
				}
				parent.add(tab);
			} else if (block.isHr()) {
				parent.add(tag("hr"));
			} else if (block.isIndexTable()) {
				parent.add(renderIndexTable((IndexTable) block.line(0)));
			} else if (block.isOrderedList()) {
				Tag tag = tag("ol");
				for (Line l : block.lines()) {
					renderListItem(tag, l);
				}
				parent.add(tag);
			} else if (block.isUnorderedList()) {
				Tag tag = tag("ul");
				for (Line l : block.lines()) {
					renderListItem(tag, l);
				}
				parent.add(tag);
			} else if (block.isCode()) {
				parent.add(tag("pre").add(text(block.line(0).getText())));
			} else if (block.isHeading()) {
				renderHeading(parent, block);
				Block[] pps = block.getBlocks();
				for (Block pp : pps)
					renderBlock(parent, pp);
			} else {
				Tag tag = tag("p");
				for (Line l : block.lines())
					tag.add(renderLine(l));
				parent.add(tag);
			}
		}

		private void renderListItem(Tag tag, Line l) {
			int liDeep = l.countMyTypeInAncestors();
			String[] ss = tag.name().equalsIgnoreCase("UL") ? ULTYPES : OLTYPES;
			tag.attr("type", ss[liDeep % ss.length]);
			Tag li = tag("li");
			tag.add(li.add(renderLine(l)));
			if (l.size() > 0) {
				Block[] ps = l.getBlocks();
				for (Block p : ps)
					renderBlock(li, p);
			}
		}

		private void renderHeading(Tag parent, Block block) {
			Tag hn = tag("h" + (block.depth() + 1));
			if (block.getDoc().contains(IndexTable.class))
				hn.add(tag("a").attr("name", block.UID()));
			for (Line l : block.lines()) {
				hn.add(renderLine(l));
			}
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
				Media media = (Media) inline;
				tag = tag("img").attr("src", media.getSrc());
				if (media.width() > 0)
					tag.attr("width", media.width());
				if (media.height() > 0)
					tag.attr("height", media.height());
			} else {
				tag = text(inline.getText());
				if (inline.hasStyle() && inline.getStyle().hasFont()) {
					FontStyle font = inline.getStyle().getFont();
					tag = wrapFont(tag, "b", font.isBold());
					tag = wrapFont(tag, "i", font.isItalic());
					tag = wrapFont(tag, "s", font.isStrike());
					tag = wrapFont(tag, "sub", font.isSub());
					tag = wrapFont(tag, "sup", font.isSup());
					tag = wrapFontColor(tag, font);
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

		private static Tag wrapFontColor(Tag tag, FontStyle font) {
			if (font.hasColor())
				return tag("span").attr("style", "color:" + font.getColor() + ";").add(tag);
			return tag;
		}

		private static Tag wrapAnchor(Tag tag, Refer href) {
			return tag("a").attr("href", href.toString()).add(tag);
		}

		Tag renderIndexTable(IndexTable table) {
			Line indexes = doc.getIndex(table);
			Tag tag = tag("ul").attr("class", "zdoc_index_table");
			for (Line l : indexes.children()) {
				tag.add(renderIndex(l));
			}
			return tag;
		}

		Tag renderIndex(Line index) {
			Tag tag = tag("li");
			tag.add(tag("a").attr("href", "#" + index.UID()).add(text(index.getText())));
			if (index.size() > 0) {
				Tag ul = tag("ul");
				for (Line sub : index.children())
					ul.add(renderIndex(sub));
				tag.add(ul);
			}
			return tag;
		}
	}

}

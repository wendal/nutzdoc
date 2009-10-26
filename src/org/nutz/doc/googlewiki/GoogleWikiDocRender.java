package org.nutz.doc.googlewiki;

import java.util.regex.Pattern;

import org.nutz.doc.DocRender;
import org.nutz.doc.meta.Author;
import org.nutz.doc.meta.ZBlock;
import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.meta.ZEle;
import org.nutz.doc.meta.ZFont;
import org.nutz.lang.Strings;

import static java.lang.String.*;

/**
 * zDoc to GoogleWiki
 * 
 * @author wendal(wendal1985@gamil.com)
 * @author zozoh(zozohtnt@gmail.com)
 */
public class GoogleWikiDocRender implements DocRender {

	private StringBuilder sb;

	public CharSequence render(ZDoc doc) {
		sb = new StringBuilder();
		// Render title
		sb.append("#summary ").append(doc.getTitle()).append('\n');

		// Body title
		sb.append("<p align=\"center\"><font size=6><b>").append(doc.getTitle()).append(
				"</b></font></p>\n");
		appendHr();
		// append Authors
		appendAuthors("By", doc.authors());
		appendAuthors("Verify By", doc.verifiers());
		sb.append('\n');

		// Render each block
		for (ZBlock block : doc.root().children()) {
			appendBlock(block);
		}
		return sb;
	}

	private void appendAuthors(String prefix, Author[] authors) {
		if (authors.length > 0) {
			sb.append("<p align=\"right\">");
			sb.append(format(" <font color=\"#AAA\" size=\"1\"> * %s * </font>  ", prefix));
			for (Author au : authors) {
				sb.append("  ");
				if (au.getEmail() != null)
					sb.append(format("*%s* (<font color=\"#080\"> %s </font>)", au.getName(), au
							.getEmail().toString()));
				else
					sb.append(format("*%s*", au.getName()));
			}
			sb.append("</p>");
		}
	}

	private void indent(int lvl) {
		sb.append(Strings.dup("  ", lvl));
	}

	private void appendBlock(ZBlock block) {
		// <TABLE>
		if (block.isTable()) {
			for (ZBlock row : block.children()) {
				sb.append("||");
				for (ZBlock cell : row.children()) {
					appendBlockContent(cell);
					sb.append("||");
				}
				sb.append("\n");
			}
			sb.append("\n");
		}
		// <HR>
		else if (block.isHr()) {
			appendHr();
		}
		// #index:
		else if (block.hasIndexRange()) {
			sb.append("<wiki:toc max_depth=\"");
			sb.append(block.getIndexRange().getRight() + 1);
			sb.append("\"/>\n");
		}
		// <Pre>
		else if (block.isCode()) {
			sb.append("{{{\n");
			sb.append(block.getText());
			sb.append("}}}\n\n");
		}
		// <OL>
		else if (block.isOL() || block.isUL()) {
			for (ZBlock li : block.children()) {
				indent(li.depth() - 1);
				if (li.isULI())
					sb.append("  * ");
				else if (li.isOLI())
					sb.append("  # ");
				appendBlockContent(li);
				sb.append('\n');
				for (ZBlock child : li.children())
					appendBlock(child);
			}
		}
		// <H1~6>
		else if (block.isHeading()) {
			int depth = block.depth();
			String ss = Strings.dup('=', depth);
			sb.append(ss);
			sb.append(block.getText());
			sb.append(ss);
			sb.append('\n');
			for (ZBlock child : block.children())
				appendBlock(child);
		}
		// <P>
		else {
			// Loop eles
			appendBlockContent(block);
			sb.append("\n\n");
		}

	}

	private void appendHr() {
		sb.append("----\n");
	}

	private void appendBlockContent(ZBlock block) {
		for (ZEle ele : block.eles())
			sb.append(ele2String(ele));
	}

	private static String wrapBy(String text, String wrapper) {
		return wrapper + text + wrapper;
	}

	private static final Pattern TKN = Pattern.compile("[_*<>{}]|\\x5B|\\x5D|,,|~~|[|][|]");

	private static String ele2String(ZEle ele) {
		String text = ele.getText();
		if (text.indexOf('`') >= 0) {
			text = "{{{" + text + "}}}";
		} else if (TKN.matcher(text).find()) {
			text = "`" + text + "`";
		}
		if (ele.isImage()) {
			if (ele.hasHref()) {
				return format("[%s %s]", ele.getHref().getPath(), ele.getSrc().getPath());
			}
			return ele.getSrc().getPath();
		}
		if (ele.hasHref()) {
			return format("[%s %s]", ele.getHref().getPath(), text);
		}
		if (ele.hasStyle() && ele.getStyle().hasFont()) {
			ZFont font = ele.getStyle().getFont();
			if (font.isBold())
				text = wrapBy(text, "*");
			if (font.isItalic())
				text = wrapBy(text, "_");
			if (font.isSup())
				text = wrapBy(text, "^");
			if (font.isSub())
				text = wrapBy(text, ",,");
			if (font.isStrike())
				text = wrapBy(text, "~~");
			if (font.hasColor())
				text = "<font color=\"" + font.getColor().toString() + "\">" + text + "</font>";
		}
		return text;
	}

}

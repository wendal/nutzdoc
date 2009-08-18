package org.nutz.doc.html;

import java.util.List;
import java.util.regex.Pattern;

import org.nutz.doc.Doc;
import org.nutz.lang.meta.Pair;

import static java.lang.String.*;

public class Tag {

	public static Tag tag(String name) {
		return new Tag(name);
	}

	public static TextTag text(String text) {
		return new TextTag(text);
	}

	public static void main(String[] args) {
		Tag tag = tag("html");
		tag.add(tag("head").add(tag("title").add(text("Test web Page"))));
		Tag body = tag("body");
		body.attr("bgcolor", "#FFC").attr("margin", "4");
		body.add(tag("h3").add(text("headhead!!!")));
		body.add(tag("ul").add(tag("li").add(text("A"))).add(tag("li").add(text("B"))));
		body.add(tag("hr"));
		body.add(tag("div").add(tag("b").add(text("I am bold"))));
		tag.add(body);
		System.out.println(tag);
	}

	private static final Pattern BLOCK = Pattern.compile(
			"^(head|div|p|ul|ol|blockquote|pre|title|h[1-9]|li|hr|table|tr|td)$",
			Pattern.CASE_INSENSITIVE);

	private static final Pattern INLINE = Pattern.compile(
			"^(span|b|i|u|em|strong|sub|sup|code|font)$", Pattern.CASE_INSENSITIVE);

	private static final Pattern NOCHILD = Pattern.compile("^(br|img|link|hr|meta)$",
			Pattern.CASE_INSENSITIVE);

	protected static String INDENTBY = "   ";

	private String name;
	private Tag parent;
	private List<Tag> children;
	private List<Pair> attributes;

	Tag(String name) {
		this.name = name;
		children = Doc.LIST(Tag.class);
		attributes = Doc.LIST(Pair.class);
	}

	public int deep() {
		if (null == parent)
			return 0;
		return parent.deep() + 1;
	}

	public boolean isBlock() {
		return BLOCK.matcher(name).find();
	}

	public boolean isInline() {
		return INLINE.matcher(name).find();
	}

	public boolean isNoChild() {
		return NOCHILD.matcher(name).find();
	}

	public boolean isHtml() {
		return name.equalsIgnoreCase("html");
	}

	public boolean isBody() {
		return name.equalsIgnoreCase("body");
	}

	public boolean isChildAllInline() {
		for (Tag tag : children)
			if (tag.isBlock())
				return false;
		return true;
	}

	List<Tag> children() {
		return children;
	}

	public Tag add(Tag... tags) {
		for (Tag tag : tags) {
			tag.parent = this;
			children.add(tag);
		}
		return this;
	}

	public String name() {
		return name;
	}

	public Tag parent() {
		return parent;
	}

	public Tag attr(String name, String value) {
		Pair attr = findAttr(name);
		if (null == attr) {
			attr = new Pair(name, value);
			attributes.add(attr);
		} else {
			attr.setValue(value);
		}
		return this;
	}

	public Tag attr(String name, int value) {
		return attr(name, String.valueOf(value));
	}

	private Pair findAttr(String name) {
		for (Pair attr : attributes)
			if (attr.getName().equals(name))
				return attr;
		return null;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (isNoChild()) {
			return format("<%s%s/>", name, attributes2String());
		} else if (isInline()) {
			sb.append(format("<%s", name)).append(attributes2String()).append('>');
			for (Tag tag : children)
				sb.append(tag);
			sb.append(format("</%s>", name));
		} else {
			sb.append(format("<%s", name));
			sb.append(attributes2String()).append('>');
			for (Tag tag : children) {
				if (tag.isBlock() || tag.isBody())
					sb.append('\n');
				sb.append(tag);
			}
			if (!this.isChildAllInline())
				sb.append('\n');
			sb.append(format("</%s>", name));
		}
		return sb.toString();
	}

	private String attributes2String() {
		StringBuilder sb = new StringBuilder();
		for (Pair attr : attributes)
			sb.append(' ').append(attr.toString());
		return sb.toString();
	}
}

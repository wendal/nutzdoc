package org.nutz.doc.meta;

import org.nutz.lang.Strings;

public class ZIndex {

	private String text;
	private int[] numbers;
	private String href;

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getText() {
		return text;
	}

	public int[] getNumbers() {
		return numbers;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setNumbers(int[] numbers) {
		this.numbers = numbers;
	}

	public String getNumberString() {
		return getNumberString(1, '.');
	}

	public String getNumberString(int base, char c) {
		if (!hasNumbers())
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i : numbers)
			sb.append(c).append(i + base);
		sb.deleteCharAt(0);
		return sb.toString();
	}

	public boolean hasNumbers() {
		return null != numbers && numbers.length > 0;
	}

	public boolean hasHref() {
		return !Strings.isBlank(href);
	}

	public boolean hasText() {
		return !Strings.isBlank(text);
	}

	public String toString() {
		return String.format("%s - [%s] <%s>", getNumberString(), text, href);
	}

}

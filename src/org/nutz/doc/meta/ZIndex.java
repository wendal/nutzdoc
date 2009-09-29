package org.nutz.doc.meta;

public class ZIndex {

	private String text;
	private int[] numbers;
	private String headingId;

	public String getHeadingId() {
		return headingId;
	}

	void setHeadingId(String headingId) {
		this.headingId = headingId;
	}

	public String getText() {
		return text;
	}

	public int[] getNumbers() {
		return numbers;
	}

	void setText(String text) {
		this.text = text;
	}

	void setNumbers(int[] numbers) {
		this.numbers = numbers;
	}

	public String getNumberString() {
		return getNumberString(1, '.');
	}

	public String getNumberString(int base, char c) {
		if (null == numbers)
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i : numbers)
			sb.append(c).append(i + base);
		sb.deleteCharAt(0);
		return sb.toString();
	}

	public String toString() {
		return String.format("%s - %s", getNumberString(), text);
	}

}

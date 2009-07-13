package org.nutz.doc.style;

import org.nutz.lang.Strings;

public class Color {

	private int red;
	private int green;
	private int blue;

	Color() {}

	Color(String hex) {
		if (hex.charAt(0) == '#')
			hex = hex.substring(1);
		hex = hex.toUpperCase();
		if (hex.length() == 3) {
			String s = Strings.dup(hex.charAt(0), 2);
			s += Strings.dup(hex.charAt(1), 2);
			s += Strings.dup(hex.charAt(2), 2);
			hex = s;
		} else if (hex.length() > 6)
			hex = hex.substring(0, 6);
		else if (hex.length() < 6) {
			hex = "000000";
		}
		red = Integer.valueOf(hex.substring(0, 2), 16);
		green = Integer.valueOf(hex.substring(2, 4), 16);
		blue = Integer.valueOf(hex.substring(4), 16);
	}

	public int getRed() {
		return red;
	}

	public int getGreen() {
		return green;
	}

	public int getBlue() {
		return blue;
	}

	public String toString() {
		return ("#" + Strings.fillHex(red, 2) + Strings.fillHex(green, 2) + Strings
				.fillHex(blue, 2)).toUpperCase();
	}
}

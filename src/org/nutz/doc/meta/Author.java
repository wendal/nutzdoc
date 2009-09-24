package org.nutz.doc.meta;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.lang.Strings;
import org.nutz.lang.meta.Email;

public class Author {

	private static Pattern PTN = Pattern.compile("^(.+)([(])(.+@.+)([)])$",
			Pattern.CASE_INSENSITIVE);

	Author(String str) {
		if (null != str) {
			Matcher m = PTN.matcher(str);
			if (m.find() && m.groupCount() == 4) {
				name = Strings.trim(m.group(1));
				email = new Email(Strings.trim(m.group(3)));
			} else {
				name = Strings.trim(str);
			}
		}
	}

	private String name;

	private Email email;

	public boolean hasEmail() {
		return null != email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Email getEmail() {
		return email;
	}

	public String getEmailString() {
		if (hasEmail())
			return email.toString();
		return null;
	}

	public void setEmail(Email email) {
		this.email = email;
	}

	@Override
	public String toString() {
		if (!hasEmail())
			return name;
		return String.format("%s(%s)", name, email.toString());
	}

}

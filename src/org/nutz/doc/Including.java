package org.nutz.doc;

public class Including extends FinalLine {

	private DocParser parser;

	private Refer refer;

	public Refer getRefer() {
		return refer;
	}

	public void setRefer(Refer refer) {
		this.refer = refer;
	}

	public void setParser(DocParser parser) {
		this.parser = parser;
	}

	public Doc getDoc() {
		return parser.parse(refer.getFile());
	}

	@Override
	public String toString() {
		return String.format("@>include: %s", refer.toString());
	}
}

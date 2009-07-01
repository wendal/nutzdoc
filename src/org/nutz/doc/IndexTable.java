package org.nutz.doc;

public class IndexTable extends FinalLine {

	private int level;

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public String toString() {
		return String.format("#index:%d", level);
	}
	
	

}

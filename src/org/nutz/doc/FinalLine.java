package org.nutz.doc;

public abstract class FinalLine extends Line {

	@Override
	public void addChild(int index, Line block) {
		parent().addChild(index, block);
	}

	@Override
	public void addChild(Line block) {
		parent().addChild(block);
	}
	
}

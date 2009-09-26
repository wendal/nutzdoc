package org.nutz.doc.zdoc;

import org.nutz.doc.meta.ZBlock;
import org.nutz.doc.meta.ZDocs;
import org.nutz.doc.meta.ZEle;

class EleHolder {

	StringBuilder sb;
	ZEle ele;

	public EleHolder() {
		reset();
	}

	void resetAndSaveTo(ZBlock block) {
		save(block);
		reset();
	}

	private void reset() {
		sb = new StringBuilder();
		ele = ZDocs.ele(null);
	}

	void save(ZBlock block) {
		if (sb.length() > 0 || ele.isImage()) {
			ele.setText(sb.toString());
			block.append(ele);
		} else if (ele.hasHref()) {
			ele.setText(ele.getHref().getPath());
			block.append(ele);
		}
	}

}

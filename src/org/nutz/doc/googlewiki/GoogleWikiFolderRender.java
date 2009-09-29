package org.nutz.doc.googlewiki;

import java.io.File;
import java.io.IOException;

import org.nutz.doc.FolderRender;
import org.nutz.doc.meta.ZFolder;
import org.nutz.lang.util.Node;

public class GoogleWikiFolderRender implements FolderRender {

	private String suffix;

	public GoogleWikiFolderRender(String suffix) {
		this.suffix = suffix;
	}

	public void render(File dest, Node<ZFolder> root) throws IOException {}

}

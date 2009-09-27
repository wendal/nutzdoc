package org.nutz.doc.zdoc;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.nutz.doc.meta.ZFolder;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.impl.xs.opti.DefaultElement;

class FolderParsing {

	private File home;

	FolderParsing(File home) {
		this.home = home;
	}

	ZFolder parse() throws IOException {
		ZFolder root = ZFolder.create();
		// Find index.xml
		// If find , then build the DOM
		// else, make a DOM
		File indexXml = new File(home.getAbsolutePath()+"/index.xml");
		Element ele= null;
		if(indexXml.exists()){
			try {
				Document xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(indexXml);
				ele = xml.getDocumentElement();
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		}else{
			ele = new DefaultElement();
		}
		return root;
	}

	private void transform(File dir, ZFolder folder) {
		
	}
}

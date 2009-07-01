package org.nutz.doc.html;

import java.io.Writer;

import org.nutz.doc.*;

public class HtmlDocRender implements DocRender {

	@Override
	public void render(Writer writer, Doc doc) {
		new InnerRender(writer).render(doc);
	}

	private static class InnerRender {
		private Writer writer;

		InnerRender(Writer writer) {
			this.writer = writer;
		}

		void render(Doc doc) {
			
		}
		
		void renderLine(Line line){
			
		}
		
		void renderCode(Code code){
			
		}
		
		void renderIncluding(Including including){
			
		}
		
		void renderInline(Inline inline){
			
		}
		
		void renderMedia(Media media){
			
		}
		
		void renderIndexTable(IndexTable table){
			
		}
	}

}

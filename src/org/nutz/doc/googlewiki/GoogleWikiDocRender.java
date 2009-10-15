package org.nutz.doc.googlewiki;

import org.nutz.doc.DocRender;
import org.nutz.doc.meta.Author;
import org.nutz.doc.meta.ZDoc;
import org.nutz.lang.Strings;

/**
 * zDoc to GoogleWiki
 * @author wendal(wendal1985@gamil.com)
 * @see 
 */
public class GoogleWikiDocRender implements DocRender {

	public CharSequence render(ZDoc doc) {
		if(doc == null) throw new NullPointerException();
		GoogleWikiBuilder wikiBuilder = new GoogleWikiBuilder();
		wikiBuilder.appendAppInfo();
		wikiBuilder.appendSummary(doc.getTitle());
		
		//Add Authors
		if (doc.hasAuthor()){
			appendAuthorTag(doc, wikiBuilder);
		}
		
		return wikiBuilder.toString();
	}
	
	private static void appendAuthorTag(ZDoc doc, GoogleWikiBuilder wikiBuilder) {
		appendAuthors("By:", doc.authors(),wikiBuilder);
		appendAuthors("Verify by:", doc.verifiers(),wikiBuilder);
	}

	private static void appendAuthors(String prefix, Author[] authors,GoogleWikiBuilder wikiBuilder) {
		if (authors.length > 0) {
			StringBuilder sb = new StringBuilder();
			for (Author au : authors) {
				sb.append(prefix).append(au.getName()).append(" ");
				String email = au.getEmailString();
				if (!Strings.isBlank(email)){
					sb.append(email);
				}
				sb.append("\n");
			}
			wikiBuilder.appendCode2(sb.toString());
		}
	}
}

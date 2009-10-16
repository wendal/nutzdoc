package org.nutz.doc.googlewiki;

/**
 * 用于构建GoogleWiki
 * @author wendal(wendal1985@gmail.com)
 */
public final class GoogleWikiBuilder {
	
	private StringBuilder sb = new StringBuilder();
	
	public GoogleWikiBuilder reset(){
		sb = new StringBuilder();
		return this;
	}
	
	public GoogleWikiBuilder appendSummary(String summary){
		sb.append("#summary ").append(summary);
		nextLine();
		return this;
	}

	public GoogleWikiBuilder appendLaberls(String laberls){
		sb.append("#labels ").append(laberls);
		nextLine();
		return this;
	}

	public GoogleWikiBuilder appendSidebar(String sidebar){
		sb.append("#sidebar ").append(sidebar);
		nextLine();
		return this;
	}
	
	public static String wrapFont(String text, String su){
		return su + text + su;
	}

	public static String wrapFont_Italic(String text){
		return wrapFont(text, "_");
	}
	public static String wrapFont_Bold(String text){
		return wrapFont(text, "*");
	}
	public static String wrapFont_SuperScript(String text){
		return wrapFont(text, "^");
	}
	public static String wrapFont_SubScript(String text){
		return wrapFont(text, ",,");
	}
	public static String wrapFont_Strike(String text){
		return wrapFont(text, "~~");
	}
	
	public static String wrapFont_Dividers(String text){
		return "----"+text;
	}
	
	public GoogleWikiBuilder appendItalic(String text){
		sb.append(wrapFont_Italic(text));
		return this;
	}
	
	public GoogleWikiBuilder appendBold(String text){
		sb.append(wrapFont_Bold(text));
		return this;
	}
	
	public GoogleWikiBuilder appendCode(String text){
		sb.append(wrapFont(text, "`"));
		return this;
	}
	
	public GoogleWikiBuilder appendCode2(String text){
		sb.append("{{{").append(text).append("}}}");
		return this;
	}
	
	public GoogleWikiBuilder appendSuperScript(String text){
		sb.append(wrapFont_SuperScript(text));
		return this;
	}
	
	public GoogleWikiBuilder appendSubScript(String text){
		sb.append(wrapFont_SubScript(text));
		return this;
	}
	
	public GoogleWikiBuilder appendStrike(String text){
		sb.append(wrapFont_Strike(text));
		return this;
	}
	
	public GoogleWikiBuilder appendHeading(String text,int level){
		nextLine();
		sb.append(makeHeading(text, level));
		nextLine();
		return this;
	}
	
	public static String makeHeading(String text,int level){
		if(level < 1) level = 1;
		if(level > 6) level = 6;
		String str = "";
		for (int i = 0; i < level; i++) {
			str += "=";
		}
		str = str + text + str;
		return str;
	}
	
	public static String makeListItem(String text,int level){
		if(level < 1) level =1;
		if(level == 1){
			return "*" + text+" ";
		}
		String str = "";
		for (int i = 0; i < level; i++) {
			str +=" ";
		}
		str += "# ";
		str += text;
		return str;
	}
	
	public GoogleWikiBuilder appendTable(String[][] datas){
		for (String[] data : datas) {
			appendTableRow(data);
		}
		return this;
	}
	
	protected GoogleWikiBuilder appendTableRow(String [] data){
		sb.append("||");
		for (int i = 0; i < data.length; i++) {
			sb.append(data[i]).append("||");
		}
		nextLine();
		return this;
	}
	
	public GoogleWikiBuilder appendComment(String text){
		sb.append("<wiki:comment>");
		nextLine();
		sb.append(text);
		nextLine();
		sb.append("</wiki:comment>");
		nextLine();
		return this;
	}
	
	public GoogleWikiBuilder appendGadgets(String url,int height, int border){
		if(height < 1) throw new IllegalArgumentException("height must greater than 0");
		if(border < 0) border = 0;
		sb.append("<wiki:gadget url=\"").append(url).append("\" ");
		sb.append("height=\"").append(height).append("\"/>");
		nextLine();
		return this;
	}
	
	public GoogleWikiBuilder appendVideo(String url){
		sb.append(makeVideo(url));
		nextLine();
		return this;
	}
	
	public static String makeVideo(String url){
		return "<wiki:video url=\""+ url + "\"/>";
	}
	
	public GoogleWikiBuilder appendURLLink(String text,String url){
		sb.append(makeURLLink(text, url));
		return this;
	}
	
	public static String makeURLLink(String text,String url){
		return "[" + text +" " + url + "]";
	}
	
	public static String makeImage(String url){
		return "["+url+"]";
	}
	
	protected GoogleWikiBuilder nextLine(){
		sb.append("\n");
		return this;
	}
	
	public GoogleWikiBuilder appendRaw(String text){
		sb.append(text);
		return this;
	}
	
	public GoogleWikiBuilder appendAppInfo(){
		appendComment("Create by "+getClass().getSimpleName());
		return this;
	}
	
	public String toString(){
		return this.sb.toString();
	}
}

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
	
	public GoogleWikiBuilder appendItalic(String text){
		sb.append("_").append(text).append("_");
		return this;
	}
	
	public GoogleWikiBuilder appendBold(String text){
		sb.append("*").append(text).append("*");
		return this;
	}
	
	public GoogleWikiBuilder appendCode(String text){
		sb.append("`").append(text).append("`");
		return this;
	}
	
	public GoogleWikiBuilder appendCode2(String text){
		sb.append("{{{").append(text).append("}}}");
		return this;
	}
	
	public GoogleWikiBuilder appendHeading(String text,int level){
		if(level < 1) level = 1;
		if(level > 6) level = 6;
		for (int i = 0; i < 6; i++) {
			sb.append("=");
		}
		sb.append(text);
		for (int i = 0; i < 6; i++) {
			sb.append("=");
		}
		nextLine();
		return this;
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
		sb.append("<wiki:video url=\"").append(url).append("\"/>");
		nextLine();
		return this;
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

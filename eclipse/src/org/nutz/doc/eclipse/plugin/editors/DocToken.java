package org.nutz.doc.eclipse.plugin.editors;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class DocToken {

	public static final String TITLE = "#title:";
	public static final String AUTHOR = "#author:";
	public static final String INDEX = "#index:";
	
	public static final String HR = "-----";

	public static final String OLI = "*";
	public static final String ULI = "#";
	
	public static final String[] LINK = {"[","]"};
	public static final String[] CODE = {"{{{","}}}"};
	public static final String[] IMAGE = {"<",">"};
	public static final String[] TABLE = {"||","||"};
	
	/***********************************************************/
	public static final String N_TITLE = "__NutzDoc_Title";
	public static final String N_AUTHOR = "__NutzDoc_Author";
	public static final String N_INDEX = "__NutzDoc_Index";
	
	public static final String N_HR = "__NutzDoc_Hr";

	public static final String N_OLI = "__NutzDoc_oli";
	public static final String N_ULI = "__NutzDoc_uli";
	
	public static final String N_LINK = "__NutzDoc_link";
	public static final String N_CODE = "__NutzDoc_code";
	public static final String N_IMAGE = "__NutzDoc_image";
	public static final String N_TABLE = "__NutzDoc_table";
	
	/***********************************************************/
	public static final IToken Token_TITLE = new Token(N_TITLE);
	public static final IToken Token_AUTHOR = new Token(N_AUTHOR);
	public static final IToken Token_INDEX = new Token(N_INDEX);
	
	public static final IToken Token_HR = new Token(N_HR);

	public static final IToken Token_OLI = new Token(N_OLI);
	public static final IToken Token_ULI = new Token(N_ULI);
	
	public static final IToken Token_LINK = new Token(N_LINK);
	public static final IToken Token_CODE = new Token(N_CODE);
	public static final IToken Token_IMAGE = new Token(N_IMAGE);
	public static final IToken Token_TABLE = new Token(N_TABLE);

}

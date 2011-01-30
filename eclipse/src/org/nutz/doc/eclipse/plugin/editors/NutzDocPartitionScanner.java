package org.nutz.doc.eclipse.plugin.editors;

import org.eclipse.jface.text.rules.*;

public class NutzDocPartitionScanner extends RuleBasedPartitionScanner {
	
	public NutzDocPartitionScanner() {
		
		IPredicateRule[] rules = new IPredicateRule[2];

		rules[0] = new SingleLineRule(DocToken.TITLE, null, DocToken.Token_TITLE);
		rules[1] = new SingleLineRule(DocToken.AUTHOR, null, DocToken.Token_AUTHOR);

		setPredicateRules(rules);
	}
}

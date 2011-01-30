package org.nutz.doc.eclipse.plugin.editors;

import org.eclipse.ui.editors.text.TextEditor;

public class NutzDocEditor extends TextEditor {

	private ColorManager colorManager;

	public NutzDocEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new XMLConfiguration(colorManager));
		setDocumentProvider(new XMLDocumentProvider());
	}
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

}

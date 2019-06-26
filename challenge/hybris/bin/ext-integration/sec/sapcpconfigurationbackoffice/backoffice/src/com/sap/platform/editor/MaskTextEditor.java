/**
 *
 */
package com.sap.platform.editor;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Textbox;

import com.hybris.cockpitng.editors.CockpitEditorRenderer;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;



public class MaskTextEditor implements CockpitEditorRenderer<String>
{
	private static final String TEXT_MASK = "**********";

	@Override
	public void render(final Component parent, final EditorContext<String> context, final EditorListener<String> listener)
	{
		// create UI component
		final Textbox editorView = new Textbox();
		
		if (StringUtils.isNotEmpty(context.getInitialValue()))
		{
			
			maskTextEditor(editorView);
		}
		
		// Add the UI component to the component tree
		editorView.setParent(parent);
		
		// Add events
		editorView.addEventListener(Events.ON_CHANGE, event -> handleOnChange(editorView, listener));

	}

	/**
	 * Method to handle Events.ON_CHANGE.
	 *
	 * @param editorView
	 * @param listener
	 */
	protected void handleOnChange(final Textbox editorView, final EditorListener<String> listener)
	{
		final String result = (String) editorView.getRawValue();
	
		listener.onValueChanged(result);
		
		//mask it after change
		maskTextEditor(editorView);
	}

	/**
	 * Helper method to mask the value for the given editor
	 *
	 * @param editorView
	 */
	protected void maskTextEditor(final Textbox editorView)
	{
		
		editorView.setValue(TEXT_MASK);
	}
}

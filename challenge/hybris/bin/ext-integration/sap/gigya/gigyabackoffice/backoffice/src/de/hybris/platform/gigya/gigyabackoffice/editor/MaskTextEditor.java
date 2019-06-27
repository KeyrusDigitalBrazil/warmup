/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.gigya.gigyabackoffice.editor;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Textbox;

import com.hybris.cockpitng.editors.CockpitEditorRenderer;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;


/**
 * Editor to mask the content of the field
 */
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

/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.rules.backoffice.editors;

import de.hybris.platform.sap.productconfig.rules.model.ProductConfigSourceRuleModel;

import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;

import org.zkoss.zk.ui.Component;

import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.editor.localized.LocalizedEditor;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;


/**
 * Default implementation of the characteristic editor in the product configuration rules
 */
public class ProductConfigLocalizedEditor extends LocalizedEditor
{
	@Resource
	private PermissionFacade permissionFacade;

	@Override
	public void render(final Component parent, final EditorContext editorContext, final EditorListener editorListener)
	{
		final Set<Locale> readableLocales = getPermissionFacade().getReadableLocalesForInstance(new ProductConfigSourceRuleModel());

		final Set<Locale> writableLocales = getPermissionFacade().getWritableLocalesForInstance(new ProductConfigSourceRuleModel());

		final EditorContext<Object> replacedEditorCtx = new EditorContext<>(editorContext.getInitialValue(),
				editorContext.getDefinition(), editorContext.getParameters(), editorContext.getLabels(), readableLocales,
				writableLocales);

		replacedEditorCtx.setEditable(editorContext.isEditable());
		replacedEditorCtx.setValueType(editorContext.getValueType());

		super.render(parent, replacedEditorCtx, editorListener);
	}

	/**
	 * @return the permissionFacade
	 */
	public PermissionFacade getPermissionFacade()
	{
		return permissionFacade;
	}

	/**
	 * @param permissionFacade
	 *           the permissionFacade to set
	 */
	public void setPermissionFacade(final PermissionFacade permissionFacade)
	{
		this.permissionFacade = permissionFacade;
	}
}

/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.addonsupport.controllers.cms;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;


/**
 * Generic Base Class Controller for Addon CMS Components that populates the view model from attributes stores on the
 * CMS Component.
 */
public class GenericCMSAddOnComponentController extends AbstractCMSAddOnComponentController<AbstractCMSComponentModel>
{
	@Resource(name = "modelService")
	private ModelService modelService;

	// Setter required for UnitTests
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final AbstractCMSComponentModel component)
	{
		// See documentation for CMSComponentService.getReadableEditorProperties, but this will return all frontend
		// properties which we just inject into the model.
		for (final String property : getCmsComponentService().getReadableEditorProperties(component))
		{
			try
			{
				final Object value = modelService.getAttributeValue(component, property);
				model.addAttribute(property, value);
			}
			catch (final AttributeNotSupportedException ignore)
			{
				// ignore
			}
		}
	}
}

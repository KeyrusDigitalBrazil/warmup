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
package de.hybris.platform.marketplaceaddon.controllers.cms;

import de.hybris.platform.addonsupport.controllers.cms.AbstractCMSAddOnComponentController;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.marketplaceaddon.constants.MarketplaceaddonConstants;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;


/**
 * Base controller for CMS components used in Marketplace service extension
 */
public class GenericMarketplaceCMSComponentController<T extends AbstractCMSComponentModel>
		extends AbstractCMSAddOnComponentController<T>
{
	@Resource(name = "modelService")
	private ModelService modelService;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final AbstractCMSComponentModel component)
	{
		// See documentation for CMSComponentService.getEditorProperties, but this will return all frontend
		// properties which we just inject into the model.
		for (final String property : getCmsComponentService().getEditorProperties(component))
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

	/*
	 * Force return the add-on otherwise it by default is the extension who generates the component
	 *
	 * @see
	 * de.hybris.platform.addonsupport.controllers.cms.AbstractCMSAddOnComponentController#getAddonUiExtensionName(de.
	 * hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel)
	 */
	@Override
	protected String getAddonUiExtensionName(final T component)
	{
		return MarketplaceaddonConstants.EXTENSIONNAME;
	}
}

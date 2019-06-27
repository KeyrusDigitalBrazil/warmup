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
package de.hybris.platform.sap.sapsubscriptionaddon.controllers.cms;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import de.hybris.platform.addonsupport.controllers.cms.AbstractCMSAddOnComponentController;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.sap.sapsubscriptionaddon.model.components.SubscriptionSearchResultsGridComponentModel;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Controller for Search Results Grid Component
 */
@Controller("SubscriptionSearchResultsGridComponentController")
@RequestMapping(value = "/view/" + SubscriptionSearchResultsGridComponentModel._TYPECODE + "Controller")
public class SubscriptionSearchResultsGridComponentController extends AbstractCMSAddOnComponentController {

	@Resource(name = "modelService")
	private ModelService modelService;

	@Override
	protected void fillModel(HttpServletRequest request, Model model,
			AbstractCMSComponentModel component) {
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
}

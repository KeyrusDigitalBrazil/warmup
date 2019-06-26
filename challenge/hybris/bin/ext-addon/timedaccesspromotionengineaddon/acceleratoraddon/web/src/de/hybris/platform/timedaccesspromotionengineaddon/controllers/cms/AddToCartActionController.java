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
package de.hybris.platform.timedaccesspromotionengineaddon.controllers.cms;

import de.hybris.platform.addonsupport.controllers.cms.GenericCMSAddOnComponentController;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.timedaccesspromotionengineaddon.constants.TimedaccesspromotionengineaddonConstants;
import de.hybris.platform.timedaccesspromotionengineaddon.controllers.TimedaccesspromotionengineaddonControllerConstants;
import de.hybris.platform.timedaccesspromotionenginefacades.FlashBuyFacade;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller("AddToCartActionController")
@RequestMapping(value = TimedaccesspromotionengineaddonControllerConstants.Actions.Cms.AddToCartAction)
public class AddToCartActionController extends GenericCMSAddOnComponentController
{
	@Resource(name = "flashBuyFacade")
	private FlashBuyFacade flashBuyFacade;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final AbstractCMSComponentModel component)
	{
		super.fillModel(request, model, component);

		final ProductData product = (ProductData) request.getAttribute("product");
		model.addAttribute("flashBuyCoupon", flashBuyFacade.prepareFlashBuyInfo(product));
	}

	@Override
	protected String getAddonUiExtensionName(final AbstractCMSComponentModel component)
	{
		return TimedaccesspromotionengineaddonConstants.EXTENSIONNAME;
	}

}

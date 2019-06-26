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
package de.hybris.platform.selectivecartsplitlistaddon.controllers.cms;

import de.hybris.platform.addonsupport.controllers.cms.AbstractCMSAddOnComponentController;
import de.hybris.platform.selectivecartfacades.SelectiveCartFacade;
import de.hybris.platform.selectivecartsplitlistaddon.controllers.SelectivecartsplitlistaddonControllerConstants;
import de.hybris.platform.selectivecartsplitlistaddon.model.components.SaveForLaterCMSComponentModel;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for CMS SaveForLaterComponent.
 */
@Controller("SaveForLaterCMSComponentController")
@RequestMapping(SelectivecartsplitlistaddonControllerConstants.Actions.Cms.SaveForLaterComponent)
public class SaveForLaterCMSComponentController extends AbstractCMSAddOnComponentController<SaveForLaterCMSComponentModel>
{

	@Resource
	private SelectiveCartFacade selectiveCartFacade;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final SaveForLaterCMSComponentModel component)
	{
		model.addAttribute("wishlist2Data", getSelectiveCartFacade().getWishlistForSelectiveCart());
	}

	protected SelectiveCartFacade getSelectiveCartFacade()
	{
		return selectiveCartFacade;
	}

}
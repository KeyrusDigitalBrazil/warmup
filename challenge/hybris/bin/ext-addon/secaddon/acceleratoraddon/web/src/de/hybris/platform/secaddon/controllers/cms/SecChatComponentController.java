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
package de.hybris.platform.secaddon.controllers.cms;

import static de.hybris.platform.secaddon.controllers.SecaddonControllerConstants.Cms.SecChatComponent;

import de.hybris.platform.addonsupport.controllers.cms.AbstractCMSAddOnComponentController;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.secaddon.model.components.SecChatComponentModel;
import de.hybris.platform.servicelayer.user.UserService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller("SecChatComponentController")
@RequestMapping(value = SecChatComponent)
public class SecChatComponentController extends AbstractCMSAddOnComponentController<SecChatComponentModel>
{
	

	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	@Resource(name = "storeSessionFacade")
	private StoreSessionFacade storeSessionFacade;

	@Resource(name = "userService")
	private UserService userService;




	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final SecChatComponentModel component)
	{
		final String customerName = customerFacade.getCurrentCustomer().getName();
		final String customerEmail = customerFacade.getCurrentCustomer().getUid();
		final UserModel user = getUserService().getCurrentUser();
		if (getUserService().isAnonymousUser(user))
		{
			model.addAttribute("customerName", "");
			model.addAttribute("customerEmail", "");

		}
		else
		{

			model.addAttribute("customerName", customerName);
			model.addAttribute("customerEmail", customerEmail);
		}
		model.addAttribute("chatScript", component.getChatScript());
		
	}

	
}

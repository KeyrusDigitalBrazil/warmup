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
package de.hybris.platform.sap.sapcpiorderexchangeb2b.controller;

import static de.hybris.platform.sap.sapcpiorderexchangeb2b.constants.Sapcpiorderexchangeb2bConstants.PLATFORM_LOGO_CODE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.hybris.platform.sap.sapcpiorderexchangeb2b.service.Sapcpiorderexchangeb2bService;


@Controller
public class Sapcpiorderexchangeb2bHelloController
{
	@Autowired
	private Sapcpiorderexchangeb2bService sapcpiorderexchangeb2bService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String printWelcome(final ModelMap model)
	{
		model.addAttribute("logoUrl", sapcpiorderexchangeb2bService.getHybrisLogoUrl(PLATFORM_LOGO_CODE));
		return "welcome";
	}
}

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
package de.hybris.platform.chinesepspalipaymock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class ChinesepspalipaymockController
{
	/**
	 * Returns the alipay landing page
	 *
	 * @return landing page
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String printWelcome()
	{
		return AlipayMockControllerConstants.Pages.IndexPage;
	}
}

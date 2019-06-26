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
package de.hybris.platform.accountsummaryaddon.interceptors;

import de.hybris.platform.acceleratorstorefrontcommons.interceptors.BeforeViewHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;


public class AccountSummaryBeforeViewHandler implements BeforeViewHandler
{

	private static final String OVERRIDING_VIEW_WITH = "Overriding view with ";
	private static final String MY_COMPANY_HOME_PAGE = "pages/company/myCompanyHomePage";
	private static final String MY_COMPANY_OVERRIDE_HOME_PAGE = "addon:/accountsummaryaddon/pages/company/myCompanyHomePage";

	private static final Logger LOG = Logger.getLogger(AccountSummaryBeforeViewHandler.class);

	@Override
	public void beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelAndView modelAndView)
	{
		final String viewName = modelAndView.getViewName();
		if (MY_COMPANY_HOME_PAGE.equals(viewName))
		{
			LOG.info(OVERRIDING_VIEW_WITH + MY_COMPANY_OVERRIDE_HOME_PAGE);
			modelAndView.setViewName(MY_COMPANY_OVERRIDE_HOME_PAGE);
		}
	}
}

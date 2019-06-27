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
package de.hybris.platform.customerticketingc4cintegration.factory;

import de.hybris.platform.customerticketingc4cintegration.facade.C4CTicketFacadeImpl;
import de.hybris.platform.customerticketingc4cintegration.facade.C4CTicketFacadeMock;
import de.hybris.platform.customerticketingfacades.TicketFacade;
import de.hybris.platform.util.Config;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;


/**
 * Factory decides which facade will be used mock/real.
 */
public class TicketFacadeFactory
{
	private C4CTicketFacadeImpl c4cTicketFacadeImpl;
	private C4CTicketFacadeMock c4cTicketFacadeMock;

	/**
	 * @return chosen facade
	 */
	public TicketFacade getTicketFacade()
	{
		final String param = Config.getParameter("customerticketingc4cintegration.facade.mock");
		final boolean mockEnabled = StringUtils.isEmpty(param) ? true : Boolean.parseBoolean(param);
		if (mockEnabled)
		{
			return c4cTicketFacadeMock;
		}
		else
		{
			return c4cTicketFacadeImpl;
		}
	}

	protected C4CTicketFacadeImpl getC4cTicketFacadeImpl()
	{
		return c4cTicketFacadeImpl;
	}

	@Resource
	public void setC4cTicketFacadeImpl(final C4CTicketFacadeImpl c4cTicketFacadeImpl)
	{
		this.c4cTicketFacadeImpl = c4cTicketFacadeImpl;
	}

	protected C4CTicketFacadeMock getC4cTicketFacadeMock()
	{
		return c4cTicketFacadeMock;
	}

	@Resource
	public void setC4cTicketFacadeMock(final C4CTicketFacadeMock c4cTicketFacadeMock)
	{
		this.c4cTicketFacadeMock = c4cTicketFacadeMock;
	}
}

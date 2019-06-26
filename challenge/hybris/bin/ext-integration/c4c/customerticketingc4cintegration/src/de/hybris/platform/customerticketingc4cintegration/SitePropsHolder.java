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
package de.hybris.platform.customerticketingc4cintegration;

import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;

import org.springframework.beans.factory.annotation.Autowired;


/**
 * Hold siteId dynamic property.
 */
public class SitePropsHolder
{
	@Autowired
	private BaseSiteService baseSiteService;

	/**
	 * @return current site id property for c4c-requests.
	 */
	public String getSiteId()
	{
		return Config.getParameter(
				String.format("customerticketingc4cintegration.siteId.%s", baseSiteService.getCurrentBaseSite().getUid()));
	}

	/**
	 * @return boolean
	 */
	public boolean isB2C()
	{
		final String type = Config.getParameter(
				String.format("customerticketingc4cintegration.siteId.%s.type", baseSiteService.getCurrentBaseSite().getUid()));
		return "b2c".equalsIgnoreCase(type);
	}
}

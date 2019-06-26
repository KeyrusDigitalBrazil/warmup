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
package de.hybris.platform.notificationservices.formatters.impl;

import de.hybris.platform.notificationservices.formatters.SiteMessageContentFormatter;


/**
 * Default implementation of {@link SiteMessageContentFormatter}
 */
public class DefaultSiteMessageContentFormatter implements SiteMessageContentFormatter
{

	@Override
	public String format(final String source)
	{
		return source;
	}

}

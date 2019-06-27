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
package de.hybris.platform.acceleratorservices.site.strategies;

import de.hybris.platform.commerceservices.enums.SiteChannel;


/**
 * Strategy interface for site channel validation.
 */
public interface SiteChannelValidationStrategy
{
	/**
	 * Validates the {@link SiteChannel}.
	 * 
	 * @param siteChannel
	 *           the {@link SiteChannel} to validate.
	 * @return <code>true</code> if the {@link SiteChannel} is supported, <code>false</code> otherwise
	 */
	boolean validateSiteChannel(SiteChannel siteChannel);
}

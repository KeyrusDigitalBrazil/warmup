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
package de.hybris.platform.notificationservices.formatters;

/**
 * Formatter to format message content.
 */
public interface SiteMessageContentFormatter
{

	/**
	 * Format source content
	 *
	 * @param source
	 *           the message content source to be formatted
	 * @return the formatted content
	 */
	String format(String source);
}

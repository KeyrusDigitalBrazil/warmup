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
package de.hybris.platform.accountsummaryaddon.document.service;

import java.util.List;

import de.hybris.platform.accountsummaryaddon.document.NumberOfDayRange;

/**
 * Provides services for Past Due Balance Date Range.
 *
 */
public interface PastDueBalanceDateRangeService
{
	/**
	 * Gets a list of number of days ranges.
	 * 
	 * @return date range list
	 */
	List<NumberOfDayRange> getNumberOfDayRange();
}

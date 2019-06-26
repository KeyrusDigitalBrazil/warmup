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
package de.hybris.platform.accountsummaryaddon.document.criteria;

import de.hybris.platform.accountsummaryaddon.utils.AccountSummaryAddonUtils;

import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;


/**
 *
 */
public class DateRangeCriteria extends RangeCriteria
{

	protected Optional<Date> startRange;
	protected Optional<Date> endRange;

	public DateRangeCriteria(final String filterByKey)
	{
		this(filterByKey, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
	}

	public DateRangeCriteria(final String filterByKey, final String startRange, final String endRange, final String documentStatus)
	{
		super(filterByKey, documentStatus);
		this.startRange = AccountSummaryAddonUtils.parseDateToOptional(startRange);
		this.endRange = AccountSummaryAddonUtils.parseDateToOptional(endRange);
	}

	/**
	 * @return the startRange
	 */
	@Override
	public Optional<Date> getStartRange()
	{
		return this.startRange;
	}

	/**
	 * @param startRange
	 *           the startRange to set
	 */
	@Override
	protected void setStartRange(final String startRange)
	{
		this.startRange = AccountSummaryAddonUtils.parseDateToOptional(startRange);
	}

	/**
	 * @return the endRange
	 */
	@Override
	public Optional<Date> getEndRange()
	{
		return this.endRange;
	}

	/**
	 * @param endRange
	 *           the endRange to set
	 */
	@Override
	protected void setEndRange(final String endRange)
	{
		this.endRange = AccountSummaryAddonUtils.parseDateToOptional(endRange);
	}
}

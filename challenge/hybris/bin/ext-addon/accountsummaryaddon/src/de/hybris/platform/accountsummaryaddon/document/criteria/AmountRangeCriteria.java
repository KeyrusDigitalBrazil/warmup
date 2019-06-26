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

import java.math.BigDecimal;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;


/**
 *
 */
public class AmountRangeCriteria extends RangeCriteria
{

	protected Optional<BigDecimal> startRange;
	protected Optional<BigDecimal> endRange;

	public AmountRangeCriteria(final String filterByKey)
	{
		this(filterByKey, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
	}

	public AmountRangeCriteria(final String filterByKey, final String startRange, final String endRange,
			final String documentStatus)
	{
		super(filterByKey, documentStatus);
		this.startRange = AccountSummaryAddonUtils.parseBigDecimalToOptional(startRange);
		this.endRange = AccountSummaryAddonUtils.parseBigDecimalToOptional(endRange);
	}

	/**
	 * @return the startRange
	 */
	@Override
	public Optional<BigDecimal> getStartRange()
	{
		return startRange;
	}

	/**
	 * @param startRange
	 *           the startRange to set
	 */
	@Override
	protected void setStartRange(final String startRange)
	{
		this.startRange = AccountSummaryAddonUtils.parseBigDecimalToOptional(startRange);
	}

	/**
	 * @return the endRange
	 */
	@Override
	public Optional<BigDecimal> getEndRange()
	{
		return endRange;
	}

	/**
	 * @param endRange
	 *           the endRange to set
	 */
	@Override
	protected void setEndRange(final String endRange)
	{
		this.endRange = AccountSummaryAddonUtils.parseBigDecimalToOptional(endRange);
	}
}

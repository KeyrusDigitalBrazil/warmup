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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 *
 */
public class RangeCriteria extends DefaultCriteria
{
	private static final Logger LOG = Logger.getLogger(RangeCriteria.class);

	protected Optional<? extends Object> startRange;
	protected Optional<? extends Object> endRange;

	public RangeCriteria(final String filterByKey)
	{
		this(filterByKey, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
	}

	public RangeCriteria(final String filterByKey, final String documentStatus)
	{
		super(filterByKey, documentStatus);
	}

	public RangeCriteria(final String filterByKey, final String startRange, final String endRange, final String documentStatus)
	{
		super(filterByKey, documentStatus);
		this.startRange = AccountSummaryAddonUtils.toOptional(startRange);
		this.endRange = AccountSummaryAddonUtils.toOptional(endRange);
	}

	/**
	 * @return the startRange
	 */
	public Optional<? extends Object> getStartRange()
	{
		return this.startRange;
	}

	/**
	 * @param startRange
	 *           the startRange to set
	 */
	protected void setStartRange(final String startRange)
	{
		this.startRange = AccountSummaryAddonUtils.toOptional(startRange);
	}

	/**
	 * @return the endRange
	 */
	public Optional<? extends Object> getEndRange()
	{
		return this.endRange;
	}

	/**
	 * @param endRange
	 *           the endRange to set
	 */
	protected void setEndRange(final String endRange)
	{
		this.endRange = AccountSummaryAddonUtils.toOptional(endRange);
	}

	@Override
	public void setCriteriaValues(final FilterByCriteriaData filterByCriteriaData)
	{
		super.setCriteriaValues(filterByCriteriaData);
		this.setStartRange(filterByCriteriaData.getStartRange());
		this.setEndRange(filterByCriteriaData.getEndRange());
	}

	@Override
	public void populateCriteriaQueryAndParamsMap(final List<String> whereQueryList, final Map<String, Object> queryParamsMap)
	{

		if (this.getStartRange().isPresent())
		{
			final String formattedQuery = String.format(RANGE_QUERY, getFilterByKey(), ">", "?startRange");
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format(QUERY_CRITERIA, formattedQuery, this.getStartRange().get()));
			}
			whereQueryList.add(formattedQuery);
			queryParamsMap.put("startRange", this.getStartRange().get());
		}

		if (this.getEndRange().isPresent())
		{
			final String formattedQuery = String.format(RANGE_QUERY, getFilterByKey(), "<", "?endRange");
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format(QUERY_CRITERIA, formattedQuery, this.getEndRange().get()));
			}
			whereQueryList.add(formattedQuery);
			queryParamsMap.put("endRange", this.getEndRange().get());
		}

		super.populateCriteriaQueryAndParamsMap(whereQueryList, queryParamsMap);
	}
}

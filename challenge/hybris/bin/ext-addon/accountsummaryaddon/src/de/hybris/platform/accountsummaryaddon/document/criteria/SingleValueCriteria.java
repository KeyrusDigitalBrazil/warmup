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

import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 *
 */
public class SingleValueCriteria extends DefaultCriteria
{

	private static final Logger LOG = Logger.getLogger(SingleValueCriteria.class);

	protected static final String WILD_CARD = "%";
	protected static final String SINGLE_VALUE_WILDCARD_QUERY = "{" + B2BDocumentModel._TYPECODE + ":%s} LIKE %s ";

	private String filterByValue;


	public SingleValueCriteria(final String filterByKey)
	{
		this(filterByKey, StringUtils.EMPTY, StringUtils.EMPTY);
	}

	public SingleValueCriteria(final String filterByKey, final String filterByValue, final String documentStatus)
	{
		super(filterByKey, documentStatus);
		this.setFilterByValue(filterByValue);
	}

	/**
	 * @return the filterByValue
	 */
	public String getFilterByValue()
	{
		return filterByValue;
	}

	/**
	 * @param filterByValue
	 *           the filterByValue to set
	 */
	protected final void setFilterByValue(final String filterByValue)
	{
		this.filterByValue = filterByValue;
	}

	@Override
	public void setCriteriaValues(final FilterByCriteriaData filterByCriteriaData)
	{
		super.setCriteriaValues(filterByCriteriaData);
		setFilterByValue(filterByCriteriaData.getFilterByValue());
	}

	@Override
	public void populateCriteriaQueryAndParamsMap(final List<String> whereQueryList, final Map<String, Object> queryParamsMap)
	{
		if (StringUtils.isNotBlank(getFilterByValue()))
		{
			final String formattedQuery = String.format(SINGLE_VALUE_WILDCARD_QUERY, getFilterByKey(), "?filterByValue");
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format(QUERY_CRITERIA, formattedQuery, getFilterByValue()));
			}
			whereQueryList.add(formattedQuery);
			queryParamsMap.put("filterByValue", WILD_CARD + getFilterByValue() + WILD_CARD);
		}

		super.populateCriteriaQueryAndParamsMap(whereQueryList, queryParamsMap);
	}

}
